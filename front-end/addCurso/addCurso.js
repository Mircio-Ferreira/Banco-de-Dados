const API = "http://localhost:8080/api/v1";

let modulos = [];

// ---------- Categorias ----------

function addCategoria() {
    const input = document.getElementById("categoriaInput");
    const list = document.getElementById("categoriaList");

    const valor = input.value.trim();
    if (valor === "") return;

    const item = document.createElement("div");

    const text = document.createElement("span");
    text.textContent = valor;

    const removeBtn = document.createElement("button");
    removeBtn.textContent = "X";
    removeBtn.className = "remove-btn";
    removeBtn.onclick = () => item.remove();

    item.appendChild(text);
    item.appendChild(removeBtn);
    list.appendChild(item);

    input.value = "";
}

function pegarCategorias() {
    const items = document.getElementById("categoriaList").children;
    const lista = [];
    for (const item of items) {
        lista.push(item.querySelector("span").textContent);
    }
    return lista;
}

// ---------- Módulos / Aulas (estado em memória) ----------

function addModulo() {
    modulos.push({
        titulo: "",
        descricao: "",
        carga_horaria: "",
        aulas: []
    });
    renderModulos();
}

function removerModulo(mIndex) {
    modulos.splice(mIndex, 1);
    renderModulos();
}

function addAula(mIndex) {
    modulos[mIndex].aulas.push({
        titulo: "",
        descricao: "",
        link_do_video: ""
    });
    renderModulos();
}

function removerAula(mIndex, aIndex) {
    modulos[mIndex].aulas.splice(aIndex, 1);
    renderModulos();
}

function renderModulos() {
    const container = document.getElementById("modulos");
    container.innerHTML = "";

    modulos.forEach((modulo, mIndex) => {
        const div = document.createElement("div");
        div.className = "box";

        div.innerHTML = `
            <div style="display:flex;justify-content:space-between;align-items:center;">
                <h4>Módulo ${mIndex + 1}</h4>
                <button class="remove-btn" type="button" onclick="removerModulo(${mIndex})">Remover módulo</button>
            </div>
            <input placeholder="Título do módulo" value="${modulo.titulo}"
                   oninput="modulos[${mIndex}].titulo=this.value">
            <input placeholder="Descrição" value="${modulo.descricao}"
                   oninput="modulos[${mIndex}].descricao=this.value">
            <input type="number" min="1" placeholder="Carga horária (horas)" value="${modulo.carga_horaria}"
                   oninput="modulos[${mIndex}].carga_horaria=this.value">

            <button type="button" onclick="addAula(${mIndex})">Adicionar aula</button>

            <div class="nested" id="aulas-${mIndex}"></div>
        `;

        container.appendChild(div);

        const aulasDiv = div.querySelector(`#aulas-${mIndex}`);

        modulo.aulas.forEach((aula, aIndex) => {
            const aulaDiv = document.createElement("div");
            aulaDiv.className = "box";

            aulaDiv.innerHTML = `
                <div style="display:flex;justify-content:space-between;align-items:center;">
                    <strong>Aula ${aIndex + 1}</strong>
                    <button class="remove-btn" type="button" onclick="removerAula(${mIndex}, ${aIndex})">Remover</button>
                </div>
                <input placeholder="Título da aula" value="${aula.titulo}"
                       oninput="modulos[${mIndex}].aulas[${aIndex}].titulo=this.value">
                <input placeholder="Descrição" value="${aula.descricao}"
                       oninput="modulos[${mIndex}].aulas[${aIndex}].descricao=this.value">
                <input placeholder="Link do vídeo" value="${aula.link_do_video}"
                       oninput="modulos[${mIndex}].aulas[${aIndex}].link_do_video=this.value">
            `;

            aulasDiv.appendChild(aulaDiv);
        });
    });
}

// ---------- Status ----------

function setStatus(tipo, msg) {
    const el = document.getElementById("status");
    el.className = `status ${tipo}`;
    el.textContent = msg;
}

// ---------- HTTP helpers ----------

async function postJson(url, body) {
    const res = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-User-CPF": currentUser.cpf
        },
        body: JSON.stringify(body)
    });
    const texto = await res.text();
    if (!res.ok) throw new Error(texto || `Falha em ${url}`);
    return texto;
}

async function getJson(url) {
    const res = await fetch(url, {
        headers: { "X-User-CPF": currentUser.cpf }
    });
    if (!res.ok) {
        const texto = await res.text();
        throw new Error(texto || `Falha em ${url}`);
    }
    return res.json();
}

// ---------- Resolução de IDs ----------

async function buscarIdCursoPorNome(nomeCurso) {
    const cursos = await getJson(`${API}/curso`);
    const encontrado = cursos.find(c => c.nome_curso === nomeCurso);
    if (!encontrado) throw new Error(`Não foi possível localizar o curso "${nomeCurso}" após criar.`);
    return encontrado.id_curso;
}

async function buscarIdModuloPorTitulo(idCurso, tituloModulo) {
    const lista = await getJson(`${API}/curso/${idCurso}/modulos-aulas`);
    const item = lista.find(x => x.modulo && x.modulo.titulo === tituloModulo);
    if (!item) throw new Error(`Não foi possível localizar o módulo "${tituloModulo}".`);
    return item.modulo.id_modulo;
}

// ---------- Validação ----------

function validarFormulario(curso) {
    if (!curso.nomeCurso || curso.nomeCurso.length < 3) {
        return "Informe um nome de curso com pelo menos 3 caracteres.";
    }
    if (curso.preco === null || isNaN(curso.preco) || curso.preco < 0) {
        return "Informe um preço válido (≥ 0).";
    }
    for (const [i, m] of modulos.entries()) {
        if (!m.titulo || !m.titulo.trim()) {
            return `Módulo ${i + 1}: título é obrigatório.`;
        }
        const carga = Number(m.carga_horaria);
        if (!Number.isInteger(carga) || carga <= 0) {
            return `Módulo ${i + 1}: carga horária deve ser um inteiro > 0.`;
        }
        for (const [j, a] of m.aulas.entries()) {
            if (!a.titulo || !a.titulo.trim()) {
                return `Módulo ${i + 1} / Aula ${j + 1}: título é obrigatório.`;
            }
            if (!a.link_do_video || !a.link_do_video.trim()) {
                return `Módulo ${i + 1} / Aula ${j + 1}: link do vídeo é obrigatório.`;
            }
        }
    }
    return null;
}

// ---------- Fluxo principal ----------

async function criarCurso() {
    const btn = document.getElementById("btnSalvar");

    const curso = {
        nomeCurso: document.getElementById("nomeCurso").value.trim(),
        preco: parseFloat(document.getElementById("precoCurso").value),
        descricaoCurso: document.getElementById("descricaoCurso").value.trim(),
        cpfProfessor: currentUser.cpf,
        categorias: pegarCategorias()
    };

    const erro = validarFormulario(curso);
    if (erro) {
        setStatus("err", erro);
        return;
    }

    btn.disabled = true;
    setStatus("info", "Criando curso...");

    try {
        // 1) Criar curso
        await postJson(`${API}/curso`, curso);

        // 2) Resolver id_curso
        setStatus("info", "Curso criado. Buscando ID...");
        const idCurso = await buscarIdCursoPorNome(curso.nomeCurso);

        // 3) Criar cada módulo, resolver id_modulo, criar aulas
        for (const [i, modulo] of modulos.entries()) {
            setStatus("info", `Criando módulo ${i + 1}/${modulos.length}: "${modulo.titulo}"...`);

            await postJson(`${API}/modulo/save`, {
                id_curso: idCurso,
                titulo: modulo.titulo.trim(),
                carga_horaria: parseInt(modulo.carga_horaria, 10),
                descricao: modulo.descricao || ""
            });

            const idModulo = await buscarIdModuloPorTitulo(idCurso, modulo.titulo.trim());

            for (const [j, aula] of modulo.aulas.entries()) {
                setStatus("info",
                    `Módulo ${i + 1}: criando aula ${j + 1}/${modulo.aulas.length} ("${aula.titulo}")...`);

                await postJson(`${API}/aula/save`, {
                    id_curso: idCurso,
                    id_modulo: idModulo,
                    titulo: aula.titulo.trim(),
                    link_do_video: aula.link_do_video.trim(),
                    descricao: aula.descricao || ""
                });
            }
        }

        setStatus("ok", "Curso, módulos e aulas criados com sucesso!");
        await reloadUser();
        setTimeout(() => {
            window.location.href = "../home/home-professor.html";
        }, 800);

    } catch (err) {
        console.error(err);
        setStatus("err", "Erro: " + err.message);
        btn.disabled = false;
    }
}
