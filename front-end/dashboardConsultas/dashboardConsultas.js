const API = "http://localhost:8080/api/v1";

let cursosCompras = [];
let comprasFiltradas = [];

let abandonoBase = [];
let abandonoFiltrado = [];

let chartReceita = null;
let chartCompras = null;
let chartAbandonoCurso = null;
let chartAbandonoPizza = null;

let sortKeyCompras = "receita_estimada";
let sortDirCompras = "desc";

const PALETA = [
    "#38bdf8", "#fbbf24", "#f87171", "#34d399", "#a78bfa",
    "#fb923c", "#f472b6", "#22d3ee", "#facc15", "#4ade80"
];

function fetchAuth(url) {
    return fetch(url, {
        headers: {
            "Accept": "application/json",
            "X-User-CPF": currentUser?.cpf ?? ""
        }
    });
}

function formatarMoeda(valor) {
    const n = Number(valor);
    if (!isFinite(n)) return "R$ —";
    return n.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarNumero(valor) {
    const n = Number(valor);
    if (!isFinite(n)) return "—";
    return n.toLocaleString("pt-BR");
}

function formatarCpf(cpf) {
    if (!cpf) return "—";
    const limpo = String(cpf).replace(/\D/g, "");
    if (limpo.length !== 11) return cpf;
    return `${limpo.slice(0, 3)}.${limpo.slice(3, 6)}.${limpo.slice(6, 9)}-${limpo.slice(9, 11)}`;
}

function classeAbandono(qtd) {
    const n = Number(qtd);
    if (!isFinite(n)) return "badge-abandono-leve";
    if (n >= 10) return "badge-abandono-grave";
    if (n >= 5) return "badge-abandono-medio";
    return "badge-abandono-leve";
}

// ====== Carregamento ======

async function carregarCursosCompras() {
    const res = await fetchAuth(`${API}/curso/curso/compras`);
    if (!res.ok) throw new Error(`Falha ao buscar cursos com compras (HTTP ${res.status})`);
    return await res.json();
}

async function carregarAbandono() {
    const res = await fetchAuth(`${API}/users/aluno/aulas-nao-assistidas`);
    if (!res.ok) throw new Error(`Falha ao buscar anti-join de aulas (HTTP ${res.status})`);
    return await res.json();
}

// ====== KPIs ======

function atualizarKpis() {
    const totalCursosVendidos = cursosCompras.length;
    const receitaTotal = cursosCompras.reduce((acc, c) => acc + Number(c.receita_estimada ?? 0), 0);
    const totalCompras = cursosCompras.reduce((acc, c) => acc + Number(c.total_compras ?? 0), 0);
    const semAulas = abandonoBase.length;

    document.getElementById("kpiCursosVendidos").textContent = totalCursosVendidos;
    document.getElementById("kpiReceita").textContent = formatarMoeda(receitaTotal);
    document.getElementById("kpiCompras").textContent = formatarNumero(totalCompras);
    document.getElementById("kpiSemAulas").textContent = formatarNumero(semAulas);
}

// ====== Consulta 1: JOIN + GROUP BY + HAVING ======

function aplicarFiltrosCompras() {
    const busca = document.getElementById("filtroBuscaCurso").value.trim().toLowerCase();
    const minCompras = Number(document.getElementById("filtroMinCompras").value || 0);

    comprasFiltradas = cursosCompras.filter(c => {
        if (busca && !(c.nome_curso ?? "").toLowerCase().includes(busca)) return false;
        if (minCompras > 0 && Number(c.total_compras ?? 0) < minCompras) return false;
        return true;
    });

    aplicarOrdenacaoCompras();
    renderChartReceita();
    renderChartCompras();
    renderTabelaCompras();
}

function aplicarOrdenacaoCompras() {
    const opt = document.getElementById("ordenarCompras").value;
    const [chave, dir] = opt.split("-");
    const mult = dir === "asc" ? 1 : -1;

    const mapa = {
        "receita": "receita_estimada",
        "compras": "total_compras",
        "preco":   "preco",
        "nome":    "nome_curso"
    };
    const campo = mapa[chave] ?? "receita_estimada";

    comprasFiltradas.sort((a, b) => {
        const va = a[campo];
        const vb = b[campo];
        if (campo === "nome_curso") {
            return String(va ?? "").localeCompare(String(vb ?? ""), "pt-BR") * mult;
        }
        return (Number(va ?? 0) - Number(vb ?? 0)) * mult;
    });

    sortKeyCompras = campo;
    sortDirCompras = dir;
}

function renderChartReceita() {
    const top = comprasFiltradas.slice(0, 10);
    const labels = top.map(c => c.nome_curso ?? "—");
    const valores = top.map(c => Number(c.receita_estimada ?? 0));

    if (chartReceita) chartReceita.destroy();
    const ctx = document.getElementById("chartReceita");
    if (!ctx) return;

    chartReceita = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels.length ? labels : ["Sem dados"],
            datasets: [{
                label: "Receita estimada (R$)",
                data: labels.length ? valores : [0],
                backgroundColor: labels.map((_, i) => PALETA[i % PALETA.length]),
                borderRadius: 4
            }]
        },
        options: {
            indexAxis: "y",
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: (item) => formatarMoeda(item.parsed.x)
                    }
                }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: {
                        color: "#cbd5e1",
                        callback: (v) => formatarMoeda(v)
                    },
                    grid: { color: "rgba(255,255,255,0.05)" }
                },
                y: { ticks: { color: "#cbd5e1" }, grid: { display: false } }
            }
        }
    });
}

function renderChartCompras() {
    const top = comprasFiltradas.slice(0, 10);
    const labels = top.map(c => c.nome_curso ?? "—");
    const valores = top.map(c => Number(c.total_compras ?? 0));

    if (chartCompras) chartCompras.destroy();
    const ctx = document.getElementById("chartCompras");
    if (!ctx) return;

    chartCompras = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels.length ? labels : ["Sem dados"],
            datasets: [{
                label: "Compras",
                data: labels.length ? valores : [0],
                backgroundColor: "#38bdf8",
                borderRadius: 4
            }]
        },
        options: {
            indexAxis: "y",
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: { color: "#cbd5e1", precision: 0 },
                    grid: { color: "rgba(255,255,255,0.05)" }
                },
                y: { ticks: { color: "#cbd5e1" }, grid: { display: false } }
            }
        }
    });
}

function renderTabelaCompras() {
    const wrapper = document.getElementById("tabelaComprasWrapper");
    const contagem = document.getElementById("contagemCompras");
    if (!wrapper) return;

    contagem.textContent =
        `${comprasFiltradas.length} ${comprasFiltradas.length === 1 ? "curso" : "cursos"}`;

    if (comprasFiltradas.length === 0) {
        wrapper.innerHTML = `<p class="placeholder">Nenhum curso bateu com os filtros.</p>`;
        return;
    }

    const linhas = comprasFiltradas.map(c => `
        <tr>
            <td>${c.nome_curso ?? "—"}</td>
            <td class="numero">${formatarMoeda(c.preco)}</td>
            <td class="numero">${formatarNumero(c.total_compras)}</td>
            <td class="numero"><span class="badge badge-receita">${formatarMoeda(c.receita_estimada)}</span></td>
        </tr>
    `).join("");

    wrapper.innerHTML = `
        <table class="tabela">
            <thead>
                <tr>
                    <th>Curso</th>
                    <th class="numero" style="text-align:right;">Preço</th>
                    <th class="numero" style="text-align:right;">Compras</th>
                    <th class="numero" style="text-align:right;">Receita estimada</th>
                </tr>
            </thead>
            <tbody>${linhas}</tbody>
        </table>
    `;
}

// ====== Consulta 4: ANTI JOIN ======

function popularDropdownCursoAbandono() {
    const sel = document.getElementById("filtroCursoAbandono");
    const cursos = [...new Map(
        abandonoBase.map(a => [String(a.idCurso), a.nome_curso ?? "—"])
    ).entries()].sort((a, b) => a[1].localeCompare(b[1], "pt-BR"));

    sel.innerHTML = `<option value="">Todos os cursos</option>`;
    cursos.forEach(([id, nome]) => {
        const opt = document.createElement("option");
        opt.value = id;
        opt.textContent = nome;
        sel.appendChild(opt);
    });
}

function aplicarFiltrosAbandono() {
    const idCurso = document.getElementById("filtroCursoAbandono").value;
    const buscaCpf = document.getElementById("filtroBuscaCpf").value.trim().toLowerCase();

    abandonoFiltrado = abandonoBase.filter(a => {
        if (idCurso && String(a.idCurso) !== String(idCurso)) return false;
        if (buscaCpf) {
            const cpf = (a.cpf ?? "").toLowerCase();
            if (!cpf.includes(buscaCpf)) return false;
        }
        return true;
    });

    renderChartAbandonoCurso();
    renderChartAbandonoPizza();
    renderTabelaAbandono();
}

function agruparAbandonoPorCurso(lista) {
    const mapa = new Map();
    lista.forEach(item => {
        const k = item.nome_curso ?? "(sem nome)";
        mapa.set(k, (mapa.get(k) || 0) + 1);
    });
    return [...mapa.entries()].sort((a, b) => b[1] - a[1]);
}

function renderChartAbandonoCurso() {
    const ranking = agruparAbandonoPorCurso(abandonoFiltrado).slice(0, 10);
    const labels = ranking.map(([nome]) => nome);
    const valores = ranking.map(([, qtd]) => qtd);

    if (chartAbandonoCurso) chartAbandonoCurso.destroy();
    const ctx = document.getElementById("chartAbandonoCurso");
    if (!ctx) return;

    chartAbandonoCurso = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels.length ? labels : ["Sem dados"],
            datasets: [{
                label: "Alunos sem aulas",
                data: labels.length ? valores : [0],
                backgroundColor: "#f87171",
                borderRadius: 4
            }]
        },
        options: {
            indexAxis: "y",
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: { color: "#cbd5e1", precision: 0 },
                    grid: { color: "rgba(255,255,255,0.05)" }
                },
                y: { ticks: { color: "#cbd5e1" }, grid: { display: false } }
            }
        }
    });
}

function renderChartAbandonoPizza() {
    const ranking = agruparAbandonoPorCurso(abandonoFiltrado);
    const labels = ranking.map(([nome]) => nome);
    const valores = ranking.map(([, qtd]) => qtd);

    if (chartAbandonoPizza) chartAbandonoPizza.destroy();
    const ctx = document.getElementById("chartAbandonoPizza");
    if (!ctx) return;

    if (labels.length === 0) {
        chartAbandonoPizza = new Chart(ctx, {
            type: "doughnut",
            data: { labels: ["Sem dados"], datasets: [{ data: [1], backgroundColor: ["#1e293b"] }] },
            options: { plugins: { legend: { labels: { color: "#94a3b8" } } } }
        });
        return;
    }

    chartAbandonoPizza = new Chart(ctx, {
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

function renderTabelaAbandono() {
    const wrapper = document.getElementById("tabelaAbandonoWrapper");
    const contagem = document.getElementById("contagemAbandono");
    if (!wrapper) return;

    contagem.textContent =
        `${abandonoFiltrado.length} ${abandonoFiltrado.length === 1 ? "registro" : "registros"}`;

    if (abandonoFiltrado.length === 0) {
        wrapper.innerHTML = `<p class="placeholder">Nenhum aluno bateu com os filtros.</p>`;
        return;
    }

    // Agrupar por curso para exibir resumo + lista de CPFs
    const grupos = new Map();
    abandonoFiltrado.forEach(item => {
        const chave = `${item.idCurso}::${item.nome_curso ?? "(sem nome)"}`;
        if (!grupos.has(chave)) {
            grupos.set(chave, {
                idCurso: item.idCurso,
                nomeCurso: item.nome_curso ?? "—",
                cpfs: []
            });
        }
        grupos.get(chave).cpfs.push(item.cpf);
    });

    const linhas = [...grupos.values()]
        .sort((a, b) => b.cpfs.length - a.cpfs.length)
        .map(g => `
            <tr>
                <td>${g.nomeCurso}</td>
                <td class="numero">
                    <span class="badge ${classeAbandono(g.cpfs.length)}">${g.cpfs.length}</span>
                </td>
                <td>
                    <details>
                        <summary style="cursor:pointer; color:#94a3b8;">Ver CPFs</summary>
                        <div style="margin-top:6px; font-size:0.85em; color:#cbd5e1;">
                            ${g.cpfs.map(c => formatarCpf(c)).join("<br>")}
                        </div>
                    </details>
                </td>
            </tr>
        `).join("");

    wrapper.innerHTML = `
        <table class="tabela">
            <thead>
                <tr>
                    <th>Curso</th>
                    <th class="numero" style="text-align:right;">Alunos sem aulas</th>
                    <th>Detalhes</th>
                </tr>
            </thead>
            <tbody>${linhas}</tbody>
        </table>
    `;
}

// ====== Bind dos filtros ======

function bindFiltros() {
    document.getElementById("filtroBuscaCurso").addEventListener("input", aplicarFiltrosCompras);
    document.getElementById("filtroMinCompras").addEventListener("input", aplicarFiltrosCompras);
    document.getElementById("ordenarCompras").addEventListener("change", aplicarFiltrosCompras);
    document.getElementById("btnResetarCompras").addEventListener("click", () => {
        document.getElementById("filtroBuscaCurso").value = "";
        document.getElementById("filtroMinCompras").value = "";
        document.getElementById("ordenarCompras").value = "receita-desc";
        aplicarFiltrosCompras();
    });

    document.getElementById("filtroCursoAbandono").addEventListener("change", aplicarFiltrosAbandono);
    document.getElementById("filtroBuscaCpf").addEventListener("input", aplicarFiltrosAbandono);
    document.getElementById("btnResetarAbandono").addEventListener("click", () => {
        document.getElementById("filtroCursoAbandono").value = "";
        document.getElementById("filtroBuscaCpf").value = "";
        aplicarFiltrosAbandono();
    });
}

// ====== Inicialização ======

async function inicializar() {
    if (!currentUser?.cpf) {
        document.getElementById("tabelaComprasWrapper").innerHTML =
            `<p class="erro">Usuário não autenticado.</p>`;
        document.getElementById("tabelaAbandonoWrapper").innerHTML =
            `<p class="erro">Usuário não autenticado.</p>`;
        return;
    }

    try {
        const [compras, abandono] = await Promise.all([
            carregarCursosCompras(),
            carregarAbandono()
        ]);

        cursosCompras = compras ?? [];
        abandonoBase = abandono ?? [];

        atualizarKpis();
        popularDropdownCursoAbandono();
        bindFiltros();
        aplicarFiltrosCompras();
        aplicarFiltrosAbandono();

    } catch (err) {
        console.error(err);
        document.getElementById("tabelaComprasWrapper").innerHTML =
            `<p class="erro">Erro ao carregar dados: ${err.message}</p>`;
        document.getElementById("tabelaAbandonoWrapper").innerHTML =
            `<p class="erro">Erro ao carregar dados: ${err.message}</p>`;
    }
}

document.addEventListener("DOMContentLoaded", inicializar);
