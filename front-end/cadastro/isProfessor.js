const isProfessor = document.getElementById("isProfessor");
const certContainer = document.getElementById("certificacoesContainer");

isProfessor.addEventListener("change", () => {
    certContainer.classList.toggle("hidden", !isProfessor.checked);
});