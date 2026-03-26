CREATE TABLE IF NOT EXISTS Usuario (
                                       cpf CHAR(11) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    numero INT NOT NULL,
    cep CHAR(8) NOT NULL
    );

CREATE TABLE IF NOT EXISTS Telefone (
                                        cpf CHAR(11) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    PRIMARY KEY (cpf, numero),
    CONSTRAINT fk_telefone_usuario
    FOREIGN KEY (cpf)
    REFERENCES Usuario(cpf)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS Professor (
                                         cpf_professor CHAR(11) PRIMARY KEY,
    CONSTRAINT fk_professor_usuario
    FOREIGN KEY (cpf_professor)
    REFERENCES Usuario(cpf)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS CertificadoProfessor (
                                                    cpf_professor CHAR(11) NOT NULL,
    titulo_certificado VARCHAR(150) NOT NULL,
    PRIMARY KEY (cpf_professor, titulo_certificado),
    CONSTRAINT fk_certificado_professor
    FOREIGN KEY (cpf_professor)
    REFERENCES Professor(cpf_professor)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS Aluno (
                                     cpf_aluno CHAR(11) PRIMARY KEY,
    CONSTRAINT fk_aluno_usuario
    FOREIGN KEY (cpf_aluno)
    REFERENCES Usuario(cpf)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS Curso (
                                     id_curso BIGSERIAL PRIMARY KEY,
                                     nome VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    descricao TEXT
    );

CREATE TABLE IF NOT EXISTS Compra (
                                      cpf_aluno CHAR(11) NOT NULL,
    id_curso BIGINT NOT NULL,
    data_matricula DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (cpf_aluno, id_curso),
    CONSTRAINT fk_compra_aluno
    FOREIGN KEY (cpf_aluno)
    REFERENCES Aluno(cpf_aluno)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT fk_compra_curso
    FOREIGN KEY (id_curso)
    REFERENCES Curso(id_curso)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS Leciona (
                                       cpf_professor CHAR(11) NOT NULL,
    id_curso BIGINT NOT NULL,
    PRIMARY KEY (cpf_professor, id_curso),
    CONSTRAINT fk_leciona_professor
    FOREIGN KEY (cpf_professor)
    REFERENCES Professor(cpf_professor)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT fk_leciona_curso
    FOREIGN KEY (id_curso)
    REFERENCES Curso(id_curso)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );