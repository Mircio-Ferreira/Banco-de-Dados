// Variáveis de controle de estado
let cursoSelecionadoId = null;
let cpfAlunoLogado = null;
let todosCursosDoSistema = []; // Guardará o catálogo completo para busca de nomes

/**
 * 🔄 Inicializador do Dashboard do Aluno
 */
async function inicializarDashboard() {
    const userStorage = localStorage.getItem("user");
    
    if (!userStorage) {
        console.error("Nenhum usuário detectado no localStorage.");
        window.location.href = "../login/login.html";
        return;
    }

    const usuario = JSON.parse(userStorage);
    cpfAlunoLogado = usuario.cpf;

    if (!cpfAlunoLogado) {
        console.error("CPF não encontrado nos dados do usuário local.");
        return;
    }

    try {
        // 1. Busca PRIMEIRO todos os cursos do sistema para termos os nomes e detalhes
        const resTodosCursos = await fetch("http://localhost:8080/api/v1/curso");
        if (resTodosCursos.ok) {
            todosCursosDoSistema = await resTodosCursos.json();
        } else {
            throw new Error("Não foi possível carregar o catálogo geral de cursos.");
        }

        // 2. Busca os históricos de compra do aluno (que só contém o id_curso)
        const resMinhasCompras = await fetch(`http://localhost:8080/api/v1/compra/aluno/${cpfAlunoLogado}`);
        const minhasCompras = resMinhasCompras.ok ? await resMinhasCompras.json() : [];
        
        // Mapeia um array simples contendo apenas os IDs das compras do aluno
        const idsCursosAdquiridos = minhasCompras.map(item => item.id_curso);

        // 3. Renderiza os cursos que o aluno comprou (passando os IDs e o catálogo para cruzar os dados)
        renderizarMeusCursos(idsCursosAdquiridos);

        // 4. Filtra o catálogo: remove os que o aluno já comprou para exibir no catálogo de vendas
        const cursosDisponiveis = todosCursosDoSistema.filter(c => !idsCursosAdquiridos.includes(c.id_curso));
        renderizarCatalogoCompra(cursosDisponiveis);

    } catch (err) {
        console.error("Erro ao carregar dados do dashboard:", err);
    }
}

/**
 * 📚 Renderiza a lista de cursos que o aluno JÁ possui acesso
 */
function renderizarMeusCursos(idsCursosAdquiridos) {
    const container = document.getElementById("carrosselCursos");
    if (!container) return;
    container.innerHTML = "";

    if (idsCursosAdquiridos.length === 0) {
        container.innerHTML = "<p style='padding:15px; color: #94a3b8;'>Você ainda não possui nenhum curso cadastrado.</p>";
        return;
    }

    idsCursosAdquiridos.forEach(idCurso => {
        // 🔍 Cruza o id_curso da compra com a lista global para achar os dados do curso (nome, categoria, etc)
        const cursoDados = todosCursosDoSistema.find(c => c.id_curso === idCurso);

        // Se por algum motivo o curso não existir mais no catálogo geral, ignora para não quebrar a tela
        if (!cursoDados) return;

        const card = document.createElement("div");
        card.classList.add("card", "card-curso");
        card.innerHTML = `
            <img src="https://via.placeholder.com/150" class="thumb" alt="Capa" />
            <h3>${cursoDados.nome_curso}</h3>
            <p style="font-size:12px; opacity:0.7;">
                ${cursoDados.categorias ? cursoDados.categorias.map(c => c.nome).join(", ") : "Geral"}
            </p>
        `;
        
        // Ao clicar, o aluno vai para a página de assistir as aulas passando o ID correto
        card.onclick = () => {
            window.location.href = `../curso/curso.html?id=${cursoDados.id_curso}`;
        };
        
        container.appendChild(card);
    });
}

/**
 * 🛒 Renderiza os cursos disponíveis para compra (catálogo filtrado)
 */
function renderizarCatalogoCompra(cursos) {
    const container = document.getElementById("carrosselCatalogo");
    if (!container) return;
    container.innerHTML = "";

    if (cursos.length === 0) {
        container.innerHTML = "<p style='padding:15px; color: #94a3b8;'>Parabéns! Você já possui todos os nossos cursos.</p>";
        return;
    }

    cursos.forEach(curso => {
        const card = document.createElement("div");
        card.classList.add("card", "card-curso");
        card.style.border = "1px solid rgba(34, 197, 94, 0.3)";

        card.innerHTML = `
            <img src="https://via.placeholder.com/150" class="thumb" alt="Capa" />
            <h3>${curso.nome_curso}</h3>
            <p style="font-size:12px; color: #22c55e; font-weight: bold; margin-top: 5px;">🛒 Adquirir Curso</p>
        `;

        card.onclick = () => abrirModalCompra(curso.id_curso, curso.nome_curso);

        container.appendChild(card);
    });
}

/**
 * 🎭 Controle do Pop-up (Modal)
 */
function abrirModalCompra(idCurso, nomeCurso) {
    cursoSelecionadoId = idCurso;
    document.getElementById("nomeCursoModal").innerText = nomeCurso;
    document.getElementById("modalCompra").style.display = "flex";
}

function fecharModal() {
    document.getElementById("modalCompra").style.display = "none";
    cursoSelecionadoId = null;
}

/**
 * 🚀 Envia o POST da compra para o backend
 */
async function efetivarCompra() {
    if (!cursoSelecionadoId || !cpfAlunoLogado) return;

    const payload = {
        "id_curso": cursoSelecionadoId,
        "cpf_aluno": cpfAlunoLogado
    };

    try {
        const response = await fetch("http://localhost:8080/api/v1/compra", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert("Curso adquirido com sucesso!");
            fecharModal();
            // Atualiza o painel inteiro de forma reativa recarregando os fluxos
            inicializarDashboard(); 
        } else {
            alert("Erro ao processar compra no servidor. Verifique os dados.");
        }
    } catch (error) {
        console.error("Erro na rota POST de compra:", error);
        alert("Não foi possível conectar ao servidor de compras.");
    }
}

// 🎬 Roda a aplicação assim que a estrutura da página carregar
document.addEventListener("DOMContentLoaded", inicializarDashboard);