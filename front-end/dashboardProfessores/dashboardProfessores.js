const API = "http://localhost:8080/api/v1";

let professoresAgg = [];
let professoresFiltrados = [];

let chartRanking = null;
let chartScatter = null;
let chartCertif  = null;
let chartReceita = null;

let sortKey = "totalAlunos";
let sortDir = "desc";

const COR_PADRAO  = "#38bdf8";
const COR_DESTAQUE = "#22c55e";

function fetchAuth(url) {
    return fetch(url, {
        headers: {
            "Accept": "application/json",
            "X-User-CPF": currentUser?.cpf ?? ""
        }
    });
}

function formatarBRL(valor) {
    const n = Number(valor);
    if (!isFinite(n)) return "—";
    return n.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarNumero(n) {
    const v = Number(n);
    if (!isFinite(v)) return "—";
    return v.toLocaleString("pt-BR");
}

async function carregarProfessores() {
    const res = await fetchAuth(`${API}/users/professor`);
    if (res.status === 204) return [];
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return await res.json();
}

async function carregarResumoGeral() {
    const res = await fetchAuth(`${API}/curso/resumo-geral`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return await res.json();
}

async function carregarHorasCurso(idCurso) {
    try {
        const res = await fetchAuth(`${API}/curso/curso-horas-totais/${idCurso}`);
        if (!res.ok) return 0;
        const txt = await res.text();
        const n = Number(txt);
        return isFinite(n) ? n : 0;
    } catch {
        return 0;
    }
}

function indexarResumoPorCurso(resumo) {
    const mapa = new Map();
    (resumo ?? []).forEach(r => mapa.set(Number(r.idCurso), r));
    return mapa;
}

async function buildHorasCache(idsUnicos) {
    const cache = new Map();
    const arr = await Promise.all(idsUnicos.map(id => carregarHorasCurso(id).then(h => [id, h])));
    arr.forEach(([id, h]) => cache.set(Number(id), Number(h) || 0));
    return cache;
}

function agregarProfessor(prof, mapaResumo, mapaHoras) {
    const cursos = prof.cursosLecionados ?? [];
    let totalAlunos = 0;
    let receitaTotal = 0;
    let cargaHorariaTotal = 0;
    let somaPreco = 0;
    let qtdComPreco = 0;

    cursos.forEach(c => {
        const id = Number(c.id_curso);
        const r = mapaResumo.get(id);
        if (r) {
            totalAlunos  += Number(r.totalCompras ?? 0);
            receitaTotal += Number(r.receitaEstimada ?? 0);
        }
        cargaHorariaTotal += mapaHoras.get(id) ?? 0;
        if (c.preco != null && isFinite(Number(c.preco))) {
            somaPreco += Number(c.preco);
            qtdComPreco++;
        }
    });

    return {
        cpf: prof.cpf,
        nome: prof.nome,
        email: prof.email,
        qtdCursos: cursos.length,
        qtdCertificacoes: (prof.certificados ?? []).length,
        totalAlunos,
        receitaTotal,
        cargaHorariaTotal,
        precoMedio: qtdComPreco ? (somaPreco / qtdComPreco) : 0,
        nomesCursos: cursos.map(c => c.nome_curso).filter(Boolean)
    };
}

function rankPor(lista, chave) {
    const ord = [...lista].sort((a, b) => Number(b[chave] ?? 0) - Number(a[chave] ?? 0));
    return ord.map((p, i) => ({ cpf: p.cpf, posicao: i + 1, valor: p[chave] }));
}

function aplicarFiltros() {
    const busca = document.getElementById("filtroBusca").value.trim().toLowerCase();
    const minCursos = Number(document.getElementById("filtroMinCursos").value || 0);
    const minAlunos = Number(document.getElementById("filtroMinAlunos").value || 0);

    professoresFiltrados = professoresAgg.filter(p => {
        if (busca) {
            const nome = (p.nome ?? "").toLowerCase();
            const cpf  = (p.cpf  ?? "").toLowerCase();
            if (!nome.includes(busca) && !cpf.includes(busca)) return false;
        }
        if (minCursos > 0 && p.qtdCursos < minCursos) return false;
        if (minAlunos > 0 && p.totalAlunos < minAlunos) return false;
        return true;
    });

    renderTudo();
}

function ehUsuarioLogado(p) {
    return currentUser?.cpf && p.cpf === currentUser.cpf;
}

function ordemDesc(lista, chave) {
    return [...lista].sort((a, b) => Number(b[chave] ?? 0) - Number(a[chave] ?? 0));
}

function topN(lista, chave, n = 10) {
    return ordemDesc(lista, chave).slice(0, n);
}

function buildBarChart(canvasId, dados, chave, label, formatter) {
    const cores = dados.map(p => ehUsuarioLogado(p) ? COR_DESTAQUE : COR_PADRAO);
    const labels = dados.map(p => p.nome ?? "(sem nome)");
    const valores = dados.map(p => Number(p[chave] ?? 0));

    return {
        type: "bar",
        data: {
            labels,
            datasets: [{
                label,
                data: valores,
                backgroundColor: cores,
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
                        label: (item) => formatter ? formatter(item.parsed.x) : item.parsed.x
                    }
                }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: { color: "#94a3b8", callback: (v) => formatter ? formatter(v) : v },
                    grid: { color: "rgba(255,255,255,0.05)" }
                },
                y: {
                    ticks: { color: "#cbd5e1" },
                    grid: { color: "rgba(255,255,255,0.05)" }
                }
            }
        }
    };
}

function renderRanking() {
    const chave = document.getElementById("filtroOrdenarPor").value;
    const dados = topN(professoresFiltrados, chave, 10);

    const formatadores = {
        totalAlunos:       (v) => formatarNumero(v) + " alunos",
        receitaTotal:      formatarBRL,
        cargaHorariaTotal: (v) => formatarNumero(v) + "h",
        qtdCursos:         (v) => formatarNumero(v) + " cursos",
        qtdCertificacoes:  (v) => formatarNumero(v) + " cert.",
        precoMedio:        formatarBRL
    };
    const labels = {
        totalAlunos:       "Alunos",
        receitaTotal:      "Receita (R$)",
        cargaHorariaTotal: "Carga horária (h)",
        qtdCursos:         "Cursos",
        qtdCertificacoes:  "Certificações",
        precoMedio:        "Preço médio (R$)"
    };

    if (chartRanking) chartRanking.destroy();
    const ctx = document.getElementById("chartRanking");
    if (!ctx) return;
    chartRanking = new Chart(ctx, buildBarChart(
        "chartRanking", dados, chave, labels[chave], formatadores[chave]
    ));
}

function renderScatter() {
    const dados = professoresFiltrados;

    if (chartScatter) chartScatter.destroy();
    const ctx = document.getElementById("chartScatter");
    if (!ctx) return;

    const pontos = dados.map(p => ({
        x: Number(p.cargaHorariaTotal ?? 0),
        y: Number(p.totalAlunos ?? 0),
        nome: p.nome ?? "—",
        eu: ehUsuarioLogado(p)
    }));

    chartScatter = new Chart(ctx, {
        type: "scatter",
        data: {
            datasets: [{
                label: "Professores",
                data: pontos,
                pointRadius: pontos.map(p => p.eu ? 9 : 6),
                pointHoverRadius: pontos.map(p => p.eu ? 12 : 8),
                backgroundColor: pontos.map(p => p.eu ? COR_DESTAQUE : "rgba(56, 189, 248, 0.7)"),
                borderColor: pontos.map(p => p.eu ? "#16a34a" : "#0284c7"),
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: (item) => {
                            const p = item.raw;
                            return `${p.nome}: ${formatarNumero(p.x)}h, ${formatarNumero(p.y)} alunos`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    title: { display: true, text: "Carga horária (h)", color: "#94a3b8" },
                    ticks: { color: "#94a3b8" },
                    grid: { color: "rgba(255,255,255,0.05)" }
                },
                y: {
                    title: { display: true, text: "Total de alunos", color: "#94a3b8" },
                    beginAtZero: true,
                    ticks: { color: "#94a3b8", precision: 0 },
                    grid: { color: "rgba(255,255,255,0.05)" }
                }
            }
        }
    });
}

function renderCertificacoes() {
    const dados = topN(professoresFiltrados, "qtdCertificacoes", 10);
    if (chartCertif) chartCertif.destroy();
    const ctx = document.getElementById("chartCertif");
    if (!ctx) return;
    chartCertif = new Chart(ctx, buildBarChart(
        "chartCertif", dados, "qtdCertificacoes", "Certificações",
        (v) => formatarNumero(v)
    ));
}

function renderReceita() {
    const dados = topN(professoresFiltrados, "receitaTotal", 10);
    if (chartReceita) chartReceita.destroy();
    const ctx = document.getElementById("chartReceita");
    if (!ctx) return;
    chartReceita = new Chart(ctx, buildBarChart(
        "chartReceita", dados, "receitaTotal", "Receita (R$)", formatarBRL
    ));
}

function ordenarLista(lista) {
    const dir = sortDir === "asc" ? 1 : -1;
    return [...lista].sort((a, b) => {
        const va = a[sortKey];
        const vb = b[sortKey];
        if (typeof va === "string" || typeof vb === "string") {
            return String(va ?? "").localeCompare(String(vb ?? ""), "pt-BR") * dir;
        }
        return (Number(va ?? 0) - Number(vb ?? 0)) * dir;
    });
}

function renderTabela() {
    const wrapper = document.getElementById("tabelaWrapper");
    const contagem = document.getElementById("contagemLista");
    if (!wrapper) return;

    contagem.textContent = `${professoresFiltrados.length} ${professoresFiltrados.length === 1 ? "professor" : "professores"}`;

    if (professoresFiltrados.length === 0) {
        wrapper.innerHTML = `<p class="placeholder">Nenhum professor bateu com os filtros.</p>`;
        return;
    }

    const ordenados = ordenarLista(professoresFiltrados);

    const cols = [
        ["nome",              "Professor",       false],
        ["qtdCursos",         "Cursos",          true],
        ["totalAlunos",       "Alunos",          true],
        ["cargaHorariaTotal", "Carga (h)",       true],
        ["qtdCertificacoes",  "Certificações",   true],
        ["precoMedio",        "Preço médio",     true],
        ["receitaTotal",      "Receita",         true]
    ];

    const headerCells = cols.map(([k, label, num]) => {
        const cls = sortKey === k ? (sortDir === "asc" ? "sort-asc" : "sort-desc") : "";
        const numCls = num ? "col-num" : "";
        return `<th data-key="${k}" class="${cls} ${numCls}">${label}</th>`;
    }).join("");

    const linhas = ordenados.map(p => {
        const eu = ehUsuarioLogado(p);
        return `
            <tr class="${eu ? "eu" : ""}">
                <td>
                    <div>
                        ${p.nome ?? "—"}
                        ${eu ? `<span class="badge-eu">você</span>` : ""}
                    </div>
                    <div style="font-size:0.78em; color:#64748b;">${p.cpf ?? ""}</div>
                </td>
                <td class="col-num">${formatarNumero(p.qtdCursos)}</td>
                <td class="col-num">${formatarNumero(p.totalAlunos)}</td>
                <td class="col-num">${formatarNumero(p.cargaHorariaTotal)}</td>
                <td class="col-num">${formatarNumero(p.qtdCertificacoes)}</td>
                <td class="col-num">${formatarBRL(p.precoMedio)}</td>
                <td class="col-num">${formatarBRL(p.receitaTotal)}</td>
            </tr>
        `;
    }).join("");

    wrapper.innerHTML = `
        <table class="tabela-prof">
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
                sortDir = "desc";
            }
            renderTabela();
        });
    });
}

function atualizarKpis() {
    const total = professoresAgg.length;
    document.getElementById("kpiTotal").textContent = total;

    const eu = professoresAgg.find(ehUsuarioLogado);
    const slots = [
        ["totalAlunos",       "kpiRankAlunos",  "kpiRankAlunosSub",  (v) => `${formatarNumero(v)} alunos no total`],
        ["receitaTotal",      "kpiRankReceita", "kpiRankReceitaSub", (v) => `${formatarBRL(v)} estimados`],
        ["cargaHorariaTotal", "kpiRankCargaH",  "kpiRankCargaHSub",  (v) => `${formatarNumero(v)} horas no total`]
    ];

    if (!eu) {
        slots.forEach(([, idValor, idSub]) => {
            document.getElementById(idValor).textContent = "—";
            document.getElementById(idSub).textContent = "Você não consta como professor.";
        });
        return;
    }

    slots.forEach(([chave, idValor, idSub, fmtSub]) => {
        const ranking = rankPor(professoresAgg, chave);
        const linha = ranking.find(r => r.cpf === eu.cpf);
        if (linha) {
            document.getElementById(idValor).textContent = `${linha.posicao}º / ${total}`;
            document.getElementById(idSub).textContent = fmtSub(linha.valor);
        }
    });
}

function renderTudo() {
    renderRanking();
    renderScatter();
    renderCertificacoes();
    renderReceita();
    renderTabela();
}

function bindFiltros() {
    ["filtroBusca", "filtroMinCursos", "filtroMinAlunos"].forEach(id => {
        document.getElementById(id).addEventListener("input", aplicarFiltros);
    });
    document.getElementById("filtroOrdenarPor").addEventListener("change", renderRanking);
    document.getElementById("btnResetar").addEventListener("click", () => {
        document.getElementById("filtroBusca").value = "";
        document.getElementById("filtroMinCursos").value = "";
        document.getElementById("filtroMinAlunos").value = "";
        document.getElementById("filtroOrdenarPor").value = "totalAlunos";
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
        const [professores, resumo] = await Promise.all([
            carregarProfessores(),
            carregarResumoGeral()
        ]);

        const mapaResumo = indexarResumoPorCurso(resumo);

        const idsUnicos = [...new Set(
            (professores ?? [])
                .flatMap(p => (p.cursosLecionados ?? []).map(c => Number(c.id_curso)))
                .filter(Number.isFinite)
        )];
        const mapaHoras = await buildHorasCache(idsUnicos);

        professoresAgg = (professores ?? []).map(p => agregarProfessor(p, mapaResumo, mapaHoras));
        professoresFiltrados = [...professoresAgg];

        atualizarKpis();
        bindFiltros();
        renderTudo();

    } catch (err) {
        console.error(err);
        document.getElementById("tabelaWrapper").innerHTML =
            `<p class="erro">Erro ao carregar dados: ${err.message}</p>`;
    }
}

document.addEventListener("DOMContentLoaded", inicializar);
