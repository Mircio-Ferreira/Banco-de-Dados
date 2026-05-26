const API = "http://localhost:8080/api/v1";

let cursoId;
let modulos = [];           // estado em memória
let modulosOriginais = [];  // snapshot para detectar removidos

// ---------- Inicialização ----------

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    cursoId = params.get("id");

    if (!cursoId) {
        setStatus("err", "Curso inválido (parâmetro ?id ausente).");
        return;
    }

    try {
        const curso = await getJson(`${API}/curso/${cursoId}`);
        const esqueleto = await getJson(`${API}/curso/${cursoId}/modulos-aulas`);

        // O endpoint /modulos-aulas devolve módulos/aulas com a maioria dos campos null.
        // Enriquecemos cada item com /modulo/{idCurso}/{idModulo} e /aula/{idCurso}/{idModulo}/{idAula}.
        const modulosCompletos = await Promise.all(
            (esqueleto ?? []).map(item => enriquecerItem(cursoId, item))
        );

        preencherCurso(curso);
        carregarModulos(modulosCompletos);
        renderModulos();
    } catch (err) {
        console.error(err);
        setStatus("err", "Erro ao carregar curso: " + err.message);
    }
});

async function enriquecerItem(idCurso, item) {
    const idModulo = item.modulo?.id_modulo;

    let detalheModulo = {};
    try {
        if (idModulo != null) {
            detalheModulo = await getJson(`${API}/modulo/${idCurso}/${idModulo}`);
        }
    } catch (err) {
        console.error("Falha ao detalhar módulo " + idModulo, err);
    }

    const aulas = await Promise.all(
        (item.aulas ?? []).map(async aulaResumo => {
            try {
                if (aulaResumo.id_aula == null || idModulo == null) return aulaResumo;
                const detalhe = await getJson(
                    `${API}/aula/${idCurso}/${idModulo}/${aulaResumo.id_aula}`
                );
                return {
                    ...aulaResumo,
                    id_curso: Number(idCurso),
                    id_modulo: idModulo,
                    titulo: detalhe.titulo ?? aulaResumo.titulo,
                    link: detalhe.link_do_video,
                    descricao_aula: detalhe.descricao
                };
            } catch (err) {
                console.error("Falha ao detalhar aula " + aulaResumo.id_aula, err);
                return aulaResumo;
            }
        })
    );

    return {
        modulo: {
            ...item.modulo,
            id_curso: Number(idCurso),
            titulo: detalheModulo.titulo ?? item.modulo?.titulo,
            cargaHoraria: detalheModulo.carga_horaria ?? item.modulo?.cargaHoraria,
            descricao_curso: detalheModulo.descricao ?? item.modulo?.descricao_curso
        },
        aulas
    };
}

function preencherCurso(curso) {
    document.getElementById("nomeCurso").value = curso.nome_curso ?? "";
    document.getElementById("precoCurso").value = curso.preco ?? "";
    document.getElementById("descricaoCurso").value = curso.descricao_curso ?? "";

    const catList = document.getElementById("categoriaList");
    catList.innerHTML = "";
    (curso.categorias ?? []).forEach(cat => adicionarCategoriaUI(cat.nome));
}

function carregarModulos(modulosAulas) {
    modulos = (modulosAulas ?? []).map(item => ({
        id_modulo: item.modulo?.id_modulo ?? null,
        titulo: item.modulo?.titulo ?? "",
        descricao: item.modulo?.descricao_curso ?? "",
        carga_horaria: item.modulo?.cargaHoraria ?? "",
        aulas: (item.aulas ?? []).map(a => ({
            id_aula: a.id_aula ?? null,
            titulo: a.titulo ?? "",
            descricao: a.descricao_aula ?? "",
            link_do_video: a.link ?? ""
        }))
    }));

    modulosOriginais = JSON.parse(JSON.stringify(modulos));
}

// ---------- Categorias ----------

function adicionarCategoriaUI(nome) {
    const list = document.getElementById("categoriaList");
    const item = document.createElement("div");

    const text = document.createElement("span");
    text.textContent = nome;

    const removeBtn = document.createElement("button");
    removeBtn.textContent = "X";
    removeBtn.className = "remove-btn";
    removeBtn.onclick = () => item.remove();

    item.appendChild(text);
    item.appendChild(removeBtn);
    list.appendChild(item);
}

function addCategoria() {
    const input = document.getElementById("categoriaInput");
    const valor = input.value.trim();
    if (!valor) return;
    adicionarCategoriaUI(valor);
    input.value = "";
}

function pegarCategorias() {
    const items = document.getElementById("categoriaList").children;
    const lista = [];
    for (const item of items) {
        lista.push(item.querySelector("span").textContent);
    }
    return lista;
}

// ---------- Módulos / Aulas ----------

function addModulo() {
    modulos.push({
        id_modulo: null,
        titulo: "",
        descricao: "",
        carga_horaria: "",
        aulas: []
    });
    renderModulos();
}

function removerModulo(mIndex) {
    modulos.splice(mIndex, 1);
    renderModulos();
}

function addAula(mIndex) {
    modulos[mIndex].aulas.push({
        id_aula: null,
        titulo: "",
        descricao: "",
        link_do_video: ""
    });
    renderModulos();
}

function removerAula(mIndex, aIndex) {
    modulos[mIndex].aulas.splice(aIndex, 1);
    renderModulos();
}

function renderModulos() {
    const container = document.getElementById("modulos");
    container.innerHTML = "";

    modulos.forEach((modulo, mIndex) => {
        const div = document.createElement("div");
        div.className = "box";

        const tagNovo = modulo.id_modulo == null
            ? `<span class="tag-novo">novo</span>`
            : `<small style="opacity:0.6;">id ${modulo.id_modulo}</small>`;

        div.innerHTML = `
            <div style="display:flex;justify-content:space-between;align-items:center;">
                <h4>Módulo ${mIndex + 1} ${tagNovo}</h4>
                <button class="remove-btn" type="button" onclick="removerModulo(${mIndex})">Remover módulo</button>
            </div>
            <input placeholder="Título do módulo" value="${escapeHtml(modulo.titulo)}"
                   oninput="modulos[${mIndex}].titulo=this.value">
            <input placeholder="Descrição" value="${escapeHtml(modulo.descricao)}"
                   oninput="modulos[${mIndex}].descricao=this.value">
            <input type="number" min="1" placeholder="Carga horária (horas)" value="${modulo.carga_horaria}"
                   oninput="modulos[${mIndex}].carga_horaria=this.value">

            <button type="button" onclick="addAula(${mIndex})">Adicionar aula</button>

            <div class="nested" id="aulas-${mIndex}"></div>
        `;

        container.appendChild(div);

        const aulasDiv = div.querySelector(`#aulas-${mIndex}`);

        modulo.aulas.forEach((aula, aIndex) => {
            const aulaDiv = document.createElement("div");
            aulaDiv.className = "box";

            const tagAula = aula.id_aula == null
                ? `<span class="tag-novo">nova</span>`
                : `<small style="opacity:0.6;">id ${aula.id_aula}</small>`;

            aulaDiv.innerHTML = `
                <div style="display:flex;justify-content:space-between;align-items:center;">
                    <strong>Aula ${aIndex + 1} ${tagAula}</strong>
                    <button class="remove-btn" type="button" onclick="removerAula(${mIndex}, ${aIndex})">Remover</button>
                </div>
                <input placeholder="Título da aula" value="${escapeHtml(aula.titulo)}"
                       oninput="modulos[${mIndex}].aulas[${aIndex}].titulo=this.value">
                <input placeholder="Descrição" value="${escapeHtml(aula.descricao)}"
                       oninput="modulos[${mIndex}].aulas[${aIndex}].descricao=this.value">
                <input placeholder="Link do vídeo" value="${escapeHtml(aula.link_do_video)}"
                       oninput="modulos[${mIndex}].aulas[${aIndex}].link_do_video=this.value">
            `;

            aulasDiv.appendChild(aulaDiv);
        });
    });
}

function escapeHtml(str) {
    return String(str ?? "")
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

// ---------- Status ----------

function setStatus(tipo, msg) {
    const el = document.getElementById("status");
    el.className = `status ${tipo}`;
    el.textContent = msg;
}

// ---------- HTTP helpers ----------

async function postJson(url, body) {
    return enviarJson("POST", url, body);
}

async function putJson(url, body) {
    return enviarJson("PUT", url, body);
}

async function enviarJson(method, url, body) {
    const res = await fetch(url, {
        method,
        headers: {
            "Content-Type": "application/json",
            "X-User-CPF": currentUser.cpf
        },
        body: JSON.stringify(body)
    });
    const texto = await res.text();
    if (!res.ok) throw new Error(texto || `Falha em ${method} ${url}`);
    return texto;
}

async function deleteRaw(url) {
    const res = await fetch(url, {
        method: "DELETE",
        headers: { "X-User-CPF": currentUser.cpf }
    });
    const texto = await res.text();
    if (!res.ok) throw new Error(texto || `Falha em DELETE ${url}`);
    return texto;
}

async function getJson(url) {
    const res = await fetch(url, {
        headers: { "X-User-CPF": currentUser.cpf }
    });
    if (!res.ok) {
        const texto = await res.text();
        throw new Error(texto || `Falha em GET ${url}`);
    }
    return res.json();
}

// ---------- Resolução de IDs ----------

async function buscarIdModuloPorTitulo(idCurso, tituloModulo) {
    const lista = await getJson(`${API}/curso/${idCurso}/modulos-aulas`);
    const item = lista.find(x => x.modulo && x.modulo.titulo === tituloModulo);
    if (!item) throw new Error(`Não foi possível localizar o módulo "${tituloModulo}".`);
    return item.modulo.id_modulo;
}

// ---------- Validação ----------

function validar(curso) {
    if (!curso.nomeCurso || curso.nomeCurso.length < 3) {
        return "Informe um nome de curso com pelo menos 3 caracteres.";
    }
    if (curso.preco === null || isNaN(curso.preco) || curso.preco < 0) {
        return "Informe um preço válido (≥ 0).";
    }
    for (const [i, m] of modulos.entries()) {
        if (!m.titulo || !m.titulo.trim()) {
            return `Módulo ${i + 1}: título é obrigatório.`;
        }
        const carga = Number(m.carga_horaria);
        if (!Number.isInteger(carga) || carga <= 0) {
            return `Módulo ${i + 1}: carga horária deve ser um inteiro > 0.`;
        }
        for (const [j, a] of m.aulas.entries()) {
            if (!a.titulo || !a.titulo.trim()) {
                return `Módulo ${i + 1} / Aula ${j + 1}: título é obrigatório.`;
            }
            if (!a.link_do_video || !a.link_do_video.trim()) {
                return `Módulo ${i + 1} / Aula ${j + 1}: link do vídeo é obrigatório.`;
            }
        }
    }
    return null;
}

// ---------- Salvar (atualiza curso, módulos, aulas) ----------

async function atualizarCurso() {
    const btn = document.getElementById("btnSalvar");

    const cursoBody = {
        nomeCurso: document.getElementById("nomeCurso").value.trim(),
        preco: parseFloat(document.getElementById("precoCurso").value),
        descricaoCurso: document.getElementById("descricaoCurso").value.trim(),
        cpfProfessor: currentUser.cpf,
        categorias: pegarCategorias()
    };

    const erro = validar(cursoBody);
    if (erro) {
        setStatus("err", erro);
        return;
    }

    btn.disabled = true;

    try {
        // 1) Atualizar curso
        setStatus("info", "Atualizando curso...");
        await putJson(`${API}/curso/${cursoId}`, cursoBody);

        // 2) Deletar módulos e aulas removidos (comparando com snapshot)
        await processarRemocoes();

        // 3) Criar/atualizar módulos e aulas
        for (const [i, modulo] of modulos.entries()) {
            const moduloBody = {
                id_curso: Number(cursoId),
                titulo: modulo.titulo.trim(),
                carga_horaria: parseInt(modulo.carga_horaria, 10),
                descricao: modulo.descricao || ""
            };

            let idModulo = modulo.id_modulo;

            if (idModulo == null) {
                setStatus("info", `Criando módulo ${i + 1}: "${modulo.titulo}"...`);
                await postJson(`${API}/modulo/save`, moduloBody);
                idModulo = await buscarIdModuloPorTitulo(cursoId, modulo.titulo.trim());
                modulo.id_modulo = idModulo;
            } else {
                setStatus("info", `Atualizando módulo ${i + 1}: "${modulo.titulo}"...`);
                await putJson(`${API}/modulo/update/${cursoId}/${idModulo}`, moduloBody);
            }

            // Aulas do módulo
            for (const [j, aula] of modulo.aulas.entries()) {
                const aulaBody = {
                    id_curso: Number(cursoId),
                    id_modulo: idModulo,
                    titulo: aula.titulo.trim(),
                    link_do_video: aula.link_do_video.trim(),
                    descricao: aula.descricao || ""
                };

                if (aula.id_aula == null) {
                    setStatus("info",
                        `Módulo ${i + 1}: criando aula ${j + 1} ("${aula.titulo}")...`);
                    await postJson(`${API}/aula/save`, aulaBody);
                } else {
                    setStatus("info",
                        `Módulo ${i + 1}: atualizando aula ${j + 1} ("${aula.titulo}")...`);
                    await putJson(`${API}/aula/update/${cursoId}/${idModulo}/${aula.id_aula}`, aulaBody);
                }
            }
        }

        setStatus("ok", "Curso atualizado com sucesso!");
        await reloadUser();
        setTimeout(() => {
            window.location.href = "../home/home-professor.html";
        }, 800);

    } catch (err) {
        console.error(err);
        setStatus("err", "Erro: " + err.message);
        btn.disabled = false;
    }
}

async function processarRemocoes() {
    const idsModulosAtuais = new Set(
        modulos.filter(m => m.id_modulo != null).map(m => m.id_modulo)
    );

    for (const original of modulosOriginais) {
        if (original.id_modulo == null) continue;

        if (!idsModulosAtuais.has(original.id_modulo)) {
            // Módulo inteiro removido — backend faz cascade nas aulas
            setStatus("info", `Removendo módulo "${original.titulo}"...`);
            await deleteRaw(`${API}/modulo/delete/${cursoId}/${original.id_modulo}`);
            continue;
        }

        // Módulo permanece: verificar aulas removidas
        const moduloAtual = modulos.find(m => m.id_modulo === original.id_modulo);
        const idsAulasAtuais = new Set(
            moduloAtual.aulas.filter(a => a.id_aula != null).map(a => a.id_aula)
        );

        for (const aulaOriginal of original.aulas) {
            if (aulaOriginal.id_aula == null) continue;
            if (!idsAulasAtuais.has(aulaOriginal.id_aula)) {
                setStatus("info", `Removendo aula "${aulaOriginal.titulo}"...`);
                await deleteRaw(
                    `${API}/aula/delete/${cursoId}/${original.id_modulo}/${aulaOriginal.id_aula}`
                );
            }
        }
    }
}

// ---------- Deletar curso ----------

async function deletarCurso() {
    if (!confirm("Tem certeza que deseja deletar este curso?")) return;

    try {
        await deleteRaw(`${API}/curso/${cursoId}`);
        setStatus("ok", "Curso deletado com sucesso!");
        await reloadUser();
        setTimeout(() => {
            window.location.href = "../home/home-professor.html";
        }, 600);
    } catch (err) {
        console.error(err);
        setStatus("err", "Erro ao deletar curso: " + err.message);
    }
}
