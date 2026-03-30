let user;

        document.addEventListener("DOMContentLoaded", () => {
            user = JSON.parse(localStorage.getItem("user"));

            if (!user) {
                window.location.href = "../login/login.html";
                return;
            }

            preencherFormulario();
        });

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