package org.cesar.edu.backend.services;

import org.cesar.edu.backend.dtos.requests.UserCreateRequest;
import org.cesar.edu.backend.dtos.requests.UserLoginRequest;
import org.cesar.edu.backend.models.*;
import org.cesar.edu.backend.repositories.*;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.cesar.edu.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final TelefoneRepository telefoneRepository;
    private final CertificacoesRepository certificacoesRepository;
    private final CursoRepository cursoRepository;

    @Autowired
    private UserService(UserRepository userRepository, AlunoRepository alunoRepository, ProfessorRepository professorRepository,
                        TelefoneRepository telefoneRepository, CertificacoesRepository certificacoesRepository,  CursoRepository cursoRepository) {
        this.userRepository = userRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.telefoneRepository = telefoneRepository;
        this.certificacoesRepository = certificacoesRepository;
        this.cursoRepository = cursoRepository;
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
    public List<Professor> listarProfessores() {
        List<Professor> listaSimples = professorRepository.findAll();

        if (listaSimples == null || listaSimples.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        for (Professor p : listaSimples) {
            User base = userRepository.findByCpf(p.getCpf());

            if (base != null) {
                p.setNome(base.getNome());
                p.setEmail(base.getEmail());
                p.setLogradouro(base.getLogradouro());
                p.setNumero(base.getNumero());
                p.setCep(base.getCep());
            }
        }

        return listaSimples;
    }
    public Professor pegarPorCpfProfessor(String cpf) {
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

            return professor;
        } catch (Exception e) {
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
    public Aluno pegarPorCpfAluno(String cpf) {
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

            return aluno;
        } catch (DataAccessException e) {
            return null;
        }
    }
    public List<Aluno> listarAlunos() {
        List<Aluno> listaSimples = alunoRepository.findAll();

        for (Aluno a : listaSimples) {
            User base = userRepository.findByCpf(a.getCpf());
            if (base != null) {
                a.setNome(base.getNome());
                a.setEmail(base.getEmail());
            }
        }
        return listaSimples;
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
}
