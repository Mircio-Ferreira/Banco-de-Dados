const API = "http://localhost:8080/api/v1";

let cursosBase = [];

const filtros = {
    busca: "",
    classificacao: "",
    ordenacao: "nome-asc"
};

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

function inicialDoTitulo(nome) {
    return (nome ?? "C").trim().charAt(0).toUpperCase();
}

async function carregarPerfilProfessor() {
    const res = await fetchAuth(`${API}/users/professor/${currentUser.cpf}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return await res.json();
}

async function carregarResumoCurso(idCurso) {
    try {
        const res = await fetchAuth(`${API}/curso/resumo-geral/${idCurso}`);
        if (!res.ok) return null;
        const lista = await res.json();
        return Array.isArray(lista) ? lista[0] : lista;
    } catch {
        return null;
    }
}

function atualizarKpis(cursos) {
    const totalCursos = cursos.length;
    const totalAlunos = cursos.reduce((acc, c) => acc + Number(c.resumo?.totalCompras ?? 0), 0);
    const receita     = cursos.reduce((acc, c) => acc + Number(c.resumo?.receitaEstimada ?? 0), 0);
    const precos      = cursos.map(c => Number(c.preco)).filter(isFinite);
    const precoMedio  = precos.length ? (precos.reduce((a, b) => a + b, 0) / precos.length) : 0;

    document.getElementById("kpiCursos").textContent      = totalCursos;
    document.getElementById("kpiAlunos").textContent      = formatarNumero(totalAlunos);
    document.getElementById("kpiReceita").textContent     = formatarBRL(receita);
    document.getElementById("kpiPrecoMedio").textContent  = precos.length ? formatarBRL(precoMedio) : "—";
}

function aplicaFiltros(curso) {
    if (filtros.busca) {
        const nome = (curso.nome_curso ?? "").toLowerCase();
        if (!nome.includes(filtros.busca)) return false;
    }
    if (filtros.classificacao) {
        if (curso.resumo?.classificacaoPreco !== filtros.classificacao) return false;
    }
    return true;
}

function ordenar(lista) {
    const arr = [...lista];
    switch (filtros.ordenacao) {
        case "nome-asc":     return arr.sort((a, b) => (a.nome_curso ?? "").localeCompare(b.nome_curso ?? "", "pt-BR"));
        case "nome-desc":    return arr.sort((a, b) => (b.nome_curso ?? "").localeCompare(a.nome_curso ?? "", "pt-BR"));
        case "alunos-desc":  return arr.sort((a, b) => Number(b.resumo?.totalCompras ?? 0)    - Number(a.resumo?.totalCompras ?? 0));
        case "receita-desc": return arr.sort((a, b) => Number(b.resumo?.receitaEstimada ?? 0) - Number(a.resumo?.receitaEstimada ?? 0));
        case "preco-desc":   return arr.sort((a, b) => Number(b.preco ?? 0) - Number(a.preco ?? 0));
        case "preco-asc":    return arr.sort((a, b) => Number(a.preco ?? 0) - Number(b.preco ?? 0));
        default: return arr;
    }
}

function montarBadgeClassificacao(classificacao) {
    const cls = {
        "ACIMA_DA_MEDIA":  ["badge-acima",  "📈 Acima da média"],
        "ABAIXO_DA_MEDIA": ["badge-abaixo", "📉 Abaixo da média"],
        "NA_MEDIA":        ["badge-na",     "➖ Na média"]
    };
    const [klass, label] = cls[classificacao] ?? ["", "—"];
    return `<span class="badge-classificacao ${klass}">${label}</span>`;
}

function montarCard(curso) {
    const card = document.createElement("div");
    card.className = "card-prof";
    card.dataset.cursoId = curso.id_curso;

    const inicial = inicialDoTitulo(curso.nome_curso);
    const preco = Number(curso.preco ?? 0);
    const precoLabel = preco > 0 ? formatarBRL(preco) : "Grátis";

    const r = curso.resumo;
    const resumoHtml = r
        ? `
            <div class="card-resumo">
                <div class="linha"><span>Compras</span><span>${formatarNumero(r.totalCompras ?? 0)}</span></div>
                <div class="linha"><span>Receita</span><span class="receita">${formatarBRL(r.receitaEstimada)}</span></div>
                <div class="linha" style="opacity:0.75;"><span>Média geral</span><span>${formatarBRL(r.mediaPrecoGeral)}</span></div>
                ${montarBadgeClassificacao(r.classificacaoPreco)}
            </div>`
        : `<div class="card-resumo"><span class="placeholder">Sem dados de resumo.</span></div>`;

    card.innerHTML = `
        <div class="thumb-area">
            <span>${inicial}</span>
            <span class="preco-tag">${precoLabel}</span>
        </div>
        <div class="corpo">
            <h3>${curso.nome_curso ?? "(sem nome)"}</h3>
            ${resumoHtml}
        </div>
    `;

    card.addEventListener("click", () => {
        window.location.href = `../editarCurso/editarCurso.html?id=${curso.id_curso}`;
    });

    return card;
}

function renderCarrossel() {
    const container = document.getElementById("carrosselCursosProfessor");
    const contagem  = document.getElementById("contagemCursos");
    if (!container) return;

    const filtrados = ordenar(cursosBase.filter(aplicaFiltros));

    contagem.textContent = `${filtrados.length} ${filtrados.length === 1 ? "curso" : "cursos"}`;

    container.innerHTML = "";
    if (filtrados.length === 0) {
        container.innerHTML = cursosBase.length === 0
            ? `<div class="secao-vazia">Você ainda não leciona nenhum curso. Crie o primeiro!</div>`
            : `<div class="secao-vazia">Nenhum curso seu corresponde aos filtros aplicados.</div>`;
        return;
    }

    filtrados.forEach(c => container.appendChild(montarCard(c)));
}

function bindFiltros() {
    const busca = document.getElementById("busca");
    const buscaTitulo = document.getElementById("buscaTitulo");

    function aplicarBusca(valor) {
        filtros.busca = (valor ?? "").trim().toLowerCase();
        if (busca && busca.value !== valor) busca.value = valor;
        if (buscaTitulo && buscaTitulo.value !== valor) buscaTitulo.value = valor;
        renderCarrossel();
    }

    if (busca) {
        busca.addEventListener("input", (e) => aplicarBusca(e.target.value));
    }
    if (buscaTitulo) {
        buscaTitulo.addEventListener("input", (e) => aplicarBusca(e.target.value));
    }

    const ordem = document.getElementById("ordenarCursos");
    if (ordem) {
        ordem.addEventListener("change", (e) => {
            filtros.ordenacao = e.target.value;
            renderCarrossel();
        });
    }

    const classif = document.getElementById("filtroClassificacao");
    if (classif) {
        classif.addEventListener("change", (e) => {
            filtros.classificacao = e.target.value;
            renderCarrossel();
        });
    }

    const reset = document.getElementById("btnResetar");
    if (reset) {
        reset.addEventListener("click", () => {
            filtros.busca = "";
            filtros.classificacao = "";
            filtros.ordenacao = "nome-asc";

            if (busca)        busca.value = "";
            if (buscaTitulo)  buscaTitulo.value = "";
            if (ordem)   ordem.value = "nome-asc";
            if (classif) classif.value = "";

            renderCarrossel();
        });
    }
}

async function inicializar() {
    if (!currentUser?.cpf) return;

    const container = document.getElementById("carrosselCursosProfessor");
    container.innerHTML = `<div class="secao-vazia">Carregando seus cursos...</div>`;

    try {
        const professor = await carregarPerfilProfessor();
        const cursos = professor.cursosLecionados ?? [];

        const resumos = await Promise.all(cursos.map(c => carregarResumoCurso(c.id_curso)));
        cursosBase = cursos.map((c, i) => ({ ...c, resumo: resumos[i] }));

        atualizarKpis(cursosBase);
        bindFiltros();
        renderCarrossel();
    } catch (err) {
        console.error("Erro ao buscar cursos do professor:", err);
        container.innerHTML =
            `<div class="secao-vazia" style="color:#ef4444;">Erro ao carregar cursos: ${err.message}</div>`;
    }
}

document.addEventListener("DOMContentLoaded", inicializar);
