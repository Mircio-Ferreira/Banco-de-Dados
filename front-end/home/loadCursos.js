async function carregarCursos() {
    try {
        const res = await fetch("http://localhost:8080/api/v1/curso");

        if (!res.ok) {
            throw new Error("Erro ao buscar cursos");
        }

        const cursos = await res.json();

        renderizarCursos(cursos);

    } catch (err) {
        console.error(err);
    }
}

function renderizarCursos(cursos) {
    const container = document.getElementById("carrosselCursos");

    container.innerHTML = ""; // limpa

    cursos.forEach(curso => {
        const card = document.createElement("div");
        card.classList.add("card", "card-curso");

        card.innerHTML = `
            <img src="https://via.placeholder.com/150" class="thumb" />
            <h3>${curso.nome_curso}</h3>
            <p style="font-size:12px; opacity:0.7;">
                ${curso.categorias.map(c => c.nome).join(", ")}
            </p>
        `;

        // clicar no curso
        card.onclick = () => {
            window.location.href = `../curso/curso.html?id=${curso.id_curso}`;
        };

        container.appendChild(card);
    });
}

document.addEventListener("DOMContentLoaded", carregarCursos);