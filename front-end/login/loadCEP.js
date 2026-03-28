async function buscarCEP(cep) {
    cep = cep.replace(/\D/g, ""); // remove tudo que não for número

    if (cep.length !== 8) return;

    try {
        const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
        const data = await res.json();

        if (data.erro) {
            throw Error("CEP não encontrado")
        }

        // Exemplo de preenchimento automático
        document.getElementById("logradouro").value = data.logradouro;

    } catch (e) {
        console.error("Erro ao buscar CEP:", e);
    }
}