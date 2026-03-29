function login() {
    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("senha").value.trim();
    const erroDiv = document.getElementById("erroLogin");

    if (!email || !senha) {
        erroDiv.style.display = "block";
        erroDiv.innerText = "Preencha email e senha.";
        return;
    }

    erroDiv.style.display = "none";

    fetch("http://localhost:8080/api/v1/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, senha })
    })
        .then(res => {
            if (!res.ok) {
                return res.text().then(err => { throw new Error(err); });
            }
            return res.json();
        })
        .then(data => {
            console.log("Login sucesso:", data);

            // exemplo: salvar token
            localStorage.setItem("token", data.cpf);

            window.location.href = "../home/home.html";
        })
        .catch(err => {
            console.error(err)
            erroDiv.style.display = "block";
            erroDiv.innerText = err.message;
        });
}