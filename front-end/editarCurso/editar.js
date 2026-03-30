let cursoId;

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    cursoId = params.get("id");

    if (!cursoId) {
        alert("Curso inválido");
        return;
    }

    const cursos = currentUser.cursosLecionados;
    let curso
    for (const c of cursos) {
        if (c.id_curso == cursoId) {
            curso = c
            break
        }
    }
    preencherFormulario(curso);
});

function preencherFormulario(curso) {
    document.getElementById("nomeCurso").value = curso.nome_curso;
    document.getElementById("precoCurso").value = curso.preco;
    document.getElementById("descricaoCurso").value = curso.descricao_curso;

    // categorias
    const catList = document.getElementById("categoriaList");
    curso.categorias?.forEach(cat => {
        const div = document.createElement("div");

        const text = document.createElement("span");
        text.textContent = cat.nome;

        const removeBtn = document.createElement("button");
        removeBtn.textContent = "X";
        removeBtn.style.marginLeft = "10px";

        removeBtn.onclick = () => {
            div.remove();
        };

        div.appendChild(text);
        div.appendChild(removeBtn);

        catList.appendChild(div);
    });

    // módulos
    const modulosDiv = document.getElementById("modulos");
    curso.modulos?.forEach(mod => {
        const div = document.createElement("div");
        div.classList.add("nested");

        div.innerHTML = `
                    <input value="${mod.nome}" placeholder="Nome do módulo">
                    <textarea placeholder="Descrição">${mod.descricao}</textarea>
                `;

        modulosDiv.appendChild(div);
    });
}

async function atualizarCurso() {
    const curso = {
        nomeCurso: document.getElementById("nomeCurso").value,
        preco: document.getElementById("precoCurso").value,
        descricaoCurso: document.getElementById("descricaoCurso").value,
        categorias: pegarCategorias(),
        //modulos: pegarModulos()
        cpfProfessor: currentUser.cpf
    };

    console.log(curso)

    try {
        const response = await fetch(`http://localhost:8080/api/v1/curso/${cursoId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-User-CPF": currentUser.cpf
            },
            body: JSON.stringify(curso)
        });

        const data = await response.text();

        if (!response.ok) {
            alert("Erro: " + data);
            return;
        }

        alert("Curso atualizado com sucesso!");
        await reloadUser()
        window.location.href = "../home/home-professor.html";

    } catch (error) {
        console.error(error);
        alert("Erro ao atualizar curso");
    }
}

function pegarCategorias() {
    const lista = document.getElementById("categoriaList").children;
    const categorias = [];

    for (let item of lista) {
        categorias.push(item.querySelector("span").textContent);
    }

    return categorias;
}

function pegarModulos() {
    const modulosHTML = document.getElementById("modulos").children;
    const modulos = [];

    for (let modulo of modulosHTML) {
        const nome = modulo.querySelector("input").value;
        const descricao = modulo.querySelector("textarea").value;

        modulos.push({ nome, descricao });
    }

    return modulos;
}

async function deletarCurso() {

    const confirmar = confirm("Tem certeza que deseja deletar este curso?");

    if (!confirmar) return;

    try {
        const response = await fetch(`http://localhost:8080/api/v1/curso/${cursoId}`, {
            method: "DELETE",
            headers: {
                "X-User-CPF": currentUser.cpf
            }
        });

        const data = await response.text();

        if (!response.ok) {
            alert("Erro: " + data);
            return;
        }

        alert("Curso deletado com sucesso!");
        await reloadUser()

        // 🔥 volta pro dashboard
        window.location.href = "../home/home-professor.html";

    } catch (error) {
        console.error(error);
        alert("Erro ao deletar curso");
    }
}

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