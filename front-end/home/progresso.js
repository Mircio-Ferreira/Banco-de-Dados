/**
 * 📊 Carrega o progresso do aluno em cada curso adquirido.
 * Endpoint: GET /api/v1/users/alunos/progresso-alunos/{cpf_aluno}
 *
 * Resposta: List<ViewProgressoAlunoCurso> com os campos
 *   { cpfAluno, idCurso, nomeCurso, totalAulas, aulasAssistidas, percentualConclusao }
 */
async function carregarProgresso() {
    const container = document.getElementById("progressoContainer");
    if (!container) return;

    const userStorage = localStorage.getItem("user");
    if (!userStorage) return;

    const usuario = JSON.parse(userStorage);
    const cpf = usuario.cpf;

    if (!cpf) {
        container.innerHTML = `<p class="progresso-empty">CPF do aluno não encontrado.</p>`;
        return;
    }

    try {
        const res = await fetch(
            `http://localhost:8080/api/v1/users/alunos/progresso-alunos/${cpf}`,
            { headers: { "X-User-CPF": cpf } }
        );

        if (!res.ok) {
            const erro = await res.text();
            throw new Error(erro || `HTTP ${res.status}`);
        }

        const progresso = await res.json();
        renderProgresso(progresso);

    } catch (err) {
        console.error("Erro ao carregar progresso:", err);
        container.innerHTML =
            `<p class="progresso-empty" style="color:#ef4444;">Erro ao carregar progresso: ${err.message}</p>`;
    }
}

function renderProgresso(lista) {
    const container = document.getElementById("progressoContainer");
    container.innerHTML = "";

    if (!Array.isArray(lista) || lista.length === 0) {
        container.innerHTML =
            `<p class="progresso-empty">Você ainda não possui progresso registrado. Adquira um curso e comece a assistir!</p>`;
        return;
    }

    lista.forEach(item => {
        const total = Number(item.totalAulas ?? 0);
        const assistidas = Number(item.aulasAssistidas ?? 0);
        const pct = Number(item.percentualConclusao ?? 0);
        const pctClamp = Math.max(0, Math.min(100, pct));

        const card = document.createElement("div");
        card.className = "progresso-card";

        card.innerHTML = `
            <h3>${item.nomeCurso ?? "(sem nome)"}</h3>
            <div class="progresso-meta">
                <span>${assistidas} / ${total} aulas</span>
                <span>${total - assistidas} restante${(total - assistidas) === 1 ? "" : "s"}</span>
            </div>
            <div class="progresso-bar">
                <div class="progresso-bar-fill" style="width:${pctClamp}%"></div>
            </div>
            <div class="progresso-pct">${pctClamp.toFixed(2)}%</div>
        `;

        card.onclick = () => {
            if (item.idCurso != null) {
                window.location.href = `../curso/curso.html?id=${item.idCurso}`;
            }
        };

        container.appendChild(card);
    });
}

document.addEventListener("DOMContentLoaded", carregarProgresso);
