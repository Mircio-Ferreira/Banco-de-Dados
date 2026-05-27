function deletarConta() {
    const user = JSON.parse(localStorage.getItem("user")); // você salvou o CPF como token
    const cpf = user.cpf
    const erroDiv = document.getElementById("erroConfig");

    if (!cpf) {
        erroDiv.innerText = "Usuário não autenticado.";
        return;
    }

    const confirmacao = confirm("Tem certeza que deseja excluir sua conta? Essa ação NÃO pode ser desfeita.");

    if (!confirmacao) return;

    const tipo = (user.tipoUsuario === "PROFESSOR") ? "professor" : "aluno";

    fetch(`http://localhost:8080/api/v1/users/${tipo}/${cpf}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "X-User-CPF": cpf
        }
    })
        .then(res => {
            if (!res.ok) {
                return res.text().then(err => { throw new Error(err); });
            }
            return res.text();
        })
        .then(msg => {
            alert("Conta deletada com sucesso!");

            // limpa sessão
            localStorage.removeItem("user");
            localStorage.removeItem("token");

            // volta pro login
            window.location.href = "../login/login.html";
        })
        .catch(err => {
            erroDiv.innerText = err.message;
        });
}

function alterarPage(){
    window.location.href = "../configuracoes/alterar-usuario.html";
}

function montarSidebarConfig() {
    const sidebar = document.getElementById("sidebarConfig");
    if (!sidebar) return;

    const user = JSON.parse(localStorage.getItem("user"));
    const isProfessor = user?.tipoUsuario === "PROFESSOR";

    if (isProfessor) {
        sidebar.innerHTML = `
            <a href="../home/home-professor.html">🏠 Início</a>
            <a href="../certificados/certificadosProfessor.html">🏆 Certificados</a>
            <a href="../addCurso/addCurso.html">➕ Criar curso</a>
            <a href="../dashboardPreco/dashboardPreco.html">📊 Dashboard de Preços</a>
            <a href="../dashboardAlunosInativos/dashboardAlunosInativos.html">😴 Alunos Inativos</a>
            <a href="../dashboardProfessores/dashboardProfessores.html">🏆 Comparar Professores</a>
            <a href="../configuracoes/configuracoes.html">⚙️ Configurações</a>
        `;
    } else {
        sidebar.innerHTML = `
            <a href="../home/home-aluno.html">🏠 Home</a>
            <a href="../home/home-aluno.html">📚 Meus cursos</a>
            <a href="../configuracoes/configuracoes.html">⚙️ Configurações</a>
        `;
    }
}

document.addEventListener("DOMContentLoaded", montarSidebarConfig);
