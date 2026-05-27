let comentarios = [];

function getNomeUsuario() {
    try {
        const u = JSON.parse(localStorage.getItem("user") ?? "{}");
        return u?.nome || "Aluno";
    } catch {
        return "Aluno";
    }
}

function inicialDe(nome) {
    return (nome ?? "U").trim().charAt(0).toUpperCase();
}

function escapeComentario(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

/* ➕ Novo comentário */
function addComentario(pai = null) {
    const input = document.getElementById("comentarioInput");
    if (!input.value.trim()) return;

    const autor = getNomeUsuario();

    comentarios.push({
        id: Date.now() + Math.random(),
        conteudo: input.value,
        autor,
        data: new Date().toLocaleString(),
        pai: pai
    });

    input.value = "";
    renderComentarios();
}

/* 🔁 Render */
function renderComentarios() {
    const container = document.getElementById("comentariosList");
    if (!container) return;
    container.innerHTML = "";

    const raiz = comentarios.filter(c => c.pai === null);

    if (raiz.length === 0) {
        container.innerHTML = `<p style="color: #64748b; font-size: 0.9em; text-align: center; padding: 12px;">
            Seja o primeiro a comentar nesta aula.
        </p>`;
        return;
    }

    raiz.forEach(c => container.appendChild(criarComentario(c)));
}

/* 🧱 Criar comentário */
function criarComentario(comentario) {
    const div = document.createElement("div");
    div.className = "comentario";

    div.innerHTML = `
        <div class="avatar-mini">${escapeComentario(inicialDe(comentario.autor))}</div>
        <div class="body">
            <div class="comentario-header">
                <strong>${escapeComentario(comentario.autor)}</strong>
                <span>• ${escapeComentario(comentario.data)}</span>
            </div>
            <div class="comentario-conteudo">${escapeComentario(comentario.conteudo)}</div>
            <span class="responder">↩ Responder</span>
            <div class="resposta-box hidden">
                <textarea placeholder="Escreva uma resposta..."></textarea>
                <button>Enviar resposta</button>
            </div>
        </div>
    `;

    const respondBtn = div.querySelector(".responder");
    const respostaBox = div.querySelector(".resposta-box");
    const textarea = respostaBox.querySelector("textarea");
    const enviarBtn = respostaBox.querySelector("button");
    const corpo = div.querySelector(".body");

    respondBtn.onclick = () => {
        respostaBox.classList.toggle("hidden");
        textarea.focus();
    };

    enviarBtn.onclick = () => {
        const texto = textarea.value.trim();
        if (!texto) return;

        comentarios.push({
            id: Date.now() + Math.random(),
            conteudo: texto,
            autor: getNomeUsuario(),
            data: new Date().toLocaleString(),
            pai: comentario.id
        });

        renderComentarios();
    };

    /* respostas filhas */
    const respostas = comentarios.filter(c => c.pai === comentario.id);
    if (respostas.length > 0) {
        const respostasDiv = document.createElement("div");
        respostasDiv.className = "respostas";
        respostas.forEach(r => respostasDiv.appendChild(criarComentario(r)));
        corpo.appendChild(respostasDiv);
    }

    return div;
}

document.addEventListener("DOMContentLoaded", renderComentarios);
