const API = "http://localhost:8080/api/v1";

let cursosProfessor = [];      // lista de CursoResponse do professor
let idsCursosProfessor = new Set();
let inativosBase = [];         // inativos cruzados com cursos do professor
let inativosFiltrados = [];
let semAulaBase = [];          // alunos que compraram mas não assistiram, cruzados com cursos do professor

let chartCurso  = null;
let chartMotivo = null;
let chartDias   = null;

let sortKey = "diasInativo";
let sortDir = "desc";

function fetchAuth(url) {
    return fetch(url, {
        headers: {
            "Accept": "application/json",
            "X-User-CPF": currentUser?.cpf ?? ""
        }
    });
}

function formatarData(iso) {
    if (!iso) return "—";
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso;
    return d.toLocaleDateString("pt-BR", { day: "2-digit", month: "2-digit", year: "numeric" });
}

function classeDias(dias) {
    const n = Number(dias);
    if (!isFinite(n)) return "dias-leve";
    if (n >= 60) return "dias-grave";
    if (n >= 30) return "dias-medio";
    return "dias-leve";
}

async function carregarCursosProfessor() {
    const res = await fetchAuth(`${API}/users/professor/${currentUser.cpf}`);
    if (!res.ok) throw new Error(`Falha ao buscar professor (HTTP ${res.status})`);
    const professor = await res.json();
    return professor.cursosLecionados ?? [];
}

async function carregarAlunosInativos() {
    const res = await fetchAuth(`${API}/users/alunos/alunos-inativos`);
    if (!res.ok) throw new Error(`Falha ao buscar inativos (HTTP ${res.status})`);
    return await res.json();
}

async function carregarAulasNaoAssistidas() {
    const res = await fetchAuth(`${API}/users/aluno/aulas-nao-assistidas`);
    if (!res.ok) throw new Error(`Falha ao buscar aulas não assistidas (HTTP ${res.status})`);
    return await res.json();
}

function popularDropdownCurso() {
    const sel = document.getElementById("filtroCurso");
    sel.innerHTML = `<option value="">Todos os meus cursos</option>`;
    cursosProfessor.forEach(c => {
        const opt = document.createElement("option");
        opt.value = c.id_curso;
        opt.textContent = c.nome_curso;
        sel.appendChild(opt);
    });
}

function popularDropdownMotivo() {
    const sel = document.getElementById("filtroMotivo");
    const motivos = [...new Set(inativosBase.map(i => i.motivo).filter(Boolean))].sort();
    sel.innerHTML = `<option value="">Todos</option>`;
    motivos.forEach(m => {
        const opt = document.createElement("option");
        opt.value = m;
        opt.textContent = m;
        sel.appendChild(opt);
    });
}

function aplicarFiltros() {
    const idCurso = document.getElementById("filtroCurso").value;
    const motivo  = document.getElementById("filtroMotivo").value;
    const minDias = Number(document.getElementById("filtroDiasMin").value || 0);
    const busca   = document.getElementById("filtroBusca").value.trim().toLowerCase();

    inativosFiltrados = inativosBase.filter(i => {
        if (idCurso && String(i.idCurso) !== String(idCurso)) return false;
        if (motivo && i.motivo !== motivo) return false;
        if (minDias > 0 && Number(i.diasInativo ?? 0) < minDias) return false;
        if (busca) {
            const nome = (i.nomeAluno ?? "").toLowerCase();
            const cpf  = (i.cpfAluno ?? "").toLowerCase();
            if (!nome.includes(busca) && !cpf.includes(busca)) return false;
        }
        return true;
    });

    atualizarKpis();
    renderChartCurso();
    renderChartMotivo();
    renderChartDias();
    renderTabela();
    renderTabelaSemAula();
}

function atualizarKpis() {
    const total = inativosFiltrados.length;
    document.getElementById("kpiTotal").textContent = total;
    document.getElementById("kpiTotalSub").textContent =
        total === 0 ? "nenhum aluno inativo" : `${total === 1 ? "aluno inativo" : "alunos inativos"}`;

    const cursosAfetados = new Set(inativosFiltrados.map(i => i.idCurso)).size;
    document.getElementById("kpiCursos").textContent = cursosAfetados;
    document.getElementById("kpiCursosSub").textContent =
        `de ${cursosProfessor.length} lecionado${cursosProfessor.length === 1 ? "" : "s"}`;

    const dias = inativosFiltrados.map(i => Number(i.diasInativo ?? 0)).filter(isFinite);
    const media = dias.length ? (dias.reduce((a, b) => a + b, 0) / dias.length) : 0;
    const maior = dias.length ? Math.max(...dias) : 0;

    document.getElementById("kpiMedia").textContent = dias.length ? media.toFixed(1) : "—";
    document.getElementById("kpiPior").textContent  = dias.length ? maior : "—";

    const piorAluno = dias.length
        ? inativosFiltrados.find(i => Number(i.diasInativo) === maior)
        : null;
    document.getElementById("kpiPiorSub").textContent =
        piorAluno ? `dias — ${piorAluno.nomeAluno}` : "dias";

    const idCurso = document.getElementById("filtroCurso").value;
    const semAulaFiltrado = idCurso
        ? semAulaBase.filter(s => String(s.idCurso) === String(idCurso))
        : semAulaBase;
    document.getElementById("kpiSemAula").textContent = semAulaFiltrado.length;
    document.getElementById("kpiSemAulaSub").textContent =
        semAulaFiltrado.length === 1 ? "aluno sem 1ª aula" : "alunos sem 1ª aula";
}

function agruparPor(lista, chave) {
    const mapa = new Map();
    lista.forEach(item => {
        const k = item[chave] ?? "(sem)";
        mapa.set(k, (mapa.get(k) || 0) + 1);
    });
    return mapa;
}

const PALETA = [
    "#38bdf8", "#fbbf24", "#f87171", "#34d399", "#a78bfa",
    "#fb923c", "#f472b6", "#22d3ee", "#facc15", "#4ade80"
];

function renderChartCurso() {
    const mapa = agruparPor(inativosFiltrados, "nomeCurso");
    const labels = [...mapa.keys()];
    const valores = [...mapa.values()];

    if (chartCurso) chartCurso.destroy();
    const ctx = document.getElementById("chartCurso");
    if (!ctx) return;

    if (labels.length === 0) {
        chartCurso = new Chart(ctx, {
            type: "doughnut",
            data: { labels: ["Sem dados"], datasets: [{ data: [1], backgroundColor: ["#1e293b"] }] },
            options: { plugins: { legend: { labels: { color: "#94a3b8" } } } }
        });
        return;
    }

    chartCurso = new Chart(ctx, {
        type: "doughnut",
        data: {
            labels,
            datasets: [{
                data: valores,
                backgroundColor: labels.map((_, i) => PALETA[i % PALETA.length]),
                borderColor: "#0f172a",
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: "right", labels: { color: "#cbd5e1", boxWidth: 14 } },
                tooltip: {
                    callbacks: {
                        label: (item) => {
                            const total = valores.reduce((a, b) => a + b, 0);
                            const pct = total ? ((item.parsed / total) * 100).toFixed(1) : 0;
                            return `${item.label}: ${item.parsed} (${pct}%)`;
                        }
                    }
                }
            }
        }
    });
}

function renderChartMotivo() {
    const mapa = agruparPor(inativosFiltrados, "motivo");
    const labels = [...mapa.keys()];
    const valores = [...mapa.values()];

    if (chartMotivo) chartMotivo.destroy();
    const ctx = document.getElementById("chartMotivo");
    if (!ctx) return;

    chartMotivo = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels.length ? labels : ["Sem dados"],
            datasets: [{
                label: "Alunos",
                data: labels.length ? valores : [0],
                backgroundColor: "#fbbf24",
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: "#cbd5e1" }, grid: { color: "rgba(255,255,255,0.05)" } },
                y: {
                    beginAtZero: true,
                    ticks: { color: "#94a3b8", precision: 0 },
                    grid: { color: "rgba(255,255,255,0.05)" }
                }
            }
        }
    });
}

function bucketizarDias(lista) {
    const buckets = { "0–14": 0, "15–29": 0, "30–59": 0, "60–89": 0, "90+": 0 };
    lista.forEach(i => {
        const d = Number(i.diasInativo ?? 0);
        if (d < 15)      buckets["0–14"]++;
        else if (d < 30) buckets["15–29"]++;
        else if (d < 60) buckets["30–59"]++;
        else if (d < 90) buckets["60–89"]++;
        else             buckets["90+"]++;
    });
    return buckets;
}

function renderChartDias() {
    const buckets = bucketizarDias(inativosFiltrados);
    const labels = Object.keys(buckets);
    const valores = Object.values(buckets);

    if (chartDias) chartDias.destroy();
    const ctx = document.getElementById("chartDias");
    if (!ctx) return;

    chartDias = new Chart(ctx, {
        type: "bar",
        data: {
            labels,
            datasets: [{
                label: "Alunos",
                data: valores,
                backgroundColor: ["#fde68a", "#fcd34d", "#fb923c", "#f87171", "#dc2626"],
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: "#cbd5e1" }, grid: { color: "rgba(255,255,255,0.05)" } },
                y: {
                    beginAtZero: true,
                    ticks: { color: "#94a3b8", precision: 0 },
                    grid: { color: "rgba(255,255,255,0.05)" }
                }
            }
        }
    });
}

function ordenarLista(lista) {
    const dir = sortDir === "asc" ? 1 : -1;
    const datas = ["dataCompra", "ultimaAulaAssistida", "dataReferenciaInatividade", "dataAtualizacao"];
    return [...lista].sort((a, b) => {
        const va = a[sortKey];
        const vb = b[sortKey];
        if (va == null && vb == null) return 0;
        if (va == null) return 1;
        if (vb == null) return -1;
        if (sortKey === "diasInativo") return (Number(va) - Number(vb)) * dir;
        if (datas.includes(sortKey))   return (new Date(va) - new Date(vb)) * dir;
        return String(va).localeCompare(String(vb), "pt-BR") * dir;
    });
}

function renderTabela() {
    const wrapper = document.getElementById("tabelaWrapper");
    const contagem = document.getElementById("contagemLista");
    if (!wrapper) return;

    contagem.textContent = `${inativosFiltrados.length} ${inativosFiltrados.length === 1 ? "registro" : "registros"}`;

    if (inativosFiltrados.length === 0) {
        wrapper.innerHTML = `<p class="placeholder">Nenhum aluno inativo bateu com os filtros.</p>`;
        return;
    }

    const ordenados = ordenarLista(inativosFiltrados);

    const headerCells = [
        ["nomeAluno",                "Aluno"],
        ["nomeCurso",                "Curso"],
        ["dataCompra",               "Compra"],
        ["ultimaAulaAssistida",      "Última aula"],
        ["diasInativo",              "Dias inativo"],
        ["motivo",                   "Motivo"],
        ["dataAtualizacao",          "Atualização"]
    ].map(([k, label]) => {
        const cls = sortKey === k ? (sortDir === "asc" ? "sort-asc" : "sort-desc") : "";
        return `<th data-key="${k}" class="${cls}">${label}</th>`;
    }).join("");

    const linhas = ordenados.map(i => `
        <tr>
            <td>
                <div>${i.nomeAluno ?? "—"}</div>
                <div style="font-size:0.78em; color:#64748b;">${i.cpfAluno ?? ""}</div>
            </td>
            <td>${i.nomeCurso ?? "—"}</td>
            <td>${formatarData(i.dataCompra)}</td>
            <td>${i.ultimaAulaAssistida ? formatarData(i.ultimaAulaAssistida) : `<span class="placeholder">nunca</span>`}</td>
            <td><span class="badge-dias ${classeDias(i.diasInativo)}">${i.diasInativo ?? 0} dias</span></td>
            <td>${i.motivo ?? "—"}</td>
            <td style="opacity:0.75;">${formatarData(i.dataAtualizacao)}</td>
        </tr>
    `).join("");

    wrapper.innerHTML = `
        <table class="tabela-inativos">
            <thead><tr>${headerCells}</tr></thead>
            <tbody>${linhas}</tbody>
        </table>
    `;

    wrapper.querySelectorAll("th[data-key]").forEach(th => {
        th.addEventListener("click", () => {
            const k = th.dataset.key;
            if (sortKey === k) {
                sortDir = sortDir === "asc" ? "desc" : "asc";
            } else {
                sortKey = k;
                sortDir = k === "diasInativo" ? "desc" : "asc";
            }
            renderTabela();
        });
    });
}

function renderTabelaSemAula() {
    const wrapper = document.getElementById("tabelaSemAulaWrapper");
    const contagem = document.getElementById("contagemSemAula");
    if (!wrapper) return;

    const idCurso = document.getElementById("filtroCurso").value;
    const busca = document.getElementById("filtroBusca").value.trim().toLowerCase();

    let lista = idCurso
        ? semAulaBase.filter(s => String(s.idCurso) === String(idCurso))
        : semAulaBase;

    if (busca) {
        lista = lista.filter(s => {
            const cpf = (s.cpf ?? "").toLowerCase();
            const nomeAluno = (s.nome_aluno ?? "").toLowerCase();
            const nomeCurso = (s.nome_curso ?? "").toLowerCase();
            return cpf.includes(busca) || nomeAluno.includes(busca) || nomeCurso.includes(busca);
        });
    }

    contagem.textContent = `${lista.length} ${lista.length === 1 ? "registro" : "registros"}`;

    if (lista.length === 0) {
        wrapper.innerHTML = `<p class="placeholder">Todos os alunos que compraram seus cursos já assistiram pelo menos uma aula.</p>`;
        return;
    }

    const linhas = lista.map(s => `
        <tr>
            <td>
                <div>${s.nome_aluno ?? "—"}</div>
                <div style="font-size:0.78em; color:#64748b;">${s.cpf ?? ""}</div>
            </td>
            <td>${s.nome_curso ?? "—"}</td>
        </tr>
    `).join("");

    wrapper.innerHTML = `
        <table class="tabela-inativos">
            <thead><tr><th>Aluno</th><th>Curso</th></tr></thead>
            <tbody>${linhas}</tbody>
        </table>
    `;
}

function bindFiltros() {
    ["filtroCurso", "filtroMotivo", "filtroDiasMin", "filtroBusca"].forEach(id => {
        const el = document.getElementById(id);
        const evento = (el.tagName === "SELECT") ? "change" : "input";
        el.addEventListener(evento, aplicarFiltros);
    });

    document.getElementById("btnResetar").addEventListener("click", () => {
        document.getElementById("filtroCurso").value = "";
        document.getElementById("filtroMotivo").value = "";
        document.getElementById("filtroDiasMin").value = "";
        document.getElementById("filtroBusca").value = "";
        aplicarFiltros();
    });
}

async function inicializar() {
    if (!currentUser?.cpf) {
        document.getElementById("tabelaWrapper").innerHTML =
            `<p class="erro">Usuário não autenticado.</p>`;
        return;
    }

    try {
        const [cursos, inativos, semAula] = await Promise.all([
            carregarCursosProfessor(),
            carregarAlunosInativos(),
            carregarAulasNaoAssistidas()
        ]);

        cursosProfessor = cursos;
        idsCursosProfessor = new Set(cursos.map(c => Number(c.id_curso)));

        inativosBase = (inativos ?? []).filter(i => idsCursosProfessor.has(Number(i.idCurso)));
        semAulaBase = (semAula ?? []).filter(s => idsCursosProfessor.has(Number(s.idCurso)));

        popularDropdownCurso();
        popularDropdownMotivo();
        bindFiltros();
        aplicarFiltros();

    } catch (err) {
        console.error(err);
        document.getElementById("tabelaWrapper").innerHTML =
            `<p class="erro">Erro ao carregar dados: ${err.message}</p>`;
    }
}

document.addEventListener("DOMContentLoaded", inicializar);
