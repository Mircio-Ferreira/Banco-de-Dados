const API = "http://localhost:8080/api/v1";

let chartInstance = null;
let cursosProfessor = [];

function fetchAuth(url) {
    return fetch(url, {
        headers: {
            "Accept": "application/json",
            "X-User-CPF": currentUser?.cpf ?? ""
        }
    });
}

function formatarBRL(valor) {
    const numero = Number(valor);
    if (!isFinite(numero)) return "—";
    return numero.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarData(iso) {
    if (!iso) return "—";
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso;
    return d.toLocaleString("pt-BR", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit"
    });
}

function variacaoPct(antigo, novo) {
    const a = Number(antigo);
    const n = Number(novo);
    if (!isFinite(a) || !isFinite(n) || a === 0) return null;
    return ((n - a) / a) * 100;
}

function badgeVariacao(pct) {
    if (pct == null) return `<span class="badge-var var-flat">—</span>`;
    if (pct > 0)     return `<span class="badge-var var-up">▲ ${pct.toFixed(1)}%</span>`;
    if (pct < 0)     return `<span class="badge-var var-down">▼ ${Math.abs(pct).toFixed(1)}%</span>`;
    return `<span class="badge-var var-flat">0%</span>`;
}

async function carregarCursosProfessor() {
    const res = await fetchAuth(`${API}/users/professor/${currentUser.cpf}`);
    if (!res.ok) throw new Error(`Falha ao buscar professor (HTTP ${res.status})`);
    const professor = await res.json();
    return professor.cursosLecionados ?? [];
}

async function carregarCursosBaratos() {
    try {
        const res = await fetchAuth(`${API}/curso/cursos-baratos`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return await res.json();
    } catch (err) {
        console.error("Erro ao buscar cursos baratos:", err);
        return [];
    }
}

async function carregarHistorico(idCurso) {
    const res = await fetchAuth(`${API}/curso/log-preco/${idCurso}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return await res.json();
}

async function carregarResumo(idCurso) {
    const res = await fetchAuth(`${API}/curso/resumo-geral/${idCurso}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const lista = await res.json();
    return Array.isArray(lista) ? lista[0] : lista;
}

function renderListaCursos(cursos) {
    const container = document.getElementById("listaCursos");
    container.innerHTML = "";

    if (cursos.length === 0) {
        container.innerHTML = `<p class="placeholder" style="padding:10px;">Você ainda não leciona nenhum curso.</p>`;
        return;
    }

    cursos.forEach(curso => {
        const div = document.createElement("div");
        div.className = "item-curso";
        div.dataset.idCurso = curso.id_curso;

        div.innerHTML = `
            <h4>${curso.nome_curso ?? "(sem nome)"}</h4>
            <div class="meta">
                <span class="preco">${formatarBRL(curso.preco)}</span>
                <span class="ultima-var" id="ultimaVar-${curso.id_curso}">
                    <span class="badge-var var-flat">—</span>
                </span>
            </div>
        `;

        div.addEventListener("click", () => selecionarCurso(curso));
        container.appendChild(div);
    });
}

function marcarItemAtivo(idCurso) {
    document.querySelectorAll(".item-curso").forEach(el => {
        el.classList.toggle("ativa", el.dataset.idCurso == idCurso);
    });
}

async function selecionarCurso(curso) {
    marcarItemAtivo(curso.id_curso);

    const painel = document.getElementById("painelCurso");
    painel.innerHTML = `<p class="placeholder">Carregando dados de "${curso.nome_curso}"...</p>`;

    let logs = [];
    let resumo = null;
    try {
        [logs, resumo] = await Promise.all([
            carregarHistorico(curso.id_curso),
            carregarResumo(curso.id_curso).catch(() => null)
        ]);
    } catch (err) {
        console.error(err);
        painel.innerHTML = `<p style="color:#ef4444;">Erro ao carregar dados do curso: ${err.message}</p>`;
        return;
    }

    atualizarUltimaVariacaoNaLista(curso.id_curso, logs);
    renderPainel(curso, logs, resumo);
}

function atualizarUltimaVariacaoNaLista(idCurso, logs) {
    const slot = document.getElementById(`ultimaVar-${idCurso}`);
    if (!slot) return;

    if (!logs || logs.length === 0) {
        slot.innerHTML = `<span class="badge-var var-flat">sem histórico</span>`;
        return;
    }

    const recente = [...logs].sort(
        (a, b) => new Date(b.dataAlteracao) - new Date(a.dataAlteracao)
    )[0];
    const pct = variacaoPct(recente.precoAntigo, recente.precoNovo);
    slot.innerHTML = badgeVariacao(pct);
}

function renderPainel(curso, logs, resumo) {
    const painel = document.getElementById("painelCurso");
    painel.innerHTML = `
        <div class="titulo-linha">
            <h3>${curso.nome_curso}</h3>
            <span class="preco-atual">${formatarBRL(curso.preco)}</span>
        </div>

        <div class="chart-wrapper">
            <canvas id="chartPreco"></canvas>
        </div>

        <h4 style="margin-top: 20px;">Histórico de alterações</h4>
        <div id="tabelaWrapper"></div>

        <h4 style="margin-top: 20px;">Resumo geral</h4>
        <div id="resumoWrapper"></div>
    `;

    renderChart(logs, curso.preco);
    renderTabela(logs);
    renderResumo(resumo);
}

function renderChart(logs, precoAtual) {
    if (chartInstance) {
        chartInstance.destroy();
        chartInstance = null;
    }

    const ctx = document.getElementById("chartPreco");
    if (!ctx) return;

    const ordenados = [...(logs ?? [])].sort(
        (a, b) => new Date(a.dataAlteracao) - new Date(b.dataAlteracao)
    );

    const pontos = ordenados.map(l => ({
        x: new Date(l.dataAlteracao),
        y: Number(l.precoAntigo)
    }));
    pontos.push({ x: new Date(), y: Number(precoAtual) });

    const labels = pontos.map(p => formatarData(p.x.toISOString()));
    const valores = pontos.map(p => p.y);

    chartInstance = new Chart(ctx, {
        type: "line",
        data: {
            labels,
            datasets: [{
                label: "Preço (R$)",
                data: valores,
                borderColor: "#38bdf8",
                backgroundColor: "rgba(56, 189, 248, 0.15)",
                tension: 0.25,
                fill: true,
                pointRadius: 4,
                pointBackgroundColor: "#38bdf8"
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { labels: { color: "#cbd5e1" } },
                tooltip: {
                    callbacks: {
                        label: (item) => {
                            const v = item.parsed.y;
                            const idx = item.dataIndex;
                            const ant = idx > 0 ? valores[idx - 1] : null;
                            const pct = ant != null ? variacaoPct(ant, v) : null;
                            const sufixo = pct != null
                                ? ` (${pct >= 0 ? "+" : ""}${pct.toFixed(2)}%)`
                                : "";
                            return `${formatarBRL(v)}${sufixo}`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    ticks: { color: "#94a3b8", maxRotation: 0, autoSkip: true },
                    grid: { color: "rgba(255,255,255,0.05)" }
                },
                y: {
                    ticks: {
                        color: "#94a3b8",
                        callback: (v) => formatarBRL(v)
                    },
                    grid: { color: "rgba(255,255,255,0.05)" }
                }
            }
        }
    });
}

function renderTabela(logs) {
    const wrapper = document.getElementById("tabelaWrapper");
    if (!wrapper) return;

    if (!logs || logs.length === 0) {
        wrapper.innerHTML = `<p class="placeholder">Sem alterações registradas para este curso.</p>`;
        return;
    }

    const ordenados = [...logs].sort(
        (a, b) => new Date(b.dataAlteracao) - new Date(a.dataAlteracao)
    );

    const linhas = ordenados.map(l => {
        const pct = variacaoPct(l.precoAntigo, l.precoNovo);
        return `
            <tr>
                <td>${formatarData(l.dataAlteracao)}</td>
                <td>${formatarBRL(l.precoAntigo)}</td>
                <td>${formatarBRL(l.precoNovo)}</td>
                <td>${badgeVariacao(pct)}</td>
                <td style="opacity: 0.75;">${l.usuarioBanco ?? "—"}</td>
            </tr>
        `;
    }).join("");

    wrapper.innerHTML = `
        <table class="tabela-precos">
            <thead>
                <tr>
                    <th>Data</th>
                    <th>Preço antigo</th>
                    <th>Preço novo</th>
                    <th>Variação</th>
                    <th>Autor</th>
                </tr>
            </thead>
            <tbody>${linhas}</tbody>
        </table>
    `;
}

function renderResumo(r) {
    const wrapper = document.getElementById("resumoWrapper");
    if (!wrapper) return;

    if (!r) {
        wrapper.innerHTML = `<p class="placeholder">Sem dados de resumo geral disponíveis.</p>`;
        return;
    }

    const classificacao = r.classificacaoPreco ?? "";
    const badgeClass =
        classificacao === "ACIMA_DA_MEDIA"  ? "badge-acima"  :
        classificacao === "ABAIXO_DA_MEDIA" ? "badge-abaixo" :
        classificacao === "NA_MEDIA"        ? "badge-na"     : "";
    const badgeLabel =
        classificacao === "ACIMA_DA_MEDIA"  ? "📈 Acima da média"  :
        classificacao === "ABAIXO_DA_MEDIA" ? "📉 Abaixo da média" :
        classificacao === "NA_MEDIA"        ? "➖ Na média"        : "—";

    wrapper.innerHTML = `
        <div class="resumo-card">
            <div class="linha"><span>Preço atual</span><span class="preco">${formatarBRL(r.preco)}</span></div>
            <div class="linha"><span>Total de compras</span><span>${Number(r.totalCompras ?? 0)}</span></div>
            <div class="linha"><span>Receita estimada</span><span class="receita">${formatarBRL(r.receitaEstimada)}</span></div>
            <div class="linha" style="opacity: 0.75;"><span>Média geral do mercado</span><span>${formatarBRL(r.mediaPrecoGeral)}</span></div>
            <span class="badge-classificacao ${badgeClass}">${badgeLabel}</span>
        </div>
    `;
}

function renderCursosBaratos(lista, idsDoProfessor) {
    const container = document.getElementById("cursosBaratos");
    if (!container) return;

    if (!Array.isArray(lista) || lista.length === 0) {
        container.innerHTML = `<p class="placeholder">Nenhum curso abaixo da média no momento.</p>`;
        return;
    }

    container.innerHTML = "";
    const setIds = new Set(idsDoProfessor);

    lista.forEach(c => {
        const proprio = setIds.has(c.id_curso);
        const div = document.createElement("div");
        div.className = "barato-item" + (proprio ? " proprio" : "");
        div.innerHTML = `
            ${proprio ? `<span class="badge-proprio">seu curso</span>` : ""}
            <span class="nome">${c.nome_curso ?? "(sem nome)"}</span>
            <span class="preco">${formatarBRL(c.preco)}</span>
        `;
        container.appendChild(div);
    });
}

async function inicializar() {
    if (!currentUser?.cpf) {
        document.getElementById("listaCursos").innerHTML =
            `<p style="color:#ef4444; padding:10px;">Usuário não autenticado.</p>`;
        return;
    }

    try {
        const [cursos, baratos] = await Promise.all([
            carregarCursosProfessor(),
            carregarCursosBaratos()
        ]);

        cursosProfessor = cursos;
        renderListaCursos(cursos);
        renderCursosBaratos(baratos, cursos.map(c => c.id_curso));

        if (cursos.length > 0) {
            await selecionarCurso(cursos[0]);
        } else {
            document.getElementById("painelCurso").innerHTML =
                `<p class="placeholder">Crie um curso para começar a acompanhar a evolução de preços.</p>`;
        }
    } catch (err) {
        console.error(err);
        document.getElementById("listaCursos").innerHTML =
            `<p style="color:#ef4444; padding:10px;">Erro ao carregar cursos: ${err.message}</p>`;
    }
}

document.addEventListener("DOMContentLoaded", inicializar);
