const API = "http://localhost:8080/api/v1";

/**
 * 🔐 Lê o CPF do usuário logado do localStorage.
 * Retorna string vazia caso não exista — o backend trata o header como opcional
 * em rotas de leitura, mas mantemos a chave para padronizar o tráfego.
 */
function getCpfLogado() {
    try {
        const raw = localStorage.getItem("user");
        if (!raw) return "";
        const user = JSON.parse(raw);
        return user?.cpf ?? "";
    } catch {
        return "";
    }
}

/**
 * 🌐 Wrapper de fetch que injeta o header X-User-CPF em toda requisição.
 * Aceita `options` no mesmo formato do fetch nativo.
 */
function fetchComCpf(url, options = {}) {
    const headers = {
        ...(options.headers ?? {}),
        "X-User-CPF": getCpfLogado()
    };
    return fetch(url, { ...options, headers });
}

/**
 * 🔗 Captura o ID do curso a partir dos parâmetros da URL (ex: assistir.html?id=2)
 */
function getCursoId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

let aulaAtual = null;

/**
 * 🎬 Carrega a aula selecionada no player principal da tela
 */
function carregarAula(aula) {
    aulaAtual = aula;

    document.getElementById("video").innerHTML =
        `<iframe width="100%" height="100%" src="${aula.link ?? ""}" frameborder="0" allowfullscreen></iframe>`;

    document.getElementById("tituloAula").innerText = aula.titulo ?? "";
    document.getElementById("descricaoAula").innerText = aula.descricao_aula ?? "";

    const matDiv = document.getElementById("materiais");
    matDiv.innerHTML = "";

    if (aula.materiais && aula.materiais.length > 0) {
        aula.materiais.forEach(m => {
            const a = document.createElement("a");
            a.href = m.link;
            a.innerText = m.nome;
            matDiv.appendChild(a);
        });
    } else {
        matDiv.innerHTML = "<p style='color: #64748b; font-size: 0.9em;'>Nenhum material de apoio para esta aula.</p>";
    }
}

/**
 * 🕒 Carga horária total do curso
 */
async function carregarCargaHoraria(idCurso) {
    try {
        const response = await fetchComCpf(`${API}/curso/curso-horas-totais/${idCurso}`);
        if (!response.ok) throw new Error("Erro ao buscar carga horária");

        const horasTotais = await response.text();
        const horasBadge = document.getElementById("cursoCargaHoraria");
        if (horasBadge) {
            horasBadge.innerHTML =
                `🕒 Carga Horária Total: <span style="background: #1e293b; color: #38bdf8; padding: 2px 6px; border-radius: 4px; margin-left: 5px;">${horasTotais}h</span>`;
        }
    } catch (error) {
        console.error("Não foi possível carregar a carga horária:", error);
        const horasBadge = document.getElementById("cursoCargaHoraria");
        if (horasBadge) horasBadge.style.display = "none";
    }
}

/**
 * 🔍 Busca detalhes completos de uma aula via /aula/{idCurso}/{idModulo}/{idAula}.
 * O endpoint /modulos-aulas devolve apenas id_aula e titulo; aqui completamos
 * link_do_video e descrição.
 */
async function buscarDetalheAula(idCurso, idModulo, aulaResumo) {
    try {
        const res = await fetchComCpf(
            `${API}/aula/${idCurso}/${idModulo}/${aulaResumo.id_aula}`,
            { method: "GET" }
        );
        if (!res.ok) throw new Error("Falha ao buscar aula " + aulaResumo.id_aula);
        const detalhe = await res.json();

        return {
            ...aulaResumo,
            id_curso: idCurso,
            id_modulo: idModulo,
            link: detalhe.link_do_video,
            descricao_aula: detalhe.descricao
        };
    } catch (err) {
        console.error(err);
        return { ...aulaResumo, id_curso: idCurso, id_modulo: idModulo };
    }
}

/**
 * 🔍 Busca detalhes completos de um módulo via /modulo/{idCurso}/{idModulo}.
 * Preenche cargaHoraria e descrição que vêm nulos no /modulos-aulas.
 */
async function buscarDetalheModulo(idCurso, item) {
    const idModulo = item.modulo.id_modulo;

    let moduloCompleto = { ...item.modulo, id_curso: idCurso };
    try {
        const res = await fetchComCpf(`${API}/modulo/${idCurso}/${idModulo}`);
        if (res.ok) {
            const detalhe = await res.json();
            moduloCompleto = {
                ...moduloCompleto,
                titulo: detalhe.titulo ?? moduloCompleto.titulo,
                cargaHoraria: detalhe.carga_horaria,
                descricao_curso: detalhe.descricao
            };
        }
    } catch (err) {
        console.error("Falha ao buscar módulo " + idModulo, err);
    }

    const aulas = await Promise.all(
        (item.aulas ?? []).map(a => buscarDetalheAula(idCurso, idModulo, a))
    );

    return { modulo: moduloCompleto, aulas };
}

/**
 * 📦 Renderiza a árvore de Módulos e Aulas na sidebar
 */
function renderSidebar(modulos) {
    const container = document.getElementById("modulosContainer");
    if (!container) return;

    container.innerHTML = "";

    modulos.forEach(item => {
        const modDiv = document.createElement("div");
        modDiv.className = "modulo";

        modDiv.innerHTML = `<h3>📦 ${item.modulo.titulo ?? "(sem título)"}</h3>`;

        if (item.aulas && item.aulas.length > 0) {
            item.aulas.forEach(aula => {
                const aulaDiv = document.createElement("div");
                aulaDiv.className = "aula";
                aulaDiv.innerText = aula.titulo ?? "(sem título)";

                aulaDiv.onclick = () => {
                    carregarAula(aula);
                    document.querySelectorAll(".aula").forEach(a => a.classList.remove("ativa"));
                    aulaDiv.classList.add("ativa");
                };

                modDiv.appendChild(aulaDiv);
            });
        }

        container.appendChild(modDiv);
    });
}

/**
 * 🔄 Orquestra: pega o esqueleto em /modulos-aulas e enriquece cada módulo/aula
 * com chamadas individuais.
 */
async function inicializarCurso() {
    const idCurso = getCursoId();

    if (!idCurso) {
        const container = document.getElementById("modulosContainer");
        if (container) {
            container.innerHTML = "<p style='padding:15px; color: #ef4444;'>Erro: Nenhum ID de curso foi especificado na URL.</p>";
        }
        return;
    }

    await carregarCargaHoraria(idCurso);

    try {
        const response = await fetchComCpf(`${API}/curso/${idCurso}/modulos-aulas`);
        if (!response.ok) throw new Error(`Erro HTTP! Status: ${response.status}`);

        const esqueleto = await response.json();

        // Enriquece todos os módulos em paralelo
        const modulos = await Promise.all(
            esqueleto.map(item => buscarDetalheModulo(idCurso, item))
        );

        renderSidebar(modulos);

        if (modulos.length > 0 && modulos[0].aulas && modulos[0].aulas.length > 0) {
            carregarAula(modulos[0].aulas[0]);

            setTimeout(() => {
                const primeiraAula = document.querySelector(".aula");
                if (primeiraAula) primeiraAula.classList.add("ativa");
            }, 50);
        } else {
            const container = document.getElementById("modulosContainer");
            if (container) {
                container.innerHTML = "<p style='padding:15px; color: #94a3b8;'>Nenhuma aula disponível para este curso.</p>";
            }
        }

    } catch (error) {
        console.error("Não foi possível carregar os módulos do backend:", error);
        const container = document.getElementById("modulosContainer");
        if (container) {
            container.innerHTML = "<p style='padding:15px; color: #ef4444;'>Erro ao carregar os módulos. Verifique o servidor.</p>";
        }
    }
}

/* 🚀 Inicialização automática */
inicializarCurso();
