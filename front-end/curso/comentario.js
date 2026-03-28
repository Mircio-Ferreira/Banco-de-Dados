let comentarios = [];

/* ➕ Novo comentário */
function addComentario(pai = null) {
    const input = document.getElementById("comentarioInput");

    if (!input.value.trim()) return;

    comentarios.push({
        id: Date.now(),
        conteudo: input.value,
        autor: "Aluno",
        data: new Date().toLocaleString(),
        pai: pai
    });

    input.value = "";
    renderComentarios();
}

/* 🔁 Render */
function renderComentarios() {
    const container = document.getElementById("comentariosList");
    container.innerHTML = "";

    const raiz = comentarios.filter(c => c.pai === null);

    raiz.forEach(c => {
        container.appendChild(criarComentario(c));
    });
}

/* 🧱 Criar comentário */
function criarComentario(comentario) {
    const div = document.createElement("div");
    div.className = "comentario";

    div.innerHTML = `
        <div class="comentario-header">
            ${comentario.autor} • ${comentario.data}
        </div>
        <div class="comentario-conteudo">
            ${comentario.conteudo}
        </div>
        <div class="responder">Responder</div>
        <div class="resposta-box hidden">
            <textarea placeholder="Escreva uma resposta..."></textarea>
            <button>Enviar</button>
        </div>
    `;

    const responderBtn = div.querySelector(".responder");
    const respostaBox = div.querySelector(".resposta-box");
    const textarea = respostaBox.querySelector("textarea");
    const enviarBtn = respostaBox.querySelector("button");

    /* Mostrar/esconder */
    responderBtn.onclick = () => {
        respostaBox.classList.toggle("hidden");
        textarea.focus();
    };

    /* Enviar resposta */
    enviarBtn.onclick = () => {
        const texto = textarea.value.trim();
        if (!texto) return;

        comentarios.push({
            id: Date.now(),
            conteudo: texto,
            autor: "Aluno",
            data: new Date().toLocaleString(),
            pai: comentario.id
        });

        renderComentarios();
    };

    /* 👇 Respostas */
    const respostas = comentarios.filter(c => c.pai === comentario.id);

    if (respostas.length > 0) {
        const respostasDiv = document.createElement("div");
        respostasDiv.className = "respostas";

        respostas.forEach(r => {
            respostasDiv.appendChild(criarComentario(r));
        });

        div.appendChild(respostasDiv);
    }

    return div;
}