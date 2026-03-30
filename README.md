# Cesar Edu

O **Cesar Edu** é um projeto acadêmico desenvolvido na faculdade com foco em modelagem e implementação de banco de dados. A proposta consiste na criação de uma plataforma de cursos online, onde é possível gerenciar usuários, instrutores, cursos, matrículas e conteúdos educacionais.

O sistema foi pensado para simular um ambiente real de ensino digital, permitindo o cadastro de alunos e professores, a oferta de cursos em diferentes áreas e o acompanhamento do progresso dos estudantes. Além disso, o projeto explora conceitos fundamentais de banco de dados, como modelagem relacional, integridade de dados, consultas e organização eficiente das informações.

O objetivo principal é aplicar, na prática, os conceitos de banco de dados em uma solução estruturada.

<img src="resource\cesar_edu.png" alt="Logo do projeto" width="350"/>

## 👥 Equipe de Desenvolvimento

- [**Eric Gonçalve**](https://github.com/eric-albuquer) 
- [**João Passos**](https://github.com/iampassos)
- [**Mircio Ferreira**](https://github.com/Mircio-Ferreira)
- [**Gabriel Aniceto**](https://github.com/gabrielaniceto1)

## Orientadora

* Natacha Targino

---
## Banco de Dados
<details>
<summary><strong>Modelo Conceitual</strong></summary>
<br>

<div align="center">
  
  <img src="resourse\modelo_conceitual.png" width="700" alt="Modelo Conceitual">
  <br>
  
  <br> 
  </div>
</details>

<details>
<summary><strong>Modelo Lógico</strong></summary> 
<br>



## Esquema Relacional

🔗   - [Documento Esquema Relacional - Cesar Edu](https://docs.google.com/document/d/1NKjeO3JPzk_YI0Vg5SAW6i60NHyMUUELvO5VN3UEHv8/edit?usp=sharing)

  </details>

## ⚙️ Como executar o projeto

### Como executar o backend com Docker

### Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- [Docker](https://www.docker.com/)
- Docker Compose

Para verificar se está tudo certo, rode no terminal ou CMD:

```bash
docker --version
docker compose version
```
Ápos a instalação, clone o repositório na pasta de sua preferência:
``` bash
git clone https://github.com/Mircio-Ferreira/Banco-de-Dados

```
Entre na pasta "backend"
```bash
cd backend
```
Execute o comando para subir a aplicação via docker
```bash
docker compose up --build
```
Após esse comando, o docker vai executar e subir a aplicação automaticamente. A porta em que a aplicação vai subir no browser será a:
```bash
http://localhost:8080
```
Para a melhor visualização e entendimento dos endpoints, entre na url:
```bash
http://localhost:8080/swagger-ui/index.html
```
Se quiser parar a aplicação, basta executar o comando no terminal ou CMD:
```bash
docker compose stop
```
---

### 🌐 Frontend

#### Opção 1: Live Server (VS Code)

1. Abra o projeto no VS Code  
2. Clique com o botão direito em:

```
front-end/login/login.html
```

3. Clique em **"Open with Live Server"**

Acesse no navegador:

```
http://localhost:51220/front-end/login/login.html
```

⚠️ **Observação:**  
A porta pode variar dependendo da sua máquina.

---

#### ✅ Opção 2: Sem extensão (recomendado)

Você pode rodar um servidor simples com Python ou Node:

##### 🔹 Python (mais fácil)

```bash
cd front-end
python -m http.server 5500
```

Acesse:

```
http://localhost:5500/front-end/login/login.html
```

---

##### 🔹 Node.js (http-server)

Instale:

```bash
npm install -g http-server
```

Execute:

```bash
cd front-end
http-server
```

---

### 🔄 Fluxo da Aplicação

Após abrir o login:

- ✅ Cadastro de usuário  
- ✅ Login automático  
- ✅ Navegação entre telas  
- ✅ Consumo do backend via API  

👉 Todo o fluxo da aplicação já está integrado entre as telas.

---

### 🧠 Observações Importantes

- O "token" atualmente é o **CPF do usuário armazenado no localStorage**
- O backend utiliza validações completas de:
  - CPF
  - Email
  - Senha
  - Telefones

---

### 🏗️ Arquitetura

O sistema segue uma arquitetura baseada em:

- Controllers  
- Services  
- Repositories  