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
        cpf: document.getElementById("cpf").value,
        nome: document.getElementById("nome").value,
        email: document.getElementById("email").value,
        senha: document.getElementById("password").value,

        endereco: {
            cep: document.getElementById("cep").value,
            logradouro: document.getElementById("logradouro").value,
            numero: document.getElementById("numero").value
        },

        isProfessor: document.getElementById("isProfessor").checked,

        telefones: [],

        certificacoes: []
    };

    const telefoneItems = document.getElementById("telefoneList").children;

    for (let item of telefoneItems) {
        usuario.telefones.push(item.firstChild.textContent);
    }

    if (usuario.isProfessor) {
        const certItems = document.getElementById("certList").children;

        for (let item of certItems) {
            usuario.certificacoes.push(item.firstChild.textContent);
        }
    }

    console.log(usuario)
    // falta mandar pro back
}