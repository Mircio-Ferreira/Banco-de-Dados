async function carregarCursos() {
  const response = await fetch("http://localhost:3000/api/cursos");
  const cursos = await response.json();

  const carrossel = document.querySelector(".carrossel");

  carrossel.innerHTML = ""; // limpa o HTML fixo

  cursos.forEach(curso => {
    const card = document.createElement("div");
    card.classList.add("card", "card-curso");
    card.dataset.curso = curso.slug;

    card.innerHTML = `
      <img src="${curso.imagem}" class="thumb" />
      <h3>${curso.nome}</h3>
    `;

    carrossel.appendChild(card);
  });
}

carregarCursos();