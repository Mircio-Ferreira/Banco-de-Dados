let categorias = [];
let modulos = [];

function addCategoria() {
    const input = document.getElementById("categoriaInput");
    const list = document.getElementById("categoriaList");

    if (input.value.trim() === "") return;

    const item = document.createElement("div");

    const text = document.createElement("span");
    text.textContent = input.value;

    const removeBtn = document.createElement("button");
    removeBtn.textContent = "X";
    removeBtn.style.marginLeft = "10px";

    removeBtn.onclick = () => {
        item.remove();
    };

    item.appendChild(text);
    item.appendChild(removeBtn);

    list.appendChild(item);
    input.value = "";
}

function addModulo() {
    const modulo = {
        titulo: "",
        descricao: "",
        carga: "",
        aulas: []
    };

    modulos.push(modulo);

    renderModulos();
}

function addAula(moduloIndex) {
    modulos[moduloIndex].aulas.push({
        titulo: "",
        descricao: "",
        video: "",
        materiais: []
    });

    renderModulos();
}

function addMaterial(moduloIndex, aulaIndex) {
    modulos[moduloIndex].aulas[aulaIndex].materiais.push({
        nome: "",
        link: ""
    });

    renderModulos();
}

function renderModulos() {
    const container = document.getElementById("modulos");
    container.innerHTML = "";

    modulos.forEach((modulo, mIndex) => {
        const div = document.createElement("div");
        div.className = "box";

        div.innerHTML = `
            <input placeholder="Título do módulo" onchange="modulos[${mIndex}].titulo=this.value">
            <input placeholder="Descrição" onchange="modulos[${mIndex}].descricao=this.value">
            <input placeholder="Carga horária" onchange="modulos[${mIndex}].carga=this.value">

            <button onclick="addAula(${mIndex})">Adicionar aula</button>

            <div class="nested" id="aulas-${mIndex}"></div>
        `;

        container.appendChild(div);

        const aulasDiv = div.querySelector(`#aulas-${mIndex}`);

        modulo.aulas.forEach((aula, aIndex) => {
            const aulaDiv = document.createElement("div");
            aulaDiv.className = "box";

            aulaDiv.innerHTML = `
                <input placeholder="Título da aula" onchange="modulos[${mIndex}].aulas[${aIndex}].titulo=this.value">
                <input placeholder="Descrição" onchange="modulos[${mIndex}].aulas[${aIndex}].descricao=this.value">
                <input placeholder="Link do vídeo" onchange="modulos[${mIndex}].aulas[${aIndex}].video=this.value">

                <button onclick="addMaterial(${mIndex}, ${aIndex})">Adicionar material</button>

                <div class="nested" id="materiais-${mIndex}-${aIndex}"></div>
            `;

            aulasDiv.appendChild(aulaDiv);

            const matDiv = aulaDiv.querySelector(`#materiais-${mIndex}-${aIndex}`);

            aula.materiais.forEach((mat, matIndex) => {
                const mDiv = document.createElement("div");

                mDiv.innerHTML = `
                    <input placeholder="Nome do material" onchange="modulos[${mIndex}].aulas[${aIndex}].materiais[${matIndex}].nome=this.value">
                    <input placeholder="Link" onchange="modulos[${mIndex}].aulas[${aIndex}].materiais[${matIndex}].link=this.value">
                `;

                matDiv.appendChild(mDiv);
            });
        });
    });
}

function criarCurso() {
    const items = document.getElementById("categoriaList").children;

    for (let item of items) {
        categorias.push(item.querySelector("span").textContent);
    }

    const curso = {
        nome: document.getElementById("nomeCurso").value,
        preco: document.getElementById("precoCurso").value,
        descricao: document.getElementById("descricaoCurso").value,
        categorias: categorias,
        modulos: modulos
    };

    console.log(curso);
    // Enviar para o back end
    //alert("Curso criado!");
}