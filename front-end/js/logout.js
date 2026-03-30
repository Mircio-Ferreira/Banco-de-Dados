function logout() {
    const confirmacao = confirm("Deslogar?");

    if (!confirmacao) return;

    localStorage.removeItem("user");

    // redireciona
    window.location.href = "../login/login.html";
}