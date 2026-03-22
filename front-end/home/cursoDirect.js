document.querySelectorAll('.card-curso').forEach(card => {
  card.addEventListener('click', () => {
    const curso = card.getAttribute('data-curso');
    window.location.href = `../curso/curso.html?curso=${curso}`;
  });
});