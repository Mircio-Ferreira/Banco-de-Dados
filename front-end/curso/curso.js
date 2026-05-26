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
 * @param {Object} aula - Objeto contendo os dados da aula
 */
function carregarAula(aula) {
    aulaAtual = aula;

    // Injeta o iframe do vídeo (utiliza a propriedade 'link' do novo modelo)
    document.getElementById("video").innerHTML =
        `<iframe width="100%" height="100%" src="${aula.link}" frameborder="0" allowfullscreen></iframe>`;

    // Atualiza os textos da aula
    document.getElementById("tituloAula").innerText = aula.titulo;
    document.getElementById("descricaoAula").innerText = aula.descricao_aula;

    // Gerencia o bloco de materiais de apoio
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
 * 🕒 Busca a Carga Horária Total do Curso no Backend e injeta no topo da Sidebar
 * @param {string} idCurso - ID do curso capturado da URL
 */
async function carregarCargaHoraria(idCurso) {
    const urlHoras = `http://localhost:8080/api/v1/curso/curso-horas-totais/${idCurso}`;
    
    try {
        const response = await fetch(urlHoras);
        if (!response.ok) throw new Error("Erro ao buscar carga horária");
        
        const horasTotais = await response.text(); 

        const horasBadge = document.getElementById("cursoCargaHoraria");
        if (horasBadge) {
            horasBadge.innerHTML = `🕒 Carga Horária Total: <span style="background: #1e293b; color: #38bdf8; padding: 2px 6px; border-radius: 4px; margin-left: 5px;">${horasTotais}h</span>`;
        }
    } catch (error) {
        console.error("Não foi possível carregar a carga horária:", error);
        const horasBadge = document.getElementById("cursoCargaHoraria");
        if (horasBadge) {
            horasBadge.style.display = "none"; // Esconde o bloco em caso de falha
        }
    }
}

/**
 * 📦 Renderiza a árvore de Módulos e Aulas no container dedicado da Sidebar
 * @param {Array} modulos - Lista de módulos e aulas retornada da API
 */
function renderSidebar(modulos) {
    const container = document.getElementById("modulosContainer");
    if (!container) return;
    
    // Limpa apenas o conteúdo do container de aulas, preservando o topo da sidebar
    container.innerHTML = ""; 

    modulos.forEach(item => {
        const modDiv = document.createElement("div");
        modDiv.className = "modulo";

        // Título extraído do objeto interno 'modulo'
        modDiv.innerHTML = `<h3>📦 ${item.modulo.titulo}</h3>`;

        // Varre e renderiza as aulas do módulo correspondente
        if (item.aulas && item.aulas.length > 0) {
            item.aulas.forEach(aula => {
                const aulaDiv = document.createElement("div");
                aulaDiv.className = "aula";
                aulaDiv.innerText = aula.titulo;

                // Evento de clique para alternar o player de vídeo
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
 * 🔄 Função Orquestradora: Executada ao carregar a página.
 * Controla o fluxo de chamadas e evita concorrência assíncrona.
 */
async function inicializarCurso() {
    const idCurso = getCursoId();
    
    if (!idCurso) {
        console.error("ID do curso não foi encontrado nos parâmetros da URL (ex: ?id=1)");
        const container = document.getElementById("modulosContainer");
        if (container) {
            container.innerHTML = "<p style='padding:15px; color: #ef4444;'>Erro: Nenhum ID de curso foi especificado na URL.</p>";
        }
        return;
    }

    // 1. Aguarda obrigatoriamente a carga horária ser injetada
    await carregarCargaHoraria(idCurso);

    // 2. Monta a URL e busca a estrutura de módulos do backend
    const urlModulos = `http://localhost:8080/api/v1/curso/${idCurso}/modulos-aulas`;

    try {
        const response = await fetch(urlModulos);
        if (!response.ok) throw new Error(`Erro HTTP! Status: ${response.status}`);

        const modulos = await response.json();
        
        // 3. Renderiza a lista estruturada na barra lateral
        renderSidebar(modulos);

        // 4. Se houver dados, inicializa o player com a primeira aula do primeiro módulo
        if (modulos.length > 0 && modulos[0].aulas && modulos[0].aulas.length > 0) {
            carregarAula(modulos[0].aulas[0]);
            
            // Ativa visualmente a primeira aula após renderização no DOM
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