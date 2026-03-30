async function reloadUser() {
    const cpf = currentUser.cpf;
    const userType = currentUser.tipoUsuario.toLowerCase();

    const response = await fetch(`http://localhost:8080/api/v1/users/${userType}/${cpf}`);

    if (!response.ok) {
        const err = await response.text();
        throw new Error(err);
    }

    const data = await response.json();

    console.log(data);
    localStorage.setItem("user", JSON.stringify(data));

    return data; // 🔥 importante
}