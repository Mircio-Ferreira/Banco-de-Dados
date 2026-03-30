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

    fetch(`http://localhost:8080/api/v1/users/aluno/${cpf}`, {
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