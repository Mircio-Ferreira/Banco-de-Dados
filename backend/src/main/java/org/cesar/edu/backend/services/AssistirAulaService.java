package org.cesar.edu.backend.services;

import org.cesar.edu.backend.models.AssistirAula;
import org.cesar.edu.backend.repositories.AssistirAulaRepository;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistirAulaService {

    private final AssistirAulaRepository assistirAulaRepository;

    @Autowired
    public AssistirAulaService(AssistirAulaRepository assistirAulaRepository) {
        this.assistirAulaRepository = assistirAulaRepository;
    }

    public List<AssistirAula> findAll() {
        return assistirAulaRepository.findAll();
    }

    public AssistirAula findById(String cpf, Long id_aula, Long id_modulo, Long id_curso) {
        ListaString erros = new ListaString();

        validarCpf(cpf, erros);
        validarIdAula(id_aula, erros);
        validarIdModulo(id_modulo, erros);
        validarIdCurso(id_curso, erros);

        if (erros.tamanho() > 0) {
            return null;
        }

        return assistirAulaRepository.findById(cpf, id_aula, id_modulo, id_curso);
    }

    public ResultService save(AssistirAula assistirAula) {
        ListaString erros = new ListaString();

        validarAssistirAula(assistirAula, erros);

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        AssistirAula assistirExistente = assistirAulaRepository.findById(
                assistirAula.getCpf_aluno(),
                assistirAula.getId_aula(),
                assistirAula.getId_modulo(),
                assistirAula.getId_curso()
        );

        if (assistirExistente != null) {
            erros.adicionar("Esse aluno já assistiu essa aula.");
            return new ResultService(false, false, erros);
        }

        boolean salvou = assistirAulaRepository.save(assistirAula);

        if (!salvou) {
            erros.adicionar("Não foi possível registrar que o aluno assistiu a aula. Verifique se o aluno, o curso, o módulo e a aula existem.");
            return new ResultService(true, false, erros);
        }

        return new ResultService(true, true, erros);
    }

    public ResultService delete(String cpf, Long id_aula, Long id_modulo, Long id_curso) {
        ListaString erros = new ListaString();

        validarCpf(cpf, erros);
        validarIdAula(id_aula, erros);
        validarIdModulo(id_modulo, erros);
        validarIdCurso(id_curso, erros);

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        AssistirAula assistirAula = assistirAulaRepository.findById(
                cpf,
                id_aula,
                id_modulo,
                id_curso
        );

        if (assistirAula == null) {
            erros.adicionar("Registro de aula assistida não encontrado.");
            return new ResultService(false, false, erros);
        }

        boolean deletou = assistirAulaRepository.delete(
                cpf,
                id_aula,
                id_modulo,
                id_curso
        );

        if (!deletou) {
            erros.adicionar("Não foi possível remover o registro de aula assistida.");
            return new ResultService(true, false, erros);
        }

        return new ResultService(true, true, erros);
    }

    private void validarAssistirAula(AssistirAula assistirAula, ListaString erros) {
        if (assistirAula == null) {
            erros.adicionar("Os dados da aula assistida devem ser informados.");
            return;
        }

        validarCpf(assistirAula.getCpf_aluno(), erros);
        validarIdAula(assistirAula.getId_aula(), erros);
        validarIdModulo(assistirAula.getId_modulo(), erros);
        validarIdCurso(assistirAula.getId_curso(), erros);
    }

    private void validarCpf(String cpf, ListaString erros) {
        if (cpf == null || cpf.isBlank()) {
            erros.adicionar("O CPF do aluno deve ser informado.");
            return;
        }

        if (cpf.length() != 11) {
            erros.adicionar("O CPF do aluno deve conter exatamente 11 caracteres.");
            return;
        }

        if (!cpf.matches("\\d{11}")) {
            erros.adicionar("O CPF do aluno deve conter apenas números.");
        }
    }

    private void validarIdAula(Long id_aula, ListaString erros) {
        if (id_aula == null) {
            erros.adicionar("O id da aula deve ser informado.");
            return;
        }

        if (id_aula <= 0) {
            erros.adicionar("O id da aula deve ser maior que zero.");
        }
    }

    private void validarIdModulo(Long id_modulo, ListaString erros) {
        if (id_modulo == null) {
            erros.adicionar("O id do módulo deve ser informado.");
            return;
        }

        if (id_modulo <= 0) {
            erros.adicionar("O id do módulo deve ser maior que zero.");
        }
    }

    private void validarIdCurso(Long id_curso, ListaString erros) {
        if (id_curso == null) {
            erros.adicionar("O id do curso deve ser informado.");
            return;
        }

        if (id_curso <= 0) {
            erros.adicionar("O id do curso deve ser maior que zero.");
        }
    }
}