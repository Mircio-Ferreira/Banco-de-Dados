function scrollCarrossel(btn, direction) {
  const container = btn.parentElement.querySelector('.carrossel');

  const scrollAmount = 300; // quanto anda por clique

  container.scrollBy({
    left: direction * scrollAmount,
    behavior: 'smooth'
  });
}