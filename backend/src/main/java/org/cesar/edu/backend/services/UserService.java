package org.cesar.edu.backend.services;

import org.cesar.edu.backend.dtos.requests.UserCreateRequest;
import org.cesar.edu.backend.dtos.requests.UserLoginRequest;
import org.cesar.edu.backend.dtos.responses.CompraResponse;
import org.cesar.edu.backend.dtos.responses.CursoResponse;
import org.cesar.edu.backend.dtos.responses.UserResponse;
import org.cesar.edu.backend.models.*;
import org.cesar.edu.backend.repositories.*;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.cesar.edu.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final TelefoneRepository telefoneRepository;
    private final CertificacoesRepository certificacoesRepository;
    private final CursoRepository cursoRepository;
    private final CompraService compraService;
    private final CursoService cursoService;
    private final LecionaRepository lecionaRepository;

    @Autowired
    public UserService(UserRepository userRepository, AlunoRepository alunoRepository, ProfessorRepository professorRepository,
                        TelefoneRepository telefoneRepository, CertificacoesRepository certificacoesRepository,  CursoRepository cursoRepository,
                            CompraService compraService, CursoService cursoService, LecionaRepository lecionaRepository) {
        this.userRepository = userRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.telefoneRepository = telefoneRepository;
        this.certificacoesRepository = certificacoesRepository;
        this.cursoRepository = cursoRepository;
        this.compraService = compraService;
        this.cursoService = cursoService;
        this.lecionaRepository = lecionaRepository;
    }
    public UserResponse buscarUsuarioGenericoPorCpf(String cpf) {
        UserResponse alunoResponse = pegarPorCpfAluno(cpf);
        if (alunoResponse != null) {
            return alunoResponse;
        }

        UserResponse professorResponse = pegarPorCpfProfessor(cpf);
        if (professorResponse != null) {
            return professorResponse;
        }

        return null;
    }
    @Transactional(readOnly = true)
    public UserResponse realizarLogin(UserLoginRequest dto) {
        User user = userRepository.findByEmail(dto.email());

        if (user == null || !dto.senha().equals(user.getSenha())) {
            return null;
        }
        Professor professor = professorRepository.findByCpf(user.getCpf());
        if (professor != null) {
            return UserResponse.fromProfessor(professor,null);
        }

        Aluno aluno = alunoRepository.findByCpf(user.getCpf());
        if (aluno != null) {
            return UserResponse.fromAluno(aluno,null);
        }

        return null;
    }
    @Transactional
    public ResultService criarProfessor(UserCreateRequest dto){
        ListaString erros = new ListaString();
        boolean realizado = false;
        boolean validado = true;

        Professor professor = UserCreateRequest.toEntityProfessor(dto);
        if(!(professor instanceof User)){
            erros.adicionar("Houve um erro inesperado!");
            return new ResultService(false,false,erros);
        }
        ResultService resultProfessorUsuario = validarUser((User) professor, null);
        if(!resultProfessorUsuario.isValid()){
            validado = false;
            return resultProfessorUsuario;
        }
        if(professor.getCertificados() != null){
            for (CertificadoProfessor certificado : professor.getCertificados()) {
                if (certificado == null) {
                    validado = false;
                    erros.adicionar("Certificado nulo na lista!");
                    continue;
                }

                if (StringUtils.estaVazia(certificado.getCpf_professor())) {
                    validado = false;
                    erros.adicionar("CPF do certificado vazio!");
                } else if (!StringUtils.cpfValido(certificado.getCpf_professor())) {
                    validado = false;
                    erros.adicionar("CPF do certificado inválido!");
                }

                if (StringUtils.estaVazia(certificado.getTitulo_certificado())) {
                    validado = false;
                    erros.adicionar("Título do certificado vazio!");
                } else if (certificado.getTitulo_certificado().length() < 3 ||
                        certificado.getTitulo_certificado().length() > 150) {
                    validado = false;
                    erros.adicionar("Título do certificado muito curto ou muito longo!");
                }
            }
            if(!validado){
                return new ResultService(validado,false,erros);
            }
        }
        else{
            erros.adicionar("Certificado nulo!");
        }
        if(userRepository.findByCpf(professor.getCpf())!=null){
            validado = false;
            erros.adicionar("Usuário já cadastrado");
            return new ResultService(validado,false,erros);
        }
        try {
            boolean sucessoBase = professorRepository.save(professor);

            if (!sucessoBase) {
                erros.adicionar("Falha ao salvar dados base do professor (Usuario/Professor)");
                return new ResultService(true, false, erros);
            }
            if (professor.getTelefones() != null) {
                for (Telefone tel : professor.getTelefones()) {
                    tel.setCpf_usuario(professor.getCpf());
                    telefoneRepository.save(tel);
                }
            }

            if (professor.getCertificados() != null) {
                for (CertificadoProfessor cert : professor.getCertificados()) {
                    cert.setCpf_professor(professor.getCpf());
                    certificacoesRepository.save(cert);
                }
            }

            return new ResultService(true, true, new ListaString());

        } catch (DataAccessException e) {
            erros.adicionar("Erro de integridade: " + e.getMostSpecificCause().getMessage());
            return new ResultService(true, false, erros);
        }
    }
    @Transactional
    public ResultService atualizarProfessor(UserCreateRequest dto, String cpfAntigo) {
        ListaString erros = new ListaString();

        Professor professorNovo = UserCreateRequest.toEntityProfessor(dto);

        ResultService resultValidacao = validarUser((User) professorNovo, cpfAntigo);
        if (!resultValidacao.isValid()) {
            return resultValidacao;
        }

        if (!professorNovo.getCpf().equals(cpfAntigo)) {
            if (userRepository.findByCpf(professorNovo.getCpf()) != null) {
                erros.adicionar("O novo CPF informado já pertence a outro usuário.");
                return new ResultService(true, false, erros);
            }
        }

        try {
            boolean sucessoBase = professorRepository.update(professorNovo, cpfAntigo);

            if (!sucessoBase) {
                erros.adicionar("Não foi possível encontrar o professor com o CPF antigo informado.");
                return new ResultService(true, false, erros);
            }
            telefoneRepository.delete(professorNovo.getCpf());
            if (professorNovo.getTelefones() != null) {
                for (Telefone tel : professorNovo.getTelefones()) {
                    tel.setCpf_usuario(professorNovo.getCpf());
                    telefoneRepository.save(tel);
                }
            }

            certificacoesRepository.deleteByCpf(professorNovo.getCpf());
            if (professorNovo.getCertificados() != null) {
                for (CertificadoProfessor cert : professorNovo.getCertificados()) {
                    cert.setCpf_professor(professorNovo.getCpf());
                    certificacoesRepository.save(cert);
                }
            }

            return new ResultService(true, true, new ListaString());

        } catch (DataAccessException e) {
            erros.adicionar("Erro técnico ao atualizar: " + e.getMostSpecificCause().getMessage());
            return new ResultService(true, false, erros);
        }
    }
    @Transactional
    public ResultService deletarProfessor(String cpf) {
        ListaString erros = new ListaString();

        User usuario = userRepository.findByCpf(cpf);
        if (usuario == null) {
            erros.adicionar("Professor não encontrado para exclusão.");
            return new ResultService(true, false, erros);
        }

        try {
            boolean sucesso = userRepository.delete(cpf);

            if (sucesso) {
                return new ResultService(true, true, new ListaString());
            } else {
                erros.adicionar("Não foi possível completar a exclusão do professor.");
                return new ResultService(true, false, erros);
            }

        } catch (DataAccessException e) {
            erros.adicionar("Erro de banco de dados ao deletar: " + e.getMostSpecificCause().getMessage());
            return new ResultService(true, false, erros);
        }
    }
    public List<UserResponse> listarProfessores() {
        List<Professor> listaSimples = professorRepository.findAll();

        if (listaSimples == null || listaSimples.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        List<UserResponse> listaCompleta = new java.util.ArrayList<>();

        for (Professor p : listaSimples) {
            UserResponse professorCompleto = pegarPorCpfProfessor(p.getCpf());

            if (professorCompleto != null) {
                listaCompleta.add(professorCompleto);
            }
        }

        return listaCompleta;
    }
    public UserResponse pegarPorCpfProfessor(String cpf) {
        try {
            User userBase = userRepository.findByCpf(cpf);
            if (userBase == null) return null;

            Professor professor = professorRepository.findByCpf(cpf);
            if (professor == null) return null;

            professor.setNome(userBase.getNome());
            professor.setEmail(userBase.getEmail());
            professor.setSenha(userBase.getSenha());
            professor.setLogradouro(userBase.getLogradouro());
            professor.setNumero(userBase.getNumero());
            professor.setCep(userBase.getCep());

            List<Telefone> telefones = telefoneRepository.findByCpf(cpf);
            professor.setTelefones(telefones);

            List<CertificadoProfessor> certificados = certificacoesRepository.findByCpf(cpf);
            professor.setCertificados(certificados);

            List<Leciona> lecionas = lecionaRepository.findByCpf(cpf);

            List<CursoResponse> cursos = lecionas.stream()
                    .map(leciona -> cursoService.findResponseById(leciona.getId_curso()))
                    .toList();

            return UserResponse.fromProfessor(professor, cursos);

        } catch (Exception e) {
            System.err.println("🚨 ATENÇÃO: ERRO AO BUSCAR PROFESSOR NO BANCO! 🚨");
            System.err.println("CPF Buscado: " + cpf);
            e.printStackTrace(); // ISSO AQUI VAI CUSPIR O ERRO REAL NO CONSOLE DO INTELLIJ
            return null;
        }
    }

    @Transactional
    public ResultService criarAluno(UserCreateRequest dto){
        ListaString erros = new ListaString();
        boolean realizado = false;
        boolean validado = true;

        Aluno aluno = UserCreateRequest.toEntityAluno(dto);
        if(!(aluno instanceof User)){
            erros.adicionar("Houve um erro inesperado!");
            return new ResultService(false,false,erros);
        }
        ResultService resultAlunoUsuario = validarUser((User) aluno, null);
        if(!resultAlunoUsuario.isValid()){
            validado = false;
            return resultAlunoUsuario;
        }
        if(userRepository.findByCpf(aluno.getCpf())!=null){
            validado = false;
            erros.adicionar("Usuário já cadastrado");
            return new ResultService(validado,false,erros);
        }
        try {
            boolean sucessoBase = alunoRepository.save(aluno);

            if (!sucessoBase) {
                erros.adicionar("Falha ao salvar dados base do aluno (Usuario/aluno)");
                return new ResultService(true, false, erros);
            }
            if (aluno.getTelefones() != null) {
                for (Telefone tel : aluno.getTelefones()) {
                    tel.setCpf_usuario(aluno.getCpf());
                    telefoneRepository.save(tel);
                }
            }

            return new ResultService(true, true, new ListaString());

        } catch (DataAccessException e) {
            erros.adicionar("Erro de integridade: " + e.getMostSpecificCause().getMessage());
            return new ResultService(true, false, erros);
        }
    }
    @Transactional
    public ResultService atualizarAluno(UserCreateRequest dto, String cpfAntigo) {
        ListaString erros = new ListaString();

        Aluno alunoNovo = UserCreateRequest.toEntityAluno(dto);

        ResultService resultValidacao = validarUser((User) alunoNovo, cpfAntigo);
        if (!resultValidacao.isValid()) {
            return resultValidacao;
        }

        if (!alunoNovo.getCpf().equals(cpfAntigo)) {
            if (userRepository.findByCpf(alunoNovo.getCpf()) != null) {
                erros.adicionar("O novo CPF já está em uso.");
                return new ResultService(true, false, erros);
            }
        }

        try {
            boolean sucessoBase = alunoRepository.update(alunoNovo, cpfAntigo);

            if (!sucessoBase) {
                erros.adicionar("Aluno não encontrado para atualização.");
                return new ResultService(true, false, erros);
            }

            telefoneRepository.delete(alunoNovo.getCpf());

            if (alunoNovo.getTelefones() != null) {
                for (Telefone tel : alunoNovo.getTelefones()) {
                    tel.setCpf_usuario(alunoNovo.getCpf());
                    telefoneRepository.save(tel);
                }
            }

            return new ResultService(true, true, new ListaString());

        } catch (DataAccessException e) {
            erros.adicionar("Erro no banco ao atualizar aluno: " + e.getMostSpecificCause().getMessage());
            return new ResultService(true, false, erros);
        }
    }
    @Transactional
    public ResultService deletarAluno(String cpf) {
        ListaString erros = new ListaString();

        User usuario = userRepository.findByCpf(cpf);

        if (usuario == null) {
            erros.adicionar("Aluno não encontrado para exclusão.");
            return new ResultService(true, false, erros);
        }

        try {
            boolean sucesso = userRepository.delete(cpf);

            if (sucesso) {
                return new ResultService(true, true, new ListaString());
            } else {
                erros.adicionar("Erro inesperado ao tentar remover o registro do aluno.");
                return new ResultService(true, false, erros);
            }

        } catch (DataAccessException e) {
            erros.adicionar("Erro de banco de dados: " + e.getMostSpecificCause().getMessage());
            return new ResultService(true, false, erros);
        }
    }
    public UserResponse pegarPorCpfAluno(String cpf) {
        try {
            User userBase = userRepository.findByCpf(cpf);
            if (userBase == null) return null;

            Aluno aluno = alunoRepository.findByCpf(cpf);
            if (aluno == null) return null;

            aluno.setNome(userBase.getNome());
            aluno.setEmail(userBase.getEmail());
            aluno.setSenha(userBase.getSenha());
            aluno.setLogradouro(userBase.getLogradouro());
            aluno.setNumero(userBase.getNumero());
            aluno.setCep(userBase.getCep());

            List<Telefone> telefones = telefoneRepository.findByCpf(cpf);
            aluno.setTelefones(telefones);

            List<CompraResponse> compras = compraService.findResponsesByAluno(cpf);

            return UserResponse.fromAluno(aluno, compras);

        } catch (DataAccessException e) {
            return null;
        }
    }
    public List<UserResponse> listarAlunos() {
        List<Aluno> listaSimples = alunoRepository.findAll();

        if (listaSimples == null || listaSimples.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        List<UserResponse> listaCompleta = new java.util.ArrayList<>();

        for (Aluno a : listaSimples) {
            UserResponse alunoCompleto = pegarPorCpfAluno(a.getCpf());

            if (alunoCompleto != null) {
                listaCompleta.add(alunoCompleto);
            }
        }

        return listaCompleta;
    }


    private ResultService validarUser(User usuario, String cpfAntigo) {
        ListaString erros = new ListaString();
        boolean validado = true;

        if(usuario == null){
            validado = false;
            erros.adicionar("Usuário nulo!");
            return new ResultService(validado,false, erros);
        }
        if(usuario.getCpf() == null || StringUtils.estaVazia(usuario.getCpf())){
            validado = false;
            erros.adicionar("CPF vazio ou inexistente!");
            return new ResultService(validado,false, erros);
        }
        if(usuario.getCpf().replaceAll("\\D","").length() != 11){
            validado = false;
            erros.adicionar("cpf invalido!");
        }
        if(!StringUtils.cpfValido(usuario.getCpf())){
            validado = false;
            erros.adicionar("CPF invalido! Não possui digitos verificadores condizentes");
        }
        if (StringUtils.estaVazia(usuario.getNome())){
            validado = false;
            erros.adicionar("Nome vazio ou inexistente!");
            return new ResultService(validado,false, erros);
        }
        if(usuario.getNome().length() > 250 || usuario.getNome().length() < 3){
            validado = false;
            erros.adicionar("Nome muito longo ou muito curto");
        }
        if(StringUtils.contemCaracterEspecial(usuario.getNome())){
            validado = false;
            erros.adicionar("Nome não pode conter caracteres especiais!");
        }
        if (StringUtils.estaVazia(usuario.getEmail())){
            validado = false;
            erros.adicionar("Email vazio ou inexistente!");
            return new ResultService(validado,false, erros);
        }
        if(usuario.getEmail().length() > 250 || usuario.getEmail().length() < 3){
            validado = false;
            erros.adicionar("Email longo ou muito curto");
        }
        if(!usuario.getEmail().contains("@") || !usuario.getEmail().contains(".")){
            validado = false;
            erros.adicionar("Formato do email invalido!");
        }
        User existente = userRepository.findByEmail(usuario.getEmail());
        if (existente != null) {
            if (cpfAntigo == null || !existente.getCpf().equals(cpfAntigo)) {
                validado = false;
                erros.adicionar("Email já cadastrado em outra conta!");
            }
        }
        if(StringUtils.estaVazia(usuario.getSenha())){
            validado = false;
            erros.adicionar("Senha Vazia ou inexistente!");
            return new ResultService(validado,false, erros);
        }
        if(usuario.getSenha().length() < 8){
            validado = false;
            erros.adicionar("Senha muito curta");
        }
        if(!StringUtils.contemCaracterEspecial(usuario.getSenha())){
            validado = false;
            erros.adicionar("Senha deve conter pelo menos 1 caracter especial!");
        }
        if(!StringUtils.contemSenhaValidaNumerosLetras(usuario.getSenha())){
            validado = false;
            erros.adicionar("A senha deve conter numeros e letras!");
        }

        if(StringUtils.estaVazia(usuario.getLogradouro())){
            erros.adicionar("Logradouro vazio ou inexistente!");
        }
        else if(usuario.getLogradouro().length() < 8 || usuario.getLogradouro().length() > 120){
            validado = false;
            erros.adicionar("Logradouro muito curto ou muito longo");
        }

        if(usuario.getNumero() == null){
            erros.adicionar("Numero inexistente!");
            return new ResultService(validado,false, erros);
        }
        else if(usuario.getNumero() <= 0){
            validado = false;
            erros.adicionar("Numero fora do intervalo!");
        }

        if(usuario.getCep() == null){
            erros.adicionar("Cep nulo!");
        }
        else if(usuario.getCep().replaceAll("\\D","").length() != 8){
            validado = false;
            erros.adicionar("Cep fora do formato!");
        }

        if(usuario.getTelefones() == null){
            erros.adicionar("Telefones nulo!");
        }
        else{
            for (Telefone telefone : usuario.getTelefones()) {
                if (telefone == null) {
                    validado = false;
                    erros.adicionar("Tem um telefone nulo na lista!");
                    continue;
                }

                if (StringUtils.estaVazia(telefone.getNumero())) {
                    validado = false;
                    erros.adicionar("Telefone com número vazio!");
                    continue;
                }

                if (!StringUtils.telefoneValido(telefone.getNumero())) {
                    validado = false;
                    erros.adicionar("Telefone inválido: " + telefone.getNumero());
                }

                if (StringUtils.estaVazia(telefone.getCpf_usuario())) {
                    validado = false;
                    erros.adicionar("Telefone sem CPF do usuário!");
                }
            }
        }
        return new ResultService(validado,false, erros);
    }

    //consulta 4: pega o cpf e o curso que o aluno não assistiu nenhuma aula
    public List<ConsultaPegarAlunoComAulasNaoAssistidas> pegarAlunoComAulasNaoAssistidas() {
        List<ConsultaPegarAlunoComAulasNaoAssistidas> resultados =
                alunoRepository.pegarAlunoComAulasNaoAssistidas();

        if (resultados == null || resultados.isEmpty()) {
            return List.of();
        }

        for (ConsultaPegarAlunoComAulasNaoAssistidas item : resultados) {

            if (item == null) {
                throw new IllegalStateException("Foi encontrado um registro inválido na consulta.");
            }

            if (item.getCpf() == null || item.getCpf().isBlank()) {
                throw new IllegalStateException("Foi encontrado um aluno sem CPF.");
            }

            if (item.getCpf().length() != 11) {
                throw new IllegalStateException(
                        "Foi encontrado um CPF inválido na consulta: " + item.getCpf()
                );
            }

            if (item.getNome_curso() == null || item.getNome_curso().isBlank()) {
                throw new IllegalStateException(
                        "Foi encontrado um curso sem nome para o aluno " + item.getCpf() + "."
                );
            }
        }

        return resultados;
    }

    //procedure 2: atualiza diariamente a tabela de alunos inativos
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Recife")
    public void atualizarAlunosInativos() {
        alunoRepository.atualizarAlunosInativos();
    }

    // procedure 2: vai listar os alunos inativos
    public List<AlunoInativo> listarAlunosInativos() {
        List<AlunoInativo> alunos = alunoRepository.alunosInativos();

        if (alunos == null || alunos.isEmpty()) {
            return List.of();
        }

        for (AlunoInativo aluno : alunos) {

            if (aluno == null) {
                throw new IllegalStateException("Foi encontrado um aluno inativo inválido.");
            }

            if (aluno.getCpfAluno() == null || aluno.getCpfAluno().isBlank()) {
                throw new IllegalStateException("Foi encontrado um aluno inativo sem CPF.");
            }

            if (aluno.getCpfAluno().length() != 11) {
                throw new IllegalStateException(
                        "Foi encontrado um CPF inválido em alunos inativos: " + aluno.getCpfAluno()
                );
            }

            if (aluno.getNomeAluno() == null || aluno.getNomeAluno().isBlank()) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno inativo sem nome."
                );
            }

            if (aluno.getIdCurso() == null || aluno.getIdCurso() <= 0) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno inativo com ID de curso inválido."
                );
            }

            if (aluno.getNomeCurso() == null || aluno.getNomeCurso().isBlank()) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno inativo sem nome do curso."
                );
            }

            if (aluno.getDataCompra() == null) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno inativo sem data de compra."
                );
            }

            if (aluno.getDataReferenciaInatividade() == null) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno inativo sem data de referência de inatividade."
                );
            }

            if (aluno.getDiasInativo() == null || aluno.getDiasInativo() <= 30) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno com dias de inatividade inválido."
                );
            }

            if (aluno.getMotivo() == null || aluno.getMotivo().isBlank()) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno inativo sem motivo."
                );
            }

            if (!aluno.getMotivo().equals("NUNCA_ASSISTIU")
                    && !aluno.getMotivo().equals("SEM_ACESSO_RECENTE")) {
                throw new IllegalStateException(
                        "Foi encontrado um motivo de inatividade inválido: " + aluno.getMotivo()
                );
            }

            if (aluno.getDataAtualizacao() == null) {
                throw new IllegalStateException(
                        "Foi encontrado um aluno inativo sem data de atualização."
                );
            }

            LocalDate dataReferenciaEsperada = aluno.getUltimaAulaAssistida() != null
                    ? aluno.getUltimaAulaAssistida()
                    : aluno.getDataCompra();

            if (!aluno.getDataReferenciaInatividade().equals(dataReferenciaEsperada)) {
                throw new IllegalStateException(
                        "A data de referência de inatividade está inconsistente para o aluno "
                                + aluno.getCpfAluno()
                                + " no curso "
                                + aluno.getIdCurso()
                                + "."
                );
            }
        }

        return alunos;
    }

    //view 1: mostra o progresso geral dos alunos nos seus respectivos cursos comprados
    public List<ViewProgressoAlunoCurso> pegarTodosProgressosAlunosCurso() {
        List<ViewProgressoAlunoCurso> progressos =
                alunoRepository.pegarTodosProgressosAlunosCurso();

        if (progressos == null || progressos.isEmpty()) {
            return List.of();
        }

        for (ViewProgressoAlunoCurso progresso : progressos) {

            if (progresso == null) {
                throw new IllegalStateException("Foi encontrado um progresso inválido na view.");
            }

            if (progresso.getCpfAluno() == null || progresso.getCpfAluno().isBlank()) {
                throw new IllegalStateException("Foi encontrado um progresso sem CPF do aluno.");
            }

            if (progresso.getCpfAluno().length() != 11) {
                throw new IllegalStateException(
                        "Foi encontrado um CPF inválido na view: " + progresso.getCpfAluno()
                );
            }

            if (progresso.getIdCurso() == null || progresso.getIdCurso() <= 0) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso com ID de curso inválido."
                );
            }

            if (progresso.getNomeCurso() == null || progresso.getNomeCurso().isBlank()) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso sem nome do curso."
                );
            }

            if (progresso.getTotalAulas() == null || progresso.getTotalAulas() <= 0) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso com total de aulas inválido."
                );
            }

            if (progresso.getAulasAssistidas() == null || progresso.getAulasAssistidas() < 0) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso com quantidade de aulas assistidas inválida."
                );
            }

            if (progresso.getAulasAssistidas() > progresso.getTotalAulas()) {
                throw new IllegalStateException(
                        "A quantidade de aulas assistidas não pode ser maior que o total de aulas."
                );
            }

            if (progresso.getPercentualConclusao() == null) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso sem percentual de conclusão."
                );
            }

            if (progresso.getPercentualConclusao().compareTo(BigDecimal.ZERO) < 0 ||
                    progresso.getPercentualConclusao().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalStateException(
                        "Foi encontrado um percentual de conclusão inválido."
                );
            }
        }

        return progressos;
    }

    //~filtra por aluno:
    public List<ViewProgressoAlunoCurso> pegarProgressoAlunoCurso(String cpfAluno) {
        if (cpfAluno == null || cpfAluno.isBlank()) {
            throw new IllegalArgumentException("O CPF do aluno deve ser informado.");
        }

        cpfAluno = cpfAluno.trim();

        if (cpfAluno.length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 caracteres.");
        }

        List<ViewProgressoAlunoCurso> progressos =
                alunoRepository.pegarProgressoAlunoCurso(cpfAluno);

        if (progressos == null || progressos.isEmpty()) {
            return List.of();
        }

        for (ViewProgressoAlunoCurso progresso : progressos) {

            if (progresso == null) {
                throw new IllegalStateException("Foi encontrado um progresso inválido na view.");
            }

            if (progresso.getCpfAluno() == null || progresso.getCpfAluno().isBlank()) {
                throw new IllegalStateException("Foi encontrado um progresso sem CPF do aluno.");
            }

            if (!progresso.getCpfAluno().equals(cpfAluno)) {
                throw new IllegalStateException(
                        "A view retornou um progresso de um CPF diferente do solicitado."
                );
            }

            if (progresso.getIdCurso() == null || progresso.getIdCurso() <= 0) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso com ID de curso inválido."
                );
            }

            if (progresso.getNomeCurso() == null || progresso.getNomeCurso().isBlank()) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso sem nome do curso."
                );
            }

            if (progresso.getTotalAulas() == null || progresso.getTotalAulas() <= 0) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso com total de aulas inválido."
                );
            }

            if (progresso.getAulasAssistidas() == null || progresso.getAulasAssistidas() < 0) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso com quantidade de aulas assistidas inválida."
                );
            }

            if (progresso.getAulasAssistidas() > progresso.getTotalAulas()) {
                throw new IllegalStateException(
                        "A quantidade de aulas assistidas não pode ser maior que o total de aulas."
                );
            }

            if (progresso.getPercentualConclusao() == null) {
                throw new IllegalStateException(
                        "Foi encontrado um progresso sem percentual de conclusão."
                );
            }

            if (progresso.getPercentualConclusao().compareTo(BigDecimal.ZERO) < 0 ||
                    progresso.getPercentualConclusao().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalStateException(
                        "Foi encontrado um percentual de conclusão inválido."
                );
            }
        }

        return progressos;
    }
}
