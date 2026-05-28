BEGIN;

-- 1. INSERINDO USUÁRIOS (60 no total: 30 Professores e 30 Alunos)
INSERT INTO usuario (cpf, nome, email, senha, logradouro, numero, cep) VALUES
    -- Professores (CPFs iniciados em 1)
    ('10000000001', 'Alan Turing', 'alan.turing@tech.com', 'hash1', 'Rua Enigma', 10, '50000001'),
    ('10000000002', 'Ada Lovelace', 'ada.l@tech.com', 'hash2', 'Av. Algoritmo', 20, '50000002'),
    ('10000000003', 'Linus Torvalds', 'linus@linux.org', 'hash3', 'Rua Kernel', 30, '50000003'),
    ('10000000004', 'Grace Hopper', 'grace@navy.mil', 'hash4', 'Av. Compilador', 40, '50000004'),
    ('10000000005', 'Tim Berners-Lee', 'tim@web.org', 'hash5', 'Rua WWW', 50, '50000005'),
    ('10000000006', 'Brendan Eich', 'brendan@js.org', 'hash6', 'Av. Script', 60, '50000006'),
    ('10000000007', 'Guido van Rossum', 'guido@python.org', 'hash7', 'Rua Serpente', 70, '50000007'),
    ('10000000008', 'James Gosling', 'james@java.com', 'hash8', 'Av. Cafe', 80, '50000008'),
    ('10000000009', 'Bjarne Stroustrup', 'bjarne@cpp.org', 'hash9', 'Rua Plus Plus', 90, '50000009'),
    ('10000000010', 'Dennis Ritchie', 'dennis@c.org', 'hash10', 'Av. Unix', 100, '50000010'),
    ('10000000011', 'Ken Thompson', 'ken@unix.com', 'hash11', 'Rua Bell', 110, '50000011'),
    ('10000000012', 'Richard Stallman', 'rms@gnu.org', 'hash12', 'Av. Livre', 120, '50000012'),
    ('10000000013', 'Anders Hejlsberg', 'anders@csharp.com', 'hash13', 'Rua Delphi', 130, '50000013'),
    ('10000000014', 'Yukihiro Matsumoto', 'matz@ruby.org', 'hash14', 'Av. Joia', 140, '50000014'),
    ('10000000015', 'Ryan Dahl', 'ryan@node.js', 'hash15', 'Rua Assincrona', 150, '50000015'),
    ('10000000016', 'Evan You', 'evan@vue.js', 'hash16', 'Av. Reativa', 160, '50000016'),
    ('10000000017', 'Jordan Walke', 'jordan@react.js', 'hash17', 'Rua Componente', 170, '50000017'),
    ('10000000018', 'Solomon Hykes', 'solomon@docker.com', 'hash18', 'Av. Container', 180, '50000018'),
    ('10000000019', 'Jeff Bezos', 'jeff@aws.com', 'hash19', 'Rua Nuvem', 190, '50000019'),
    ('10000000020', 'Satya Nadella', 'satya@azure.com', 'hash20', 'Av. Microsoft', 200, '50000020'),
    ('10000000021', 'Sundar Pichai', 'sundar@gcp.com', 'hash21', 'Rua Google', 210, '50000021'),
    ('10000000022', 'Demis Hassabis', 'demis@deepmind.com', 'hash22', 'Av. Neural', 220, '50000022'),
    ('10000000023', 'Ilya Sutskever', 'ilya@openai.com', 'hash23', 'Rua GPT', 230, '50000023'),
    ('10000000024', 'Andrew Ng', 'andrew@coursera.org', 'hash24', 'Av. Learn', 240, '50000024'),
    ('10000000025', 'Geoffrey Hinton', 'geoffrey@toronto.edu', 'hash25', 'Rua Backprop', 250, '50000025'),
    ('10000000026', 'Yann LeCun', 'yann@meta.com', 'hash26', 'Av. Visao', 260, '50000026'),
    ('10000000027', 'Ian Goodfellow', 'ian@gan.com', 'hash27', 'Rua Adversaria', 270, '50000027'),
    ('10000000028', 'Vitalik Buterin', 'vitalik@eth.org', 'hash28', 'Av. Bloco', 280, '50000028'),
    ('10000000029', 'Satoshi Nakamoto', 'satoshi@btc.com', 'hash29', 'Rua Cadeia', 290, '50000029'),
    ('10000000030', 'Gavin Wood', 'gavin@polkadot.com', 'hash30', 'Av. Web3', 300, '50000030'),

    -- Alunos (CPFs iniciados em 2, nomes brasileiros criativos)
    ('20000000001', 'Lucas Mendes', 'lucas.m@email.com', 'hash', 'Rua A', 1, '60000001'),
    ('20000000002', 'Mariana Costa', 'mariana.c@email.com', 'hash', 'Rua B', 2, '60000002'),
    ('20000000003', 'Carlos Eduardo', 'cadu@email.com', 'hash', 'Rua C', 3, '60000003'),
    ('20000000004', 'Fernanda Lima', 'nanda@email.com', 'hash', 'Rua D', 4, '60000004'),
    ('20000000005', 'João Pedro', 'jp@email.com', 'hash', 'Rua E', 5, '60000005'),
    ('20000000006', 'Beatriz Silva', 'bia@email.com', 'hash', 'Rua F', 6, '60000006'),
    ('20000000007', 'Rafael Rocha', 'rafa@email.com', 'hash', 'Rua G', 7, '60000007'),
    ('20000000008', 'Camila Alves', 'camila@email.com', 'hash', 'Rua H', 8, '60000008'),
    ('20000000009', 'Diego Nunes', 'diego@email.com', 'hash', 'Rua I', 9, '60000009'),
    ('20000000010', 'Juliana Castro', 'ju@email.com', 'hash', 'Rua J', 10, '60000010'),
    ('20000000011', 'Mateus Moura', 'mateus@email.com', 'hash', 'Rua K', 11, '60000011'),
    ('20000000012', 'Letícia Borges', 'le@email.com', 'hash', 'Rua L', 12, '60000012'),
    ('20000000013', 'Thiago Ribeiro', 'thiago@email.com', 'hash', 'Rua M', 13, '60000013'),
    ('20000000014', 'Amanda Freitas', 'amanda@email.com', 'hash', 'Rua N', 14, '60000014'),
    ('20000000015', 'Felipe Santos', 'felipe@email.com', 'hash', 'Rua O', 15, '60000015'),
    ('20000000016', 'Bruna Martins', 'bruna@email.com', 'hash', 'Rua P', 16, '60000016'),
    ('20000000017', 'Gustavo Lima', 'gustavo@email.com', 'hash', 'Rua Q', 17, '60000017'),
    ('20000000018', 'Isabela Ramos', 'isa@email.com', 'hash', 'Rua R', 18, '60000018'),
    ('20000000019', 'Rodrigo Teixeira', 'rodrigo@email.com', 'hash', 'Rua S', 19, '60000019'),
    ('20000000020', 'Larissa Vieira', 'lari@email.com', 'hash', 'Rua T', 20, '60000020'),
    ('20000000021', 'Leonardo Pires', 'leo@email.com', 'hash', 'Rua U', 21, '60000021'),
    ('20000000022', 'Natália Dias', 'nati@email.com', 'hash', 'Rua V', 22, '60000022'),
    ('20000000023', 'Renato Moraes', 'renato@email.com', 'hash', 'Rua W', 23, '60000023'),
    ('20000000024', 'Carolina Farias', 'carol@email.com', 'hash', 'Rua X', 24, '60000024'),
    ('20000000025', 'Eduardo Machado', 'edu@email.com', 'hash', 'Rua Y', 25, '60000025'),
    ('20000000026', 'Tatiane Mendes', 'tati@email.com', 'hash', 'Rua Z', 26, '60000026'),
    ('20000000027', 'Vinícius Aragão', 'vini@email.com', 'hash', 'Rua AA', 27, '60000027'),
    ('20000000028', 'Priscila Novaes', 'pri@email.com', 'hash', 'Rua BB', 28, '60000028'),
    ('20000000029', 'André Monteiro', 'andre@email.com', 'hash', 'Rua CC', 29, '60000029'),
    ('20000000030', 'Sofia Carvalho', 'sofia@email.com', 'hash', 'Rua DD', 30, '60000030')
ON CONFLICT DO NOTHING;
@@

-- 2. TELEFONES (30 Inserções)
INSERT INTO telefone (cpf, numero) VALUES
    ('10000000001', '11999990001'), ('10000000002', '11999990002'), ('10000000003', '11999990003'),
    ('10000000004', '11999990004'), ('10000000005', '11999990005'), ('10000000006', '11999990006'),
    ('10000000007', '11999990007'), ('10000000008', '11999990008'), ('10000000009', '11999990009'),
    ('10000000010', '11999990010'), ('10000000011', '11999990011'), ('10000000012', '11999990012'),
    ('10000000013', '11999990013'), ('10000000014', '11999990014'), ('10000000015', '11999990015'),
    ('20000000001', '11988880001'), ('20000000002', '11988880002'), ('20000000003', '11988880003'),
    ('20000000004', '11988880004'), ('20000000005', '11988880005'), ('20000000006', '11988880006'),
    ('20000000007', '11988880007'), ('20000000008', '11988880008'), ('20000000009', '11988880009'),
    ('20000000010', '11988880010'), ('20000000011', '11988880011'), ('20000000012', '11988880012'),
    ('20000000013', '11988880013'), ('20000000014', '11988880014'), ('20000000015', '11988880015')
ON CONFLICT DO NOTHING;
@@

-- 3. PROFESSORES (30 inserções)
INSERT INTO professor (cpf_professor) VALUES
    ('10000000001'), ('10000000002'), ('10000000003'), ('10000000004'), ('10000000005'),
    ('10000000006'), ('10000000007'), ('10000000008'), ('10000000009'), ('10000000010'),
    ('10000000011'), ('10000000012'), ('10000000013'), ('10000000014'), ('10000000015'),
    ('10000000016'), ('10000000017'), ('10000000018'), ('10000000019'), ('10000000020'),
    ('10000000021'), ('10000000022'), ('10000000023'), ('10000000024'), ('10000000025'),
    ('10000000026'), ('10000000027'), ('10000000028'), ('10000000029'), ('10000000030')
ON CONFLICT DO NOTHING;
@@

-- 4. CERTIFICAÇÕES DOS PROFESSORES (30 inserções)
INSERT INTO certificacoes (cpf_professor, titulo_certificado) VALUES
    ('10000000001', 'Doutor em Criptografia e Teoria dos Grafos'),
    ('10000000002', 'Mestre em Lógica de Programação'),
    ('10000000003', 'Linux Foundation Certified Engineer (LFCE)'),
    ('10000000004', 'Especialista em Compiladores'),
    ('10000000005', 'W3C Web Standard Creator'),
    ('10000000006', 'JavaScript Expert Creator'),
    ('10000000007', 'Python Institute Certified Professional'),
    ('10000000008', 'Oracle Certified Master Java EE'),
    ('10000000009', 'C++ Institute Certified Advanced Programmer'),
    ('10000000010', 'C Programming Language Master'),
    ('10000000011', 'UNIX Systems Architecture Expert'),
    ('10000000012', 'Especialista em Licenças Open Source'),
    ('10000000013', 'Microsoft Certified: C# Specialist'),
    ('10000000014', 'Ruby Association Certified Ruby Programmer'),
    ('10000000015', 'Node.js Certified Developer'),
    ('10000000016', 'Vue.js Core Team Certification'),
    ('10000000017', 'React Native and React.js Specialist'),
    ('10000000018', 'Docker Certified Associate (DCA)'),
    ('10000000019', 'AWS Certified Solutions Architect - Professional'),
    ('10000000020', 'Microsoft Certified: Azure Administrator'),
    ('10000000021', 'Google Cloud Certified Professional Cloud Architect'),
    ('10000000022', 'DeepMind AI Expert Certification'),
    ('10000000023', 'OpenAI Generative AI Specialist'),
    ('10000000024', 'Stanford Machine Learning Certificate'),
    ('10000000025', 'Ph.D em Redes Neurais Artificiais'),
    ('10000000026', 'Especialista em Visão Computacional'),
    ('10000000027', 'Master em Generative Adversarial Networks'),
    ('10000000028', 'Ethereum Smart Contracts Auditor'),
    ('10000000029', 'Bitcoin Protocol Architecture Certified'),
    ('10000000030', 'Polkadot and Web3 Specialist')
ON CONFLICT DO NOTHING;
@@

-- 5. ALUNOS (30 inserções)
INSERT INTO aluno (cpf_aluno) VALUES
    ('20000000001'), ('20000000002'), ('20000000003'), ('20000000004'), ('20000000005'),
    ('20000000006'), ('20000000007'), ('20000000008'), ('20000000009'), ('20000000010'),
    ('20000000011'), ('20000000012'), ('20000000013'), ('20000000014'), ('20000000015'),
    ('20000000016'), ('20000000017'), ('20000000018'), ('20000000019'), ('20000000020'),
    ('20000000021'), ('20000000022'), ('20000000023'), ('20000000024'), ('20000000025'),
    ('20000000026'), ('20000000027'), ('20000000028'), ('20000000029'), ('20000000030')
ON CONFLICT DO NOTHING;
@@

-- 6. CURSOS (30 inserções de tecnologias variadas)
INSERT INTO curso (id_curso, nome, preco, descricao) VALUES
    (1, 'Lógica de Programação e Algoritmos', 49.90, 'O básico que todo dev precisa saber.'),
    (2, 'Estruturas de Dados Essenciais', 59.90, 'Pilhas, Filas, Listas e Árvores na prática.'),
    (3, 'Administração Linux Avançada', 120.00, 'Domine o terminal e processos do Linux.'),
    (4, 'Compiladores: Teoria e Prática', 199.90, 'Crie o seu próprio compilador do zero.'),
    (5, 'Arquitetura da Internet', 80.00, 'Como a web funciona por baixo dos panos.'),
    (6, 'JavaScript Completo 2026', 150.00, 'Do básico ao assíncrono avançado.'),
    (7, 'Python para Ciência de Dados', 190.00, 'Pandas, Numpy e visualização de dados.'),
    (8, 'Java Spring Boot 3', 250.00, 'Crie APIs RESTful seguras e escaláveis.'),
    (9, 'C++ Moderno e Alta Performance', 220.00, 'Gerenciamento de memória e concorrência.'),
    (10, 'C para Sistemas Embarcados', 100.00, 'Programação baixo nível sem dor de cabeça.'),
    (11, 'Shell Script e Automação UNIX', 85.00, 'Automatize tarefas maçantes com scripts.'),
    (12, 'Cultura Open Source', 0.00, 'Como contribuir para grandes projetos livres.'),
    (13, 'C# e .NET Core Enterprise', 210.00, 'Arquitetura limpa para grandes empresas.'),
    (14, 'Ruby on Rails na Prática', 160.00, 'Desenvolvimento ágil de produtos web.'),
    (15, 'Backend de Alta Escala com Node.js', 180.00, 'Microserviços com Event Loop Otimizado.'),
    (16, 'Vue.js 3 e Composition API', 130.00, 'Interfaces rápidas e reativas.'),
    (17, 'React.js e Next.js', 190.00, 'Frontend moderno e Server Side Rendering.'),
    (18, 'Docker e Containers do Zero', 140.00, 'Empacote suas aplicações de forma definitiva.'),
    (19, 'AWS Solutions Architect', 300.00, 'Preparatório completo para a certificação SAA.'),
    (20, 'Microsoft Azure Fundamentals', 250.00, 'Os primeiros passos na nuvem da Microsoft.'),
    (21, 'Google Cloud Data Engineer', 280.00, 'Construa pipelines de Big Data no GCP.'),
    (22, 'Inteligência Artificial Clássica', 200.00, 'Algoritmos de busca e sistemas especialistas.'),
    (23, 'Engenharia de Prompt com GPT', 99.90, 'Extraia o máximo dos modelos de linguagem.'),
    (24, 'Machine Learning de A a Z', 240.00, 'Modelos de regressão, classificação e clusterização.'),
    (25, 'Deep Learning com PyTorch', 270.00, 'Redes neurais profundas do zero.'),
    (26, 'Visão Computacional Aplicada', 220.00, 'Detecção e reconhecimento de imagens.'),
    (27, 'Redes Adversárias Generativas (GANs)', 350.00, 'Crie imagens e sons do zero usando IA.'),
    (28, 'Smart Contracts com Solidity', 180.00, 'Programação na blockchain Ethereum.'),
    (29, 'Fundamentos do Bitcoin e Criptografia', 150.00, 'Como funciona o dinheiro descentralizado.'),
    (30, 'Web3 e Aplicações Descentralizadas (DApps)', 190.00, 'O futuro da internet e identidade digital.')
ON CONFLICT DO NOTHING;
@@

-- 7. LECIONA (30 inserções - Relacionando Professores aos Cursos)
INSERT INTO leciona (cpf_professor, id_curso) VALUES
    ('10000000001', 1), ('10000000002', 2), ('10000000003', 3), ('10000000004', 4), ('10000000005', 5),
    ('10000000006', 6), ('10000000007', 7), ('10000000008', 8), ('10000000009', 9), ('10000000010', 10),
    ('10000000011', 11), ('10000000012', 12), ('10000000013', 13), ('10000000014', 14), ('10000000015', 15),
    ('10000000016', 16), ('10000000017', 17), ('10000000018', 18), ('10000000019', 19), ('10000000020', 20),
    ('10000000021', 21), ('10000000022', 22), ('10000000023', 23), ('10000000024', 24), ('10000000025', 25),
    ('10000000026', 26), ('10000000027', 27), ('10000000028', 28), ('10000000029', 29), ('10000000030', 30)
ON CONFLICT DO NOTHING;
@@

-- 8. CATEGORIA (30 inserções)
INSERT INTO categoria (id_categoria, nome_da_categoria) VALUES
    (1, 'Fundamentos'), (2, 'Estruturas de Dados'), (3, 'Sistemas Operacionais'),
    (4, 'Ciência da Computação'), (5, 'Redes'), (6, 'Frontend'), (7, 'Data Science'),
    (8, 'Backend Java'), (9, 'Backend C++'), (10, 'Sistemas Embarcados'),
    (11, 'Automação'), (12, 'Open Source'), (13, 'Backend C#'), (14, 'Backend Ruby'),
    (15, 'Backend Node'), (16, 'Frameworks Frontend Vue'), (17, 'Frameworks Frontend React'),
    (18, 'DevOps Containers'), (19, 'Cloud Computing AWS'), (20, 'Cloud Computing Azure'),
    (21, 'Engenharia de Dados GCP'), (22, 'Inteligência Artificial'), (23, 'Prompt Engineering'),
    (24, 'Machine Learning'), (25, 'Deep Learning'), (26, 'Visão Computacional'),
    (27, 'Redes Neurais'), (28, 'Smart Contracts'), (29, 'Criptoeconomia'), (30, 'Web3')
ON CONFLICT DO NOTHING;
@@

-- 9. POSSUI (30 inserções - Ligando Cursos e Categorias 1 para 1)
INSERT INTO possui (id_categoria, id_curso) VALUES
    (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
    (11, 11), (12, 12), (13, 13), (14, 14), (15, 15), (16, 16), (17, 17), (18, 18),
    (19, 19), (20, 20), (21, 21), (22, 22), (23, 23), (24, 24), (25, 25), (26, 26),
    (27, 27), (28, 28), (29, 29), (30, 30)
ON CONFLICT DO NOTHING;
@@

-- 10. COMPRA (30 inserções - O Aluno 1 compra o Curso 1, Aluno 2 compra Curso 2, etc.)
INSERT INTO compra (id_curso, cpf_aluno, data_compra) VALUES
    (1, '20000000001', DATE '2026-05-01'), (2, '20000000002', DATE '2026-05-02'),
    (3, '20000000003', DATE '2026-05-03'), (4, '20000000004', DATE '2026-05-04'),
    (5, '20000000005', DATE '2026-05-05'), (6, '20000000006', DATE '2026-05-06'),
    (7, '20000000007', DATE '2026-05-07'), (8, '20000000008', DATE '2026-05-08'),
    (9, '20000000009', DATE '2026-05-09'), (10, '20000000010', DATE '2026-05-10'),
    (11, '20000000011', DATE '2026-05-11'), (12, '20000000012', DATE '2026-05-12'),
    (13, '20000000013', DATE '2026-05-13'), (14, '20000000014', DATE '2026-05-14'),
    (15, '20000000015', DATE '2026-05-15'), (16, '20000000016', DATE '2026-05-16'),
    (17, '20000000017', DATE '2026-05-17'), (18, '20000000018', DATE '2026-05-18'),
    (19, '20000000019', DATE '2026-05-19'), (20, '20000000020', DATE '2026-05-20'),
    (21, '20000000021', DATE '2026-05-21'), (22, '20000000022', DATE '2026-05-22'),
    (23, '20000000023', DATE '2026-05-23'), (24, '20000000024', DATE '2026-05-24'),
    (25, '20000000025', DATE '2026-05-25'), (26, '20000000026', DATE '2026-05-26'),
    (27, '20000000027', DATE '2026-05-27'), (28, '20000000028', DATE '2026-05-28'),
    (29, '20000000029', DATE '2026-05-29'), (30, '20000000030', DATE '2026-05-30')
ON CONFLICT DO NOTHING;
@@

-- 11. MODULO (30 inserções - 1 módulo por curso)
INSERT INTO modulo (id_curso, id_modulo, titulo, carga_horaria, descricao) VALUES
    (1, 1, 'Introdução à Lógica', 10, 'Conceitos iniciais.'), (2, 1, 'Listas e Arrays', 15, 'Módulo único.'),
    (3, 1, 'Comandos Essenciais', 12, 'Módulo único.'), (4, 1, 'Análise Léxica', 20, 'Módulo único.'),
    (5, 1, 'Protocolos TCP/IP', 8, 'Módulo único.'), (6, 1, 'Sintaxe do JS', 30, 'Módulo único.'),
    (7, 1, 'Introdução ao Pandas', 25, 'Módulo único.'), (8, 1, 'Controllers REST', 40, 'Módulo único.'),
    (9, 1, 'Ponteiros em C++', 15, 'Módulo único.'), (10, 1, 'Sinais Analógicos', 18, 'Módulo único.'),
    (11, 1, 'Loops em Bash', 5, 'Módulo único.'), (12, 1, 'Entendendo Licenças', 3, 'Módulo único.'),
    (13, 1, 'Injeção de Dependência', 22, 'Módulo único.'), (14, 1, 'Active Record', 16, 'Módulo único.'),
    (15, 1, 'Callbacks e Promises', 25, 'Módulo único.'), (16, 1, 'Diretivas Vue', 14, 'Módulo único.'),
    (17, 1, 'Hooks e Context', 28, 'Módulo único.'), (18, 1, 'Imagens e Volumes', 10, 'Módulo único.'),
    (19, 1, 'EC2 e S3', 35, 'Módulo único.'), (20, 1, 'Azure Virtual Machines', 20, 'Módulo único.'),
    (21, 1, 'BigQuery Essencial', 25, 'Módulo único.'), (22, 1, 'Busca A Estrela (A*)', 12, 'Módulo único.'),
    (23, 1, 'Zero-shot Prompting', 5, 'Módulo único.'), (24, 1, 'Regressão Linear', 30, 'Módulo único.'),
    (25, 1, 'Tensores e Grafos', 40, 'Módulo único.'), (26, 1, 'Filtros de Convolução', 35, 'Módulo único.'),
    (27, 1, 'Arquitetura do Gerador', 45, 'Módulo único.'), (28, 1, 'Variáveis de Estado', 20, 'Módulo único.'),
    (29, 1, 'Criptografia Assimétrica', 15, 'Módulo único.'), (30, 1, 'Criando Carteiras', 18, 'Módulo único.')
ON CONFLICT DO NOTHING;
@@

-- 12. AULA (30 inserções - 1 aula por módulo)
INSERT INTO aula (id_aula, id_curso, id_modulo, titulo, link_do_video, descricao) VALUES
    (1, 1, 1, 'Hello World', 'video/hello_world', 'A sua primeira linha de código.'),
    (2, 2, 1, 'O que é um Array', 'video/array', 'Aula única do curso.'),
    (3, 3, 1, 'Comandos ls, cd e pwd', 'video/comandos_linux', 'Aula única do curso.'),
    (4, 4, 1, 'Tokens e Expressões Regulares', 'video/tokens', 'Aula única do curso.'),
    (5, 5, 1, 'A diferença entre TCP e UDP', 'video/tcp_udp', 'Aula única do curso.'),
    (6, 6, 1, 'Let, Const e Var', 'video/js_variables', 'Aula única do curso.'),
    (7, 7, 1, 'Criando um DataFrame', 'video/pandas_df', 'Aula única do curso.'),
    (8, 8, 1, 'Mapeamento @GetMapping', 'video/spring_get', 'Aula única do curso.'),
    (9, 9, 1, 'Alocação Dinâmica de Memória', 'video/cpp_memory', 'Aula única do curso.'),
    (10, 10, 1, 'Acendendo um LED', 'video/c_led', 'Aula única do curso.'),
    (11, 11, 1, 'Criando um Script Automatizado', 'video/bash_script', 'Aula única do curso.'),
    (12, 12, 1, 'GPL vs MIT', 'video/licenses', 'Aula única do curso.'),
    (13, 13, 1, 'Configurando o Service Collection', 'video/csharp_di', 'Aula única do curso.'),
    (14, 14, 1, 'Migrations no Rails', 'video/rails_migrations', 'Aula única do curso.'),
    (15, 15, 1, 'Entendendo o Event Loop', 'video/node_event_loop', 'Aula única do curso.'),
    (16, 16, 1, 'v-if e v-for', 'video/vue_directives', 'Aula única do curso.'),
    (17, 17, 1, 'Gerenciando Estado com useState', 'video/react_state', 'Aula única do curso.'),
    (18, 18, 1, 'Construindo o primeiro Dockerfile', 'video/dockerfile', 'Aula única do curso.'),
    (19, 19, 1, 'Configurando um Bucket S3', 'video/aws_s3', 'Aula única do curso.'),
    (20, 20, 1, 'Deploy de App Service', 'video/azure_deploy', 'Aula única do curso.'),
    (21, 21, 1, 'Queries Rápidas no BigQuery', 'video/gcp_bq', 'Aula única do curso.'),
    (22, 22, 1, 'Otimizando o caminho com A*', 'video/ai_astar', 'Aula única do curso.'),
    (23, 23, 1, 'Como evitar alucinações no LLM', 'video/prompt_hallucinations', 'Aula única do curso.'),
    (24, 24, 1, 'Minimizando o Erro Quadrático', 'video/ml_regression', 'Aula única do curso.'),
    (25, 25, 1, 'Forward Propagation', 'video/dl_forward', 'Aula única do curso.'),
    (26, 26, 1, 'Detecção de Bordas', 'video/cv_edges', 'Aula única do curso.'),
    (27, 27, 1, 'Treinando as duas redes juntas', 'video/gans_training', 'Aula única do curso.'),
    (28, 28, 1, 'Deploy com Hardhat', 'video/solidity_hardhat', 'Aula única do curso.'),
    (29, 29, 1, 'Chave Pública vs Privada', 'video/crypto_keys', 'Aula única do curso.'),
    (30, 30, 1, 'Interagindo com MetaMask', 'video/web3_metamask', 'Aula única do curso.')
ON CONFLICT DO NOTHING;
@@

-- 13. MATERIAL (30 inserções - 1 material por aula)
INSERT INTO material (id_material, id_aula, id_curso, id_modulo, link_material, nome) VALUES
    (1, 1, 1, 1, 'link/pdf1', 'Slides Introdução'), (2, 2, 2, 1, 'link/pdf2', 'Lista de Exercícios'),
    (3, 3, 3, 1, 'link/pdf3', 'Cheat Sheet Linux'), (4, 4, 4, 1, 'link/pdf4', 'Tabela de Tokens'),
    (5, 5, 5, 1, 'link/pdf5', 'Diagrama OSI'), (6, 6, 6, 1, 'link/pdf6', 'Resumo Variáveis JS'),
    (7, 7, 7, 1, 'link/pdf7', 'Jupyter Notebook'), (8, 8, 8, 1, 'link/pdf8', 'Código Fonte Spring'),
    (9, 9, 9, 1, 'link/pdf9', 'Exemplos de Ponteiros'), (10, 10, 10, 1, 'link/pdf10', 'Esquema do Circuito'),
    (11, 11, 11, 1, 'link/pdf11', 'Script Final Bash'), (12, 12, 12, 1, 'link/pdf12', 'Matriz de Licenças'),
    (13, 13, 13, 1, 'link/pdf13', 'Diagrama de Classes'), (14, 14, 14, 1, 'link/pdf14', 'Comandos Rails'),
    (15, 15, 15, 1, 'link/pdf15', 'Fluxograma Event Loop'), (16, 16, 16, 1, 'link/pdf16', 'Template Vue'),
    (17, 17, 17, 1, 'link/pdf17', 'Componente React'), (18, 18, 18, 1, 'link/pdf18', 'Dockerfile de Exemplo'),
    (19, 19, 19, 1, 'link/pdf19', 'Apostila S3'), (20, 20, 20, 1, 'link/pdf20', 'Guia Azure CLI'),
    (21, 21, 21, 1, 'link/pdf21', 'Query SQL de Exemplo'), (22, 22, 22, 1, 'link/pdf22', 'Pseudocódigo A*'),
    (23, 23, 23, 1, 'link/pdf23', 'Templates de Prompt'), (24, 24, 24, 1, 'link/pdf24', 'Dataset CSV'),
    (25, 25, 25, 1, 'link/pdf25', 'Notebook PyTorch'), (26, 26, 26, 1, 'link/pdf26', 'Imagem de Teste'),
    (27, 27, 27, 1, 'link/pdf27', 'Modelo Pré-Treinado'), (28, 28, 28, 1, 'link/pdf28', 'Código Solidity'),
    (29, 29, 29, 1, 'link/pdf29', 'Explicação SHA-256'), (30, 30, 30, 1, 'link/pdf30', 'ABI Exemplo')
ON CONFLICT DO NOTHING;
@@

-- 14. ASSISTIR (30 inserções - Cada aluno assiste à aula do curso que comprou)
INSERT INTO assistir (id_aula, id_curso, id_modulo, cpf_aluno, data_assistida) VALUES
    (1, 1, 1, '20000000001', DATE '2026-05-15'), (2, 2, 1, '20000000002', DATE '2026-05-15'),
    (3, 3, 1, '20000000003', DATE '2026-05-15'), (4, 4, 1, '20000000004', DATE '2026-05-15'),
    (5, 5, 1, '20000000005', DATE '2026-05-15'), (6, 6, 1, '20000000006', DATE '2026-05-15'),
    (7, 7, 1, '20000000007', DATE '2026-05-15'), (8, 8, 1, '20000000008', DATE '2026-05-15'),
    (9, 9, 1, '20000000009', DATE '2026-05-15'), (10, 10, 1, '20000000010', DATE '2026-05-15'),
    (11, 11, 1, '20000000011', DATE '2026-05-15'), (12, 12, 1, '20000000012', DATE '2026-05-15'),
    (13, 13, 1, '20000000013', DATE '2026-05-15'), (14, 14, 1, '20000000014', DATE '2026-05-15'),
    (15, 15, 1, '20000000015', DATE '2026-05-15'), (16, 16, 1, '20000000016', DATE '2026-05-15'),
    (17, 17, 1, '20000000017', DATE '2026-05-15'), (18, 18, 1, '20000000018', DATE '2026-05-15'),
    (19, 19, 1, '20000000019', DATE '2026-05-15'), (20, 20, 1, '20000000020', DATE '2026-05-15'),
    (21, 21, 1, '20000000021', DATE '2026-05-15'), (22, 22, 1, '20000000022', DATE '2026-05-15'),
    (23, 23, 1, '20000000023', DATE '2026-05-15'), (24, 24, 1, '20000000024', DATE '2026-05-15'),
    (25, 25, 1, '20000000025', DATE '2026-05-15'), (26, 26, 1, '20000000026', DATE '2026-05-15'),
    (27, 27, 1, '20000000027', DATE '2026-05-15'), (28, 28, 1, '20000000028', DATE '2026-05-15'),
    (29, 29, 1, '20000000029', DATE '2026-05-15'), (30, 30, 1, '20000000030', DATE '2026-05-15')
ON CONFLICT DO NOTHING;
@@

-- 15. CERTIFICADO CURSO (30 inserções - Como cada curso tem só 1 aula, o aluno ganha o certificado após assistir)
INSERT INTO certificado_curso (id_curso_concluido, cpf_aluno_graduado, data_certificado) VALUES
    (1, '20000000001', DATE '2026-05-16'), (2, '20000000002', DATE '2026-05-16'),
    (3, '20000000003', DATE '2026-05-16'), (4, '20000000004', DATE '2026-05-16'),
    (5, '20000000005', DATE '2026-05-16'), (6, '20000000006', DATE '2026-05-16'),
    (7, '20000000007', DATE '2026-05-16'), (8, '20000000008', DATE '2026-05-16'),
    (9, '20000000009', DATE '2026-05-16'), (10, '20000000010', DATE '2026-05-16'),
    (11, '20000000011', DATE '2026-05-16'), (12, '20000000012', DATE '2026-05-16'),
    (13, '20000000013', DATE '2026-05-16'), (14, '20000000014', DATE '2026-05-16'),
    (15, '20000000015', DATE '2026-05-16'), (16, '20000000016', DATE '2026-05-16'),
    (17, '20000000017', DATE '2026-05-16'), (18, '20000000018', DATE '2026-05-16'),
    (19, '20000000019', DATE '2026-05-16'), (20, '20000000020', DATE '2026-05-16'),
    (21, '20000000021', DATE '2026-05-16'), (22, '20000000022', DATE '2026-05-16'),
    (23, '20000000023', DATE '2026-05-16'), (24, '20000000024', DATE '2026-05-16'),
    (25, '20000000025', DATE '2026-05-16'), (26, '20000000026', DATE '2026-05-16'),
    (27, '20000000027', DATE '2026-05-16'), (28, '20000000028', DATE '2026-05-16'),
    (29, '20000000029', DATE '2026-05-16'), (30, '20000000030', DATE '2026-05-16')
ON CONFLICT DO NOTHING;
@@

-- 16. COMENTÁRIO (30 inserções - Cada aluno deixa um feedback na aula assistida)
INSERT INTO comentario (id_aula, id_curso, cpf_aluno, cpf_professor, data_criacao, conteudo, comentario_pai) VALUES
    (1, 1, '20000000001', NULL, DATE '2026-05-17', 'Excelente didática, professor Alan!', NULL),
    (2, 2, '20000000002', NULL, DATE '2026-05-17', 'Prof Ada, amei as explicações sobre Arrays.', NULL),
    (3, 3, '20000000003', NULL, DATE '2026-05-17', 'Com certeza o Linux não é bicho de 7 cabeças.', NULL),
    (4, 4, '20000000004', NULL, DATE '2026-05-17', 'Muito boa a aula de Regex!', NULL),
    (5, 5, '20000000005', NULL, DATE '2026-05-17', 'O TCP é bem mais seguro que eu imaginava.', NULL),
    (6, 6, '20000000006', NULL, DATE '2026-05-17', 'Graças a essa aula não erro mais Let e Const.', NULL),
    (7, 7, '20000000007', NULL, DATE '2026-05-17', 'Pandas facilita tudo!', NULL),
    (8, 8, '20000000008', NULL, DATE '2026-05-17', 'O Spring resolve muitas configurações de forma mágica.', NULL),
    (9, 9, '20000000009', NULL, DATE '2026-05-17', 'Os ponteiros em C++ agora fazem sentido.', NULL),
    (10, 10, '20000000010', NULL, DATE '2026-05-17', 'Ver o LED acender na protoboard foi incrível.', NULL),
    (11, 11, '20000000011', NULL, DATE '2026-05-17', 'Automatizei meu backup diário com essa aula.', NULL),
    (12, 12, '20000000012', NULL, DATE '2026-05-17', 'GPL mudou minha forma de ver o software.', NULL),
    (13, 13, '20000000013', NULL, DATE '2026-05-17', 'Injeção de dependência deixou meu código limpo.', NULL),
    (14, 14, '20000000014', NULL, DATE '2026-05-17', 'As migrations poupam um tempão.', NULL),
    (15, 15, '20000000015', NULL, DATE '2026-05-17', 'O Event Loop finalmente entrou na minha cabeça.', NULL),
    (16, 16, '20000000016', NULL, DATE '2026-05-17', 'Adoro a sintaxe do Vue!', NULL),
    (17, 17, '20000000017', NULL, DATE '2026-05-17', 'React mudou a forma que construo interfaces.', NULL),
    (18, 18, '20000000018', NULL, DATE '2026-05-17', 'Adeus: "Na minha máquina funciona".', NULL),
    (19, 19, '20000000019', NULL, DATE '2026-05-17', 'S3 é muito barato de usar, adorei.', NULL),
    (20, 20, '20000000020', NULL, DATE '2026-05-17', 'A interface do Azure é muito rica.', NULL),
    (21, 21, '20000000021', NULL, DATE '2026-05-17', 'As queries no BigQuery voam de verdade.', NULL),
    (22, 22, '20000000022', NULL, DATE '2026-05-17', 'Achei o caminho mais curto na primeira tentativa.', NULL),
    (23, 23, '20000000023', NULL, DATE '2026-05-17', 'Engenharia de Prompt é a profissão do futuro!', NULL),
    (24, 24, '20000000024', NULL, DATE '2026-05-17', 'O gráfico de regressão ficou perfeito.', NULL),
    (25, 25, '20000000025', NULL, DATE '2026-05-17', 'GPU chorou mas rodou a rede neural.', NULL),
    (26, 26, '20000000026', NULL, DATE '2026-05-17', 'Consegui detectar o rosto do meu gato.', NULL),
    (27, 27, '20000000027', NULL, DATE '2026-05-17', 'A imagem gerada parece de verdade.', NULL),
    (28, 28, '20000000028', NULL, DATE '2026-05-17', 'Primeiro smart contract deployado!', NULL),
    (29, 29, '20000000029', NULL, DATE '2026-05-17', 'O SHA-256 é impressionante.', NULL),
    (30, 30, '20000000030', NULL, DATE '2026-05-17', 'Conectei minha carteira de primeira.', NULL)
ON CONFLICT DO NOTHING;
@@

COMMIT;
@@