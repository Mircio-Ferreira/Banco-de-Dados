function cadastrar() {
    const campos = [
        { id: "cpf", nome: "CPF" },
        { id: "nome", nome: "Nome completo" },
        { id: "email", nome: "Email" },
        { id: "password", nome: "Senha" },
        { id: "cep", nome: "CEP" },
        { id: "logradouro", nome: "Logradouro" },
        { id: "numero", nome: "Número" }
    ];

    let faltando = [];

    campos.forEach(campo => {
        const input = document.getElementById(campo.id);

        if (!input.value.trim()) {
            faltando.push(campo.nome);
            input.style.border = "2px solid red"; // destaque erro
        } else {
            input.style.border = "1px solid #ccc";
        }
    });

    // Telefones (pelo menos 1)
    const telefones = document.getElementById("telefoneList").children;
    if (telefones.length === 0) {
        faltando.push("Pelo menos um telefone");
    }

    // Certificações se for professor
    const isProfessor = document.getElementById("isProfessor").checked;
    if (isProfessor) {
        const certs = document.getElementById("certList").children;
        if (certs.length === 0) {
            faltando.push("Pelo menos uma certificação");
        }
    }

    const erroDiv = document.getElementById("erroMsg");

    if (faltando.length > 0) {
        erroDiv.style.display = "block";
        erroDiv.innerText = "⚠️ Preencha os seguintes campos:\n\n" + faltando.join("\n");
        return;
    }

    erroDiv.style.display = "none";

    // Se passou tudo
    const usuario = {
        cpf: document.getElementById("cpf").value.replace(/\D/g, ""),
        nome: document.getElementById("nome").value,
        email: document.getElementById("email").value,
        senha: document.getElementById("password").value,

        logradouro: document.getElementById("logradouro").value,
        numero: document.getElementById("numero").value,
        cep: document.getElementById("cep").value.replace(/\D/g, ""),

        telefones: []
    };

    const telefoneItems = document.getElementById("telefoneList").children;

    for (let item of telefoneItems) {
        usuario.telefones.push(item.firstChild.textContent);
    }

    if (isProfessor) {
        const certItems = document.getElementById("certList").children;
        usuario.certificados = []

        for (let item of certItems) {
            usuario.certificados.push(item.firstChild.textContent);
        }
    }

    //http://localhost:8080/api/v1/users/aluno/create

    const userType = isProfessor ? "professor" : "aluno"

    fetch(`http://localhost:8080/api/v1/users/${userType}/create`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(usuario)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err); });
            }
            return response.text();
        })
        .then(msg => {
            console.log("Cadastro OK:", msg);

            // 👉 AGORA FAZ LOGIN
            return fetch("http://localhost:8080/api/v1/users/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: usuario.email,
                    senha: usuario.senha
                })
            });
        })
        .then(res => {
            if (!res.ok) {
                return res.text().then(err => { throw new Error(err); });
            }
            return res.json();
        })
        .then(data => {
            console.log("Login sucesso:", data);

            const cpf = data.cpf
            const userType = data.tipoUsuario.toLowerCase()

            return fetch(`http://localhost:8080/api/v1/users/${userType}/${cpf}`)
                .then(res => {
                    if (!res.ok) {
                        return res.text().then(err => { throw new Error(err); });
                    }
                    return res.json();
                })
                .then(data => {
                    console.log(data)
                    localStorage.setItem("user", JSON.stringify(data));

                    window.location.href = `../home/home.html`;
                })
        })
        .catch(error => {
            console.error("Erro:", error);

            let msg;

            try {
                msg = JSON.parse(error.message);
                erroDiv.innerText = msg.join("\n");
            } catch {
                erroDiv.innerText = error.message;
            }

            erroDiv.style.display = "block";
        });
}