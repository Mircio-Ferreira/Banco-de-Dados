/* 📦 Simulação do backend */
const curso = {
    nome: "JavaScript Completo",
    modulos: [
        {
            titulo: "Introdução",
            aulas: [
                {
                    titulo: "O que é JavaScript",
                    descricao: "Entenda a origem do JavaScript, onde ele é utilizado e por que é uma das linguagens mais populares do mundo.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: [
                        { nome: "Slides da aula", link: "#" },
                        { nome: "Artigo MDN", link: "https://developer.mozilla.org/pt-BR/docs/Web/JavaScript" }
                    ]
                },
                {
                    titulo: "Ambiente de desenvolvimento",
                    descricao: "Aprenda a configurar seu ambiente com VSCode e Node.js.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: [
                        { nome: "Download Node.js", link: "https://nodejs.org" }
                    ]
                }
            ]
        },
        {
            titulo: "Fundamentos",
            aulas: [
                {
                    titulo: "Variáveis",
                    descricao: "Conheça var, let e const e quando usar cada uma.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: [
                        { nome: "Exercícios", link: "#" }
                    ]
                },
                {
                    titulo: "Tipos de dados",
                    descricao: "String, Number, Boolean, Null, Undefined e Object.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: []
                },
                {
                    titulo: "Operadores",
                    descricao: "Operadores aritméticos, lógicos e de comparação.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: [
                        { nome: "Lista de exercícios", link: "#" }
                    ]
                }
            ]
        },
        {
            titulo: "Funções",
            aulas: [
                {
                    titulo: "Funções básicas",
                    descricao: "Como declarar e utilizar funções.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: []
                },
                {
                    titulo: "Arrow Functions",
                    descricao: "Sintaxe moderna para funções.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: [
                        { nome: "Comparação com funções tradicionais", link: "#" }
                    ]
                },
                {
                    titulo: "Callbacks",
                    descricao: "Entenda funções como argumentos.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: []
                }
            ]
        },
        {
            titulo: "DOM",
            aulas: [
                {
                    titulo: "Selecionando elementos",
                    descricao: "getElementById, querySelector e outros.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: []
                },
                {
                    titulo: "Eventos",
                    descricao: "Click, input, submit e mais.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: [
                        { nome: "Exemplo prático", link: "#" }
                    ]
                },
                {
                    titulo: "Manipulação de elementos",
                    descricao: "Alterando conteúdo e estilos via JS.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: []
                }
            ]
        },
        {
            titulo: "Projeto Final",
            aulas: [
                {
                    titulo: "Planejamento do projeto",
                    descricao: "Definindo escopo e funcionalidades.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: []
                },
                {
                    titulo: "Implementação",
                    descricao: "Construindo o projeto passo a passo.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: [
                        { nome: "Código base", link: "#" },
                        { nome: "Repositório GitHub", link: "#" }
                    ]
                },
                {
                    titulo: "Deploy",
                    descricao: "Publicando seu projeto online.",
                    video: "https://www.youtube.com/embed/W6NZfCO5SIk",
                    materiais: []
                }
            ]
        }
    ]
};

function getCursoId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

let aulaAtual = null;

/* 🎬 Carregar aula */
function carregarAula(aula) {
    aulaAtual = aula;

    document.getElementById("video").innerHTML =
        `<iframe width="100%" height="100%" src="${aula.video}" frameborder="0" allowfullscreen></iframe>`;

    document.getElementById("tituloAula").innerText = aula.titulo;
    document.getElementById("descricaoAula").innerText = aula.descricao;

    const matDiv = document.getElementById("materiais");
    matDiv.innerHTML = "";

    aula.materiais.forEach(m => {
        const a = document.createElement("a");
        a.href = m.link;
        a.innerText = m.nome;
        matDiv.appendChild(a);
    });
}

function renderSidebar() {
    const sidebar = document.getElementById("sidebar");

    curso.modulos.forEach(mod => {
        const modDiv = document.createElement("div");
        modDiv.className = "modulo";

        modDiv.innerHTML = `<h3>📦 ${mod.titulo}</h3>`;

        mod.aulas.forEach(aula => {
            const aulaDiv = document.createElement("div");
            aulaDiv.className = "aula";
            aulaDiv.innerText = aula.titulo;

            aulaDiv.onclick = () => {
                carregarAula(aula);
                document.querySelectorAll(".aula").forEach(a => a.classList.remove("ativa"));
                aulaDiv.classList.add("ativa");
            };

            modDiv.appendChild(aulaDiv);
        });

        sidebar.appendChild(modDiv);
    });
}

/* 🚀 Init */
renderSidebar();
carregarAula(curso.modulos[0].aulas[0]);