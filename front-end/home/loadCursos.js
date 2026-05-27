const API = "http://localhost:8080/api/v1";

let cursoSelecionadoId = null;
let cursoSelecionadoPreco = null;
let cpfAlunoLogado = null;
let todosCursosDoSistema = [];
let idsAdquiridos = new Set();

const filtros = {
    busca: "",
    categoria: "",
    precoMax: null,
    ordenacao: "nome-asc"
};

function fetchAuth(url, options = {}) {
    return fetch(url, {
        ...options,
        headers: {
            "Accept": "application/json",
            "X-User-CPF": cpfAlunoLogado ?? "",
            ...(options.headers ?? {})
        }
    });
}

function formatarBRL(valor) {
    const n = Number(valor);
    if (!isFinite(n)) return "—";
    return n.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function nomesCategorias(curso) {
    return (curso.categorias ?? []).map(c => c.nome).filter(Boolean);
}

function inicialDoTitulo(nome) {
    return (nome ?? "C").trim().charAt(0).toUpperCase();
}

async function inicializarDashboard() {
    const userStorage = localStorage.getItem("user");
    if (!userStorage) {
        console.error("Nenhum usuário detectado no localStorage.");
        window.location.href = "../login/login.html";
        return;
    }

    const usuario = JSON.parse(userStorage);
    cpfAlunoLogado = usuario.cpf;
    if (!cpfAlunoLogado) {
        console.error("CPF não encontrado nos dados do usuário local.");
        return;
    }

    try {
        const [resTodos, resCompras] = await Promise.all([
            fetchAuth(`${API}/curso`),
            fetchAuth(`${API}/compra/aluno/${cpfAlunoLogado}`)
        ]);

        if (!resTodos.ok) throw new Error("Não foi possível carregar o catálogo geral de cursos.");
        todosCursosDoSistema = await resTodos.json();

        const minhasCompras = resCompras.ok ? await resCompras.json() : [];
        idsAdquiridos = new Set(minhasCompras.map(c => Number(c.id_curso)));

        atualizarKpis();
        renderChipsCategoria();
        bindFiltros();
        renderTudo();
    } catch (err) {
        console.error("Erro ao carregar dados do dashboard:", err);
        document.getElementById("carrosselCatalogo").innerHTML =
            `<p class="secao-vazia" style="color:#ef4444;">Erro ao carregar catálogo: ${err.message}</p>`;
    }
}

function atualizarKpis() {
    const adquiridos  = idsAdquiridos.size;
    const disponiveis = todosCursosDoSistema.filter(c => !idsAdquiridos.has(Number(c.id_curso))).length;
    const cats = new Set();
    let somaPreco = 0, qtd = 0;

    todosCursosDoSistema.forEach(c => {
        nomesCategorias(c).forEach(n => cats.add(n));
        if (c.preco != null && isFinite(Number(c.preco))) {
            somaPreco += Number(c.preco);
            qtd++;
        }
    });

    document.getElementById("kpiAdquiridos").textContent = adquiridos;
    document.getElementById("kpiDisponiveis").textContent = disponiveis;
    document.getElementById("kpiCategorias").textContent = cats.size;
    document.getElementById("kpiPrecoMedio").textContent = qtd ? formatarBRL(somaPreco / qtd) : "—";
}

function renderChipsCategoria() {
    const container = document.getElementById("chipsCategoria");
    if (!container) return;

    const cats = new Set();
    todosCursosDoSistema.forEach(c => nomesCategorias(c).forEach(n => cats.add(n)));
    const ordenadas = [...cats].sort((a, b) => a.localeCompare(b, "pt-BR"));

    container.innerHTML = "";

    const chipTodas = document.createElement("div");
    chipTodas.className = "chip" + (filtros.categoria === "" ? " ativo" : "");
    chipTodas.textContent = "Todas";
    chipTodas.dataset.categoria = "";
    chipTodas.addEventListener("click", () => selecionarCategoria(""));
    container.appendChild(chipTodas);

    ordenadas.forEach(nome => {
        const chip = document.createElement("div");
        chip.className = "chip" + (filtros.categoria === nome ? " ativo" : "");
        chip.textContent = nome;
        chip.dataset.categoria = nome;
        chip.addEventListener("click", () => selecionarCategoria(nome));
        container.appendChild(chip);
    });
}

function selecionarCategoria(nome) {
    filtros.categoria = filtros.categoria === nome ? "" : nome;
    renderChipsCategoria();
    renderCatalogo();
}

function bindFiltros() {
    const busca = document.getElementById("busca");
    const buscaTitulo = document.getElementById("buscaTitulo");

    function aplicarBusca(valor) {
        filtros.busca = (valor ?? "").trim().toLowerCase();
        if (busca && busca.value !== valor) busca.value = valor;
        if (buscaTitulo && buscaTitulo.value !== valor) buscaTitulo.value = valor;
        renderTudo();
    }

    if (busca) {
        busca.addEventListener("input", (e) => aplicarBusca(e.target.value));
    }
    if (buscaTitulo) {
        buscaTitulo.addEventListener("input", (e) => aplicarBusca(e.target.value));
    }

    const ordem = document.getElementById("ordenarCatalogo");
    if (ordem) {
        ordem.addEventListener("change", (e) => {
            filtros.ordenacao = e.target.value;
            renderCatalogo();
        });
    }

    const preco = document.getElementById("filtroPrecoMax");
    if (preco) {
        preco.addEventListener("input", (e) => {
            const v = Number(e.target.value);
            filtros.precoMax = isFinite(v) && v > 0 ? v : null;
            renderCatalogo();
        });
    }

    const reset = document.getElementById("btnResetar");
    if (reset) {
        reset.addEventListener("click", () => {
            filtros.busca = "";
            filtros.categoria = "";
            filtros.precoMax = null;
            filtros.ordenacao = "nome-asc";

            if (busca)        busca.value = "";
            if (buscaTitulo)  buscaTitulo.value = "";
            if (ordem) ordem.value = "nome-asc";
            if (preco) preco.value = "";

            renderChipsCategoria();
            renderTudo();
        });
    }
}

function aplicaFiltrosNoCurso(curso, ignoraAdquirido = false) {
    if (!ignoraAdquirido && idsAdquiridos.has(Number(curso.id_curso))) return false;

    if (filtros.busca) {
        const nome = (curso.nome_curso ?? "").toLowerCase();
        if (!nome.includes(filtros.busca)) return false;
    }
    if (filtros.categoria) {
        const cats = nomesCategorias(curso);
        if (!cats.includes(filtros.categoria)) return false;
    }
    if (filtros.precoMax != null) {
        const p = Number(curso.preco ?? 0);
        if (p > filtros.precoMax) return false;
    }
    return true;
}

function ordenarCatalogo(lista) {
    const arr = [...lista];
    switch (filtros.ordenacao) {
        case "nome-asc":  return arr.sort((a, b) => (a.nome_curso ?? "").localeCompare(b.nome_curso ?? "", "pt-BR"));
        case "nome-desc": return arr.sort((a, b) => (b.nome_curso ?? "").localeCompare(a.nome_curso ?? "", "pt-BR"));
        case "preco-asc": return arr.sort((a, b) => Number(a.preco ?? 0) - Number(b.preco ?? 0));
        case "preco-desc":return arr.sort((a, b) => Number(b.preco ?? 0) - Number(a.preco ?? 0));
        default: return arr;
    }
}

function montarCard(curso, { adquirido }) {
    const card = document.createElement("div");
    card.className = "card-melhor" + (adquirido ? " adquirido" : "");
    card.dataset.id = curso.id_curso;

    const cats = nomesCategorias(curso);
    const preco = Number(curso.preco ?? 0);
    const precoLabel = preco > 0 ? formatarBRL(preco) : "Grátis";
    const precoCls = preco > 0 ? "" : "gratis";
    const inicial = inicialDoTitulo(curso.nome_curso);

    card.innerHTML = `
        <div class="thumb-area">
            <span>${inicial}</span>
            <span class="preco-tag ${precoCls}">${precoLabel}</span>
        </div>
        <div class="corpo">
            <h3>${curso.nome_curso ?? "(sem nome)"}</h3>
            <div class="cats">
                ${cats.length
                    ? cats.slice(0, 3).map(n => `<span class="cat">${n}</span>`).join("")
                    : `<span class="cat" style="opacity:0.6;">sem categoria</span>`}
            </div>
            <div class="cta">${adquirido ? "▶ Continuar" : "🛒 Adquirir"}</div>
        </div>
    `;

    if (adquirido) {
        card.onclick = () => { window.location.href = `../curso/curso.html?id=${curso.id_curso}`; };
    } else {
        card.onclick = () => abrirModalCompra(curso.id_curso, curso.nome_curso, curso.preco);
    }

    return card;
}

function renderTudo() {
    renderMeusCursos();
    renderCatalogo();
}

function renderMeusCursos() {
    const container = document.getElementById("carrosselCursos");
    const contagem  = document.getElementById("contagemMeus");
    if (!container) return;

    const meus = todosCursosDoSistema
        .filter(c => idsAdquiridos.has(Number(c.id_curso)))
        .filter(c => aplicaFiltrosNoCurso(c, true));

    contagem.textContent = `${meus.length} ${meus.length === 1 ? "curso" : "cursos"}`;

    container.innerHTML = "";
    if (meus.length === 0) {
        container.innerHTML = idsAdquiridos.size === 0
            ? `<div class="secao-vazia">Você ainda não possui nenhum curso. Confira o catálogo abaixo!</div>`
            : `<div class="secao-vazia">Nenhum curso seu corresponde à busca.</div>`;
        return;
    }

    ordenarCatalogo(meus).forEach(c => container.appendChild(montarCard(c, { adquirido: true })));
}

function renderCatalogo() {
    const container = document.getElementById("carrosselCatalogo");
    const contagem  = document.getElementById("contagemCatalogo");
    if (!container) return;

    const disponiveis = todosCursosDoSistema.filter(c => aplicaFiltrosNoCurso(c, false));

    contagem.textContent = `${disponiveis.length} ${disponiveis.length === 1 ? "curso" : "cursos"}`;

    container.innerHTML = "";
    if (disponiveis.length === 0) {
        container.innerHTML = `<div class="secao-vazia">Nenhum curso encontrado com esses filtros.</div>`;
        return;
    }

    ordenarCatalogo(disponiveis).forEach(c => container.appendChild(montarCard(c, { adquirido: false })));
}

const QR_CODES_PIX = [
    { arquivo: "pix_amanda.png",  nome: "Amanda"  },
    { arquivo: "pix_eric.png",    nome: "Eric"    },
    { arquivo: "pix_gabriel.png", nome: "Gabriel" }
];

function sortearQrCodePix() {
    const idx = Math.floor(Math.random() * QR_CODES_PIX.length);
    return QR_CODES_PIX[idx];
}

function abrirModalCompra(idCurso, nomeCurso, preco) {
    cursoSelecionadoId = idCurso;
    cursoSelecionadoPreco = preco;
    document.getElementById("nomeCursoModal").innerText = nomeCurso;
    const slotPreco = document.getElementById("precoCursoModal");
    if (slotPreco) {
        slotPreco.innerText = (preco != null && Number(preco) > 0) ? formatarBRL(preco) : "Grátis";
    }

    const qr = sortearQrCodePix();
    const qrImg = document.getElementById("qrCodePix");
    const destinatario = document.getElementById("pixDestinatario");
    if (qrImg) qrImg.src = `../../resource/${qr.arquivo}`;
    if (destinatario) destinatario.innerHTML = `Pix de <strong style="color:#f1f5f9;">${qr.nome}</strong>`;

    document.getElementById("modalCompra").style.display = "flex";
}

function fecharModal() {
    document.getElementById("modalCompra").style.display = "none";
    cursoSelecionadoId = null;
    cursoSelecionadoPreco = null;
}

async function efetivarCompra() {
    if (!cursoSelecionadoId || !cpfAlunoLogado) return;

    const payload = {
        "id_curso": cursoSelecionadoId,
        "cpf_aluno": cpfAlunoLogado
    };

    try {
        const response = await fetchAuth(`${API}/compra`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert("Curso adquirido com sucesso!");
            fecharModal();
            inicializarDashboard();
        } else {
            const motivo = await response.text().catch(() => "");
            alert("Erro ao processar compra: " + (motivo || `HTTP ${response.status}`));
        }
    } catch (error) {
        console.error("Erro na rota POST de compra:", error);
        alert("Não foi possível conectar ao servidor de compras.");
    }
}

document.addEventListener("DOMContentLoaded", inicializarDashboard);
