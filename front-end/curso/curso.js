const API = "http://localhost:8080/api/v1";

/* ============================================================
 * Helpers
 * ============================================================ */
function getCpfLogado() {
    try {
        const raw = localStorage.getItem("user");
        if (!raw) return "";
        const user = JSON.parse(raw);
        return user?.cpf ?? "";
    } catch {
        return "";
    }
}

function fetchComCpf(url, options = {}) {
    const headers = {
        "Accept": "application/json",
        ...(options.headers ?? {}),
        "X-User-CPF": getCpfLogado()
    };
    return fetch(url, { ...options, headers });
}

function getCursoId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function converterYoutubeParaEmbed(url) {
    if (!url) return "";
    if (url.includes("watch?v=")) {
        const videoId = url.split("v=")[1]?.split("&")[0];
        return `https://www.youtube.com/embed/${videoId}`;
    }
    if (url.includes("youtu.be/")) {
        const videoId = url.split("youtu.be/")[1]?.split("?")[0];
        return `https://www.youtube.com/embed/${videoId}`;
    }
    if (url.includes("/embed/")) return url;
    return url;
}

/* ============================================================
 * Estado da página
 * ============================================================ */
let estadoCurso = {
    idCurso: null,
    nome: "",
    modulos: [],          // [{ modulo, aulas: [{...}] }]
    aulasFlat: [],        // lista plana de aulas em ordem
    indiceAtual: -1,
    assistidasSet: new Set(), // ids de aulas marcadas como assistidas (localStorage)
    busca: ""
};

const STORAGE_KEY_PREFIX = "cesar.assistidas.";

function carregarAssistidasLocal(idCurso) {
    try {
        const raw = localStorage.getItem(STORAGE_KEY_PREFIX + idCurso);
        if (!raw) return new Set();
        const arr = JSON.parse(raw);
        return new Set(Array.isArray(arr) ? arr : []);
    } catch {
        return new Set();
    }
}

function salvarAssistidasLocal(idCurso, set) {
    try {
        localStorage.setItem(STORAGE_KEY_PREFIX + idCurso, JSON.stringify([...set]));
    } catch { /* ignore */ }
}

function aulaKey(aula) {
    // composta porque o id_aula é único só dentro de (curso, modulo)
    return `${aula.id_curso}-${aula.id_modulo}-${aula.id_aula}`;
}

/* ============================================================
 * Carregamento de dados
 * ============================================================ */
async function carregarCargaHoraria(idCurso) {
    try {
        const response = await fetchComCpf(`${API}/curso/curso-horas-totais/${idCurso}`);
        if (!response.ok) throw new Error("Erro ao buscar carga horária");
        const horasTotais = await response.text();
        const el = document.getElementById("cursoCargaHoraria");
        const elTopo = document.getElementById("cursoHoras");
        if (el) el.innerHTML = `🕒 Carga horária total: <strong style="color:#38bdf8;">${horasTotais}h</strong>`;
        if (elTopo) elTopo.textContent = `${horasTotais}h totais`;
    } catch (err) {
        console.error("Não foi possível carregar a carga horária:", err);
    }
}

async function carregarInfoCurso(idCurso) {
    try {
        const res = await fetchComCpf(`${API}/curso/${idCurso}`);
        if (!res.ok) return null;
        return await res.json();
    } catch (err) {
        console.error("Falha ao buscar curso:", err);
        return null;
    }
}

async function buscarDetalheAula(idCurso, idModulo, aulaResumo) {
    const idAula = aulaResumo?.id_aula;
    if (idAula == null || idAula <= 0 || idModulo == null) {
        return { ...aulaResumo, id_curso: idCurso, id_modulo: idModulo };
    }
    try {
        const res = await fetchComCpf(`${API}/aula/get/${idCurso}/${idModulo}/${idAula}`);
        if (!res.ok) {
            const motivo = await res.text().catch(() => "");
            throw new Error(`Falha ao buscar aula ${idAula} (${res.status}): ${motivo}`);
        }
        const detalhe = await res.json();
        return {
            ...aulaResumo,
            id_curso: idCurso,
            id_modulo: idModulo,
            link: detalhe.link_do_video,
            descricao_aula: detalhe.descricao
        };
    } catch (err) {
        console.error(err);
        return { ...aulaResumo, id_curso: idCurso, id_modulo: idModulo };
    }
}

async function buscarDetalheModulo(idCurso, item) {
    const idModulo = item.modulo.id_modulo;
    let moduloCompleto = { ...item.modulo, id_curso: idCurso };
    try {
        const res = await fetchComCpf(`${API}/modulo/get/${idCurso}/${idModulo}`);
        if (res.ok) {
            const detalhe = await res.json();
            moduloCompleto = {
                ...moduloCompleto,
                titulo: detalhe.titulo ?? moduloCompleto.titulo,
                cargaHoraria: detalhe.carga_horaria,
                descricao_modulo: detalhe.descricao
            };
        }
    } catch (err) {
        console.error("Falha ao buscar módulo " + idModulo, err);
    }
    const aulas = await Promise.all(
        (item.aulas ?? []).map(a => buscarDetalheAula(idCurso, idModulo, a))
    );
    return { modulo: moduloCompleto, aulas };
}

/* ============================================================
 * Render — sidebar
 * ============================================================ */
function renderSidebar() {
    const container = document.getElementById("modulosContainer");
    if (!container) return;
    container.innerHTML = "";

    const termo = (estadoCurso.busca ?? "").trim().toLowerCase();

    let totalVisiveis = 0;

    estadoCurso.modulos.forEach((item, idxModulo) => {
        const aulasFiltradas = (item.aulas ?? []).filter(a => {
            if (!termo) return true;
            return (a.titulo ?? "").toLowerCase().includes(termo);
        });

        if (termo && aulasFiltradas.length === 0) return;

        const block = document.createElement("div");
        block.className = "modulo-block aberto";

        const totalAulas = (item.aulas ?? []).length;
        const assistidas = (item.aulas ?? []).filter(a => estadoCurso.assistidasSet.has(aulaKey(a))).length;

        block.innerHTML = `
            <div class="modulo-header">
                <div class="nome">📦 ${escapeHtml(item.modulo.titulo ?? "(sem título)")}</div>
                <div style="display:flex; align-items:center; gap:10px;">
                    <span class="stats">${assistidas}/${totalAulas}</span>
                    <span class="seta">▶</span>
                </div>
            </div>
            <div class="modulo-aulas">
                ${item.modulo.descricao_modulo ? `<div class="modulo-descricao">${escapeHtml(item.modulo.descricao_modulo)}</div>` : ''}
            </div>
        `;

        const headerEl = block.querySelector(".modulo-header");
        const aulasEl = block.querySelector(".modulo-aulas");

        headerEl.addEventListener("click", () => block.classList.toggle("aberto"));

        aulasFiltradas.forEach(aula => {
            const idx = estadoCurso.aulasFlat.findIndex(a =>
                a.id_aula === aula.id_aula && a.id_modulo === aula.id_modulo
            );

            const aulaDiv = document.createElement("div");
            aulaDiv.className = "aula-item";
            if (estadoCurso.assistidasSet.has(aulaKey(aula))) aulaDiv.classList.add("assistida");
            if (idx === estadoCurso.indiceAtual) aulaDiv.classList.add("ativa");

            aulaDiv.innerHTML = `
                <span class="check"></span>
                <span class="titulo">${escapeHtml(aula.titulo ?? "(sem título)")}</span>
            `;

            aulaDiv.addEventListener("click", () => carregarAulaPorIndice(idx));
            aulasEl.appendChild(aulaDiv);
            totalVisiveis++;
        });

        container.appendChild(block);
    });

    if (totalVisiveis === 0) {
        container.innerHTML = `<p style="padding: 20px; color: #94a3b8; font-size: 0.88em; text-align: center;">
            ${termo ? `Nenhuma aula encontrada com "${escapeHtml(estadoCurso.busca)}".` : "Nenhuma aula disponível para este curso."}
        </p>`;
    }
}

/* ============================================================
 * Render — header / progresso
 * ============================================================ */
function renderProgresso() {
    const total = estadoCurso.aulasFlat.length;
    const assistidas = estadoCurso.aulasFlat.filter(a => estadoCurso.assistidasSet.has(aulaKey(a))).length;
    const pct = total === 0 ? 0 : Math.round((assistidas / total) * 100);

    const lbl = document.getElementById("progressoLabel");
    const fill = document.getElementById("progressoFill");
    const pctEl = document.getElementById("progressoPct");
    if (lbl) lbl.textContent = `${assistidas} / ${total} aulas`;
    if (fill) fill.style.width = `${pct}%`;
    if (pctEl) pctEl.textContent = `${pct}%`;
}

function renderHeaderCurso(curso) {
    const nomeEl = document.getElementById("cursoNome");
    const catEl = document.getElementById("cursoCategorias");
    const badgeEl = document.getElementById("cursoBadge");

    if (curso) {
        if (nomeEl) nomeEl.textContent = curso.nome_curso ?? "(sem nome)";
        estadoCurso.nome = curso.nome_curso ?? "";
        const cats = (curso.categorias ?? []).map(c => c.nome).filter(Boolean);
        if (catEl) catEl.textContent = cats.length ? cats.join(", ") : "Sem categoria";
    } else {
        if (nomeEl) nomeEl.textContent = "Curso #" + (estadoCurso.idCurso ?? "?");
        if (catEl) catEl.textContent = "—";
    }
    if (badgeEl) badgeEl.style.display = "inline-block";

    const totalMod = estadoCurso.modulos.length;
    const totalAul = estadoCurso.aulasFlat.length;
    const modEl = document.getElementById("cursoModulosCount");
    const aulEl = document.getElementById("cursoAulasCount");
    if (modEl) modEl.textContent = `${totalMod} ${totalMod === 1 ? "módulo" : "módulos"}`;
    if (aulEl) aulEl.textContent = `${totalAul} ${totalAul === 1 ? "aula" : "aulas"}`;
}

/* ============================================================
 * Render — aula atual
 * ============================================================ */
function carregarAulaPorIndice(idx) {
    if (idx < 0 || idx >= estadoCurso.aulasFlat.length) return;
    estadoCurso.indiceAtual = idx;
    const aula = estadoCurso.aulasFlat[idx];

    const videoUrl = converterYoutubeParaEmbed(aula.link ?? "");
    const videoEl = document.getElementById("video");
    if (videoUrl) {
        videoEl.innerHTML = `
            <iframe
                src="${videoUrl}"
                title="${escapeHtml(aula.titulo ?? "Vídeo da aula")}"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowfullscreen></iframe>`;
    } else {
        videoEl.innerHTML = `<div style="display:flex; align-items:center; justify-content:center; height:100%; color:#64748b;">
            Esta aula ainda não possui vídeo.
        </div>`;
    }

    document.getElementById("tituloAula").innerText = aula.titulo ?? "";
    document.getElementById("descricaoAula").innerText = aula.descricao_aula ?? "";

    const moduloDoAula = estadoCurso.modulos.find(m =>
        (m.aulas ?? []).some(a => a.id_aula === aula.id_aula && a.id_modulo === aula.id_modulo)
    );
    document.getElementById("aulaModulo").textContent = moduloDoAula?.modulo?.titulo ?? "—";
    document.getElementById("aulaIndice").textContent = `${idx + 1} de ${estadoCurso.aulasFlat.length}`;

    // Materiais
    const matDiv = document.getElementById("materiais");
    matDiv.innerHTML = "";
    if (aula.materiais && aula.materiais.length > 0) {
        aula.materiais.forEach(m => {
            const a = document.createElement("a");
            a.href = m.link;
            a.innerHTML = `<span>📎</span><span>${escapeHtml(m.nome ?? "Material")}</span>`;
            a.target = "_blank";
            a.rel = "noopener";
            matDiv.appendChild(a);
        });
    } else {
        matDiv.innerHTML = "<p style='color: #64748b; font-size: 0.9em;'>Nenhum material de apoio para esta aula.</p>";
    }

    // Botões nav
    const btnAnt = document.getElementById("btnAnterior");
    const btnProx = document.getElementById("btnProxima");
    const btnMarc = document.getElementById("btnMarcar");
    if (btnAnt) btnAnt.disabled = idx === 0;
    if (btnProx) btnProx.disabled = idx === estadoCurso.aulasFlat.length - 1;
    if (btnMarc) {
        btnMarc.disabled = false;
        const isAssistida = estadoCurso.assistidasSet.has(aulaKey(aula));
        btnMarc.classList.toggle("assistida", isAssistida);
        btnMarc.innerHTML = isAssistida ? "✓ Assistida (clique para desmarcar)" : "✓ Marcar como assistida";
    }

    renderSidebar();
}

function navegarAula(delta) {
    const novo = estadoCurso.indiceAtual + delta;
    if (novo >= 0 && novo < estadoCurso.aulasFlat.length) {
        carregarAulaPorIndice(novo);
    }
}

async function registrarAssistirNoBackend(aula) {
    const cpf = getCpfLogado();
    if (!cpf) throw new Error("CPF do aluno não encontrado.");

    const payload = {
        cpf_aluno: cpf,
        id_aula: aula.id_aula,
        id_modulo: aula.id_modulo,
        id_curso: aula.id_curso
    };

    const res = await fetchComCpf(`${API}/assistir`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!res.ok) {
        const motivo = await res.text().catch(() => "");
        throw new Error(motivo || `HTTP ${res.status}`);
    }
}

async function alternarAssistida() {
    if (estadoCurso.indiceAtual < 0) return;
    const aula = estadoCurso.aulasFlat[estadoCurso.indiceAtual];
    const key = aulaKey(aula);
    const btnMarc = document.getElementById("btnMarcar");

    if (estadoCurso.assistidasSet.has(key)) {
        // Desmarcar — somente local (sem endpoint DELETE).
        estadoCurso.assistidasSet.delete(key);
        salvarAssistidasLocal(estadoCurso.idCurso, estadoCurso.assistidasSet);
        carregarAulaPorIndice(estadoCurso.indiceAtual);
        renderProgresso();
        return;
    }

    // Marcar como assistida — registra no backend antes de atualizar a UI
    if (btnMarc) {
        btnMarc.disabled = true;
        btnMarc.innerHTML = "⏳ Registrando...";
    }

    try {
        await registrarAssistirNoBackend(aula);

        estadoCurso.assistidasSet.add(key);
        salvarAssistidasLocal(estadoCurso.idCurso, estadoCurso.assistidasSet);
        carregarAulaPorIndice(estadoCurso.indiceAtual);
        renderProgresso();

        // Auto-avança para a próxima aula
        if (estadoCurso.indiceAtual < estadoCurso.aulasFlat.length - 1) {
            setTimeout(() => navegarAula(1), 250);
        }
    } catch (err) {
        console.error("Falha ao registrar 'assistir' no backend:", err);
        alert(`Não foi possível registrar a aula como assistida: ${err.message}`);
        if (btnMarc) {
            btnMarc.disabled = false;
            btnMarc.innerHTML = "✓ Marcar como assistida";
        }
    }
}

/* ============================================================
 * Tabs e busca
 * ============================================================ */
function bindTabs() {
    document.querySelectorAll(".tab-bar .tab").forEach(tab => {
        tab.addEventListener("click", () => {
            const alvo = tab.dataset.tab;
            document.querySelectorAll(".tab-bar .tab").forEach(t => t.classList.remove("ativo"));
            document.querySelectorAll(".tab-content").forEach(t => t.classList.remove("ativo"));
            tab.classList.add("ativo");
            const content = document.getElementById("tab" + alvo.charAt(0).toUpperCase() + alvo.slice(1));
            if (content) content.classList.add("ativo");
        });
    });
}

function bindBuscaAula() {
    const input = document.getElementById("buscaAula");
    if (!input) return;
    input.addEventListener("input", (e) => {
        estadoCurso.busca = e.target.value;
        renderSidebar();
    });
}

function setarAvatar() {
    try {
        const u = JSON.parse(localStorage.getItem("user") ?? "{}");
        const inicial = (u?.nome ?? "U").charAt(0).toUpperCase();
        const avatarEl = document.getElementById("meuAvatar");
        if (avatarEl) avatarEl.textContent = inicial;
    } catch { /* ignore */ }
}

/* ============================================================
 * Inicialização
 * ============================================================ */
async function inicializarCurso() {
    const idCurso = getCursoId();
    estadoCurso.idCurso = idCurso;

    if (!idCurso) {
        const container = document.getElementById("modulosContainer");
        if (container) container.innerHTML = "<p style='padding:15px; color: #ef4444;'>Erro: Nenhum ID de curso foi especificado na URL.</p>";
        return;
    }

    setarAvatar();
    bindTabs();
    bindBuscaAula();

    estadoCurso.assistidasSet = carregarAssistidasLocal(idCurso);

    // dispara em paralelo
    const [curso] = await Promise.all([
        carregarInfoCurso(idCurso),
        carregarCargaHoraria(idCurso)
    ]);

    try {
        const response = await fetchComCpf(`${API}/curso/${idCurso}/modulos-aulas`);
        if (!response.ok) throw new Error(`Erro HTTP! Status: ${response.status}`);
        const esqueleto = await response.json();

        const modulos = await Promise.all(
            esqueleto.map(item => buscarDetalheModulo(idCurso, item))
        );
        console.log(modulos)

        estadoCurso.modulos = modulos;
        estadoCurso.aulasFlat = modulos.flatMap(m => m.aulas ?? []);

        renderHeaderCurso(curso);
        renderSidebar();
        renderProgresso();

        if (estadoCurso.aulasFlat.length > 0) {
            carregarAulaPorIndice(0);
        }
    } catch (error) {
        console.error("Não foi possível carregar os módulos do backend:", error);
        const container = document.getElementById("modulosContainer");
        if (container) container.innerHTML = "<p style='padding:15px; color: #ef4444;'>Erro ao carregar os módulos. Verifique o servidor.</p>";
    }
}

inicializarCurso();
