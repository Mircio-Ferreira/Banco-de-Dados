const API = "http://localhost:8080/api/v1";

let certificadosBase = [];
let nomeProfessor = "";

const filtroCert = {
    busca: ""
};

function fetchAuth(url) {
    return fetch(url, {
        headers: {
            "Accept": "application/json",
            "X-User-CPF": currentUser?.cpf ?? ""
        }
    });
}

async function carregarPerfilProfessor() {
    const res = await fetchAuth(`${API}/users/professor/${currentUser.cpf}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return await res.json();
}

function montarCard(titulo) {
    const card = document.createElement("div");
    card.className = "cert-card";

    card.innerHTML = `
        <div class="cert-medalha">🏆</div>
        <h3 class="cert-titulo">${escapeHtml(titulo)}</h3>
        <div class="cert-emissor">Certificação registrada no perfil do professor</div>
        <div class="cert-rodape">
            <span>👤 ${escapeHtml(nomeProfessor || "Professor")}</span>
            <span class="verificado">✔ verificado</span>
        </div>
    `;
    return card;
}

function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function renderCertificados() {
    const container = document.getElementById("certificadosContainer");
    const contagem  = document.getElementById("contagemCertificados");
    if (!container) return;

    const termo = filtroCert.busca.trim().toLowerCase();
    const filtrados = termo
        ? certificadosBase.filter(t => (t ?? "").toLowerCase().includes(termo))
        : certificadosBase;

    contagem.textContent = `${filtrados.length} ${filtrados.length === 1 ? "certificado" : "certificados"}`;

    container.innerHTML = "";

    if (certificadosBase.length === 0) {
        container.innerHTML = `
            <div class="secao-vazia" style="grid-column: 1 / -1;">
                <span class="icone">🎓</span>
                Você ainda não possui certificações cadastradas.<br>
                Adicione a primeira em <a href="../configuracoes/alterar-usuario.html" style="color:#bae6fd;">Editar Perfil</a>.
            </div>`;
        return;
    }

    if (filtrados.length === 0) {
        container.innerHTML = `
            <div class="secao-vazia" style="grid-column: 1 / -1;">
                Nenhum certificado corresponde ao termo "<strong>${escapeHtml(filtroCert.busca)}</strong>".
            </div>`;
        return;
    }

    filtrados.forEach(t => container.appendChild(montarCard(t)));
}

function bindFiltros() {
    const busca = document.getElementById("buscaCertificado");
    if (busca) {
        busca.addEventListener("input", (e) => {
            filtroCert.busca = e.target.value;
            renderCertificados();
        });
    }
}

async function inicializar() {
    if (!currentUser?.cpf) return;

    if (currentUser.tipoUsuario && currentUser.tipoUsuario !== "PROFESSOR") {
        document.getElementById("certificadosContainer").innerHTML = `
            <div class="secao-vazia" style="grid-column: 1 / -1;">
                Esta página é exclusiva para professores.
            </div>`;
        document.getElementById("contagemCertificados").textContent = "—";
        return;
    }

    try {
        const professor = await carregarPerfilProfessor();
        certificadosBase = Array.isArray(professor.certificados) ? professor.certificados : [];
        nomeProfessor = professor.nome ?? currentUser.nome ?? "";

        const resumo = document.getElementById("resumoProfessor");
        if (resumo) {
            resumo.style.display = "block";
            document.getElementById("resumoNome").textContent = nomeProfessor || "Professor";
            const cursos = Array.isArray(professor.cursosLecionados) ? professor.cursosLecionados.length : 0;
            document.getElementById("resumoSub").textContent =
                `${certificadosBase.length} ${certificadosBase.length === 1 ? "certificação" : "certificações"} · ${cursos} ${cursos === 1 ? "curso lecionado" : "cursos lecionados"}`;
        }

        bindFiltros();
        renderCertificados();
    } catch (err) {
        console.error("Erro ao buscar certificados do professor:", err);
        document.getElementById("certificadosContainer").innerHTML = `
            <div class="secao-vazia" style="grid-column: 1 / -1; color:#fca5a5;">
                Erro ao carregar certificados: ${escapeHtml(err.message)}
            </div>`;
    }
}

document.addEventListener("DOMContentLoaded", inicializar);
