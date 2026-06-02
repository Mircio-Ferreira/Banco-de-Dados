let user;

document.addEventListener("DOMContentLoaded", () => {
    user = JSON.parse(localStorage.getItem("user"));

    if (!user) {
        window.location.href = "../login/login.html";
        return;
    }

    montarSidebar();
    preencherFormulario();
});

function montarSidebar() {
    const sidebar = document.getElementById("sidebarAlterar");
    if (!sidebar) return;

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

function preencherFormulario() {
    document.getElementById("cpf").value = user.cpf || "";
    document.getElementById("nome").value = user.nome || "";
    document.getElementById("email").value = user.email || "";
    document.getElementById("cep").value = user.cep || "";
    document.getElementById("logradouro").value = user.logradouro || "";
    document.getElementById("numero").value = user.numero || "";

    // Mostrar certificações se for professor
    if (user.tipoUsuario !== "ALUNO") {
        document.getElementById("certificacoesContainer").classList.remove("hidden");
    }

    const list = document.getElementById("telefoneList");
    const listC = document.getElementById("certList");

    for (const t of currentUser.telefones) {
        const item = document.createElement("div");

        const text = document.createElement("span");
        text.textContent = t;

        const removeBtn = document.createElement("button");
        removeBtn.textContent = "X";
        removeBtn.style.marginLeft = "10px";

        removeBtn.onclick = () => {
            item.remove();
        };

        item.appendChild(text);
        item.appendChild(removeBtn);

        list.appendChild(item);
    }

    for (const c of currentUser.certificados) {
        const item = document.createElement("div");

        const text = document.createElement("span");
        text.textContent = c;

        const removeBtn = document.createElement("button");
        removeBtn.textContent = "X";
        removeBtn.style.marginLeft = "10px";

        removeBtn.onclick = () => {
            item.remove();
        };

        item.appendChild(text);
        item.appendChild(removeBtn);

        listC.appendChild(item);
    }
}

async function atualizarUsuario() {
    const updatedUser = {
        ...user,
        nome: document.getElementById("nome").value,
        email: document.getElementById("email").value,
        cep: document.getElementById("cep").value,
        logradouro: document.getElementById("logradouro").value,
        numero: document.getElementById("numero").value
    };

    updatedUser.telefones = Array.from(
        document.querySelectorAll("#telefoneList > div > span")
    ).map(span => span.textContent);

    if (user.tipoUsuario === "PROFESSOR") {
        updatedUser.certificados = Array.from(
            document.querySelectorAll("#certList > div > span")
        ).map(span => span.textContent);
    }

    const novaSenha = document.getElementById("password").value;

    if (novaSenha) {
        updatedUser.senha = novaSenha;
    }

    // 🔥 Define endpoint baseado no tipo
    let url;

    if (user.tipoUsuario === "ALUNO") {
        url = `http://localhost:8080/api/v1/users/aluno/${user.cpf}`;
    } else {
        url = `http://localhost:8080/api/v1/users/professor/${user.cpf}`;
    }

    try {
        const response = await fetch(url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-User-CPF": user.cpf // 🔐 segurança
            },
            body: JSON.stringify(updatedUser)
        });

        const data = await response.text();

        if (!response.ok) {
            alert("Erro: " + data);
            return;
        }

        // ✅ Atualiza localStorage só se deu certo
        localStorage.setItem("user", JSON.stringify(updatedUser));

        alert("Perfil atualizado com sucesso!");

    } catch (error) {
        console.error(error);
        alert("Erro ao conectar com o servidor.");
    }
}