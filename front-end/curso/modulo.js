document.querySelectorAll('.modulo h3').forEach(titulo => {
  titulo.addEventListener('click', () => {

    const moduloAtual = titulo.parentElement;
    const aulasAtual = moduloAtual.querySelector('.aulas');

    document.querySelectorAll('.modulo').forEach(modulo => {
      const aulas = modulo.querySelector('.aulas');

      if (modulo !== moduloAtual) {
        modulo.classList.remove('ativo');
        aulas.style.height = "0px";
      }
    });

    if (moduloAtual.classList.contains('ativo')) {
      aulasAtual.style.height = "0px";
    } else {
      aulasAtual.style.height = aulasAtual.scrollHeight + "px";
    }

    moduloAtual.classList.toggle('ativo');
  });
});