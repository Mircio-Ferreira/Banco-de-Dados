const currentUser = JSON.parse(localStorage.getItem("user"))

if (!currentUser){
    window.location.href = "../login/login.html";
}

document.addEventListener("DOMContentLoaded", function () {
    const userNameElement = this.getElementById("userName")
    userNameElement.innerHTML = currentUser.nome
});