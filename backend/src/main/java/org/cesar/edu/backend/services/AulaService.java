package org.cesar.edu.backend.services;

import org.cesar.edu.backend.dtos.requests.AulaRequest;
import org.cesar.edu.backend.dtos.responses.AulaResponse;
import org.cesar.edu.backend.models.Aula;
import org.cesar.edu.backend.models.Modulo;
import org.cesar.edu.backend.repositories.AulaRepository;
import org.cesar.edu.backend.repositories.ModuloRepository;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final ModuloRepository moduloRepository;

    @Autowired
    public AulaService(AulaRepository aulaRepository, ModuloRepository moduloRepository) {
        this.aulaRepository = aulaRepository;
        this.moduloRepository = moduloRepository;
    }

    public List<AulaResponse> findAll() {
        return aulaRepository.findAll()
                .stream()
                .map(AulaResponse::fromEntity)
                .toList();
    }

    public AulaResponse findById(Long idAula, Long idModulo, Long idCurso) {
        ListaString erros = new ListaString();

        validarIdAula(idAula, erros);
        validarIdModulo(idModulo, erros);
        validarIdCurso(idCurso, erros);

        if (erros.tamanho() > 0) {
            throw new IllegalArgumentException(erros.toString());
        }

        Aula aula = aulaRepository.findById(idAula, idModulo, idCurso);

        if (aula == null) {
            throw new RuntimeException("Aula não encontrada.");
        }

        return AulaResponse.fromEntity(aula);
    }

    public ResultService save(AulaRequest request) {
        ListaString erros = validarRequest(request);

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        if (!moduloExiste(request.id_modulo(), request.id_curso())) {
            erros.adicionar("O módulo informado não existe para este curso.");
            return new ResultService(false, false, erros);
        }

        Aula aula = request.toEntity();
        normalizarDados(aula);

        if (existeTituloNoModulo(aula.getTitulo(), aula.getId_modulo(), aula.getId_curso(), null)) {
            erros.adicionar("Já existe uma aula com esse título neste módulo.");
            return new ResultService(false, false, erros);
        }

        try {
            boolean salvou = aulaRepository.save(aula);

            if (!salvou) {
                erros.adicionar("Não foi possível salvar a aula.");
                return new ResultService(true, false, erros);
            }

            return new ResultService(true, true, erros);

        } catch (DataIntegrityViolationException e) {
            erros.adicionar("O curso ou módulo informado não existe, ou os dados violam alguma regra do banco.");
            return new ResultService(false, false, erros);

        } catch (Exception e) {
            erros.adicionar("Erro inesperado ao salvar aula: " + e.getMessage());
            return new ResultService(false, false, erros);
        }
    }

    public ResultService update(Long idAula, Long idModulo, Long idCurso, AulaRequest request) {
        ListaString erros = new ListaString();

        validarIdAula(idAula, erros);
        validarIdModulo(idModulo, erros);
        validarIdCurso(idCurso, erros);

        ListaString errosRequest = validarRequest(request);
        adicionarTodos(erros, errosRequest);

        if (request != null) {
            if (request.id_curso() != null && idCurso != null && !request.id_curso().equals(idCurso)) {
                erros.adicionar("O id_curso da URL precisa ser igual ao id_curso do corpo da requisição.");
            }

            if (request.id_modulo() != null && idModulo != null && !request.id_modulo().equals(idModulo)) {
                erros.adicionar("O id_modulo da URL precisa ser igual ao id_modulo do corpo da requisição.");
            }
        }

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        Aula aulaExistente = aulaRepository.findById(idAula, idModulo, idCurso);

        if (aulaExistente == null) {
            erros.adicionar("Aula não encontrada para atualização.");
            return new ResultService(false, false, erros);
        }

        if (!moduloExiste(idModulo, idCurso)) {
            erros.adicionar("O módulo informado não existe para este curso.");
            return new ResultService(false, false, erros);
        }

        Aula aulaAtualizada = request.toEntity();
        normalizarDados(aulaAtualizada);

        if (existeTituloNoModulo(aulaAtualizada.getTitulo(), idModulo, idCurso, idAula)) {
            erros.adicionar("Já existe outra aula com esse título neste módulo.");
            return new ResultService(false, false, erros);
        }

        try {
            boolean atualizou = aulaRepository.update(aulaAtualizada, idAula, idModulo, idCurso);

            if (!atualizou) {
                erros.adicionar("Não foi possível atualizar a aula.");
                return new ResultService(true, false, erros);
            }

            return new ResultService(true, true, erros);

        } catch (DataIntegrityViolationException e) {
            erros.adicionar("O curso ou módulo informado não existe, ou os dados violam alguma regra do banco.");
            return new ResultService(false, false, erros);

        } catch (Exception e) {
            erros.adicionar("Erro inesperado ao atualizar aula: " + e.getMessage());
            return new ResultService(false, false, erros);
        }
    }

    public ResultService delete(Long idAula, Long idModulo, Long idCurso) {
        ListaString erros = new ListaString();

        validarIdAula(idAula, erros);
        validarIdModulo(idModulo, erros);
        validarIdCurso(idCurso, erros);

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        Aula aula = aulaRepository.findById(idAula, idModulo, idCurso);

        if (aula == null) {
            erros.adicionar("Aula não encontrada para exclusão.");
            return new ResultService(false, false, erros);
        }

        try {
            boolean deletou = aulaRepository.delete(idAula, idModulo, idCurso);

            if (!deletou) {
                erros.adicionar("Não foi possível deletar a aula.");
                return new ResultService(true, false, erros);
            }

            return new ResultService(true, true, erros);

        } catch (Exception e) {
            erros.adicionar("Erro inesperado ao deletar aula: " + e.getMessage());
            return new ResultService(false, false, erros);
        }
    }

    private ListaString validarRequest(AulaRequest request) {
        ListaString erros = new ListaString();

        if (request == null) {
            erros.adicionar("Os dados da aula são obrigatórios.");
            return erros;
        }

        validarIdCurso(request.id_curso(), erros);
        validarIdModulo(request.id_modulo(), erros);

        if (request.titulo() == null || request.titulo().isBlank()) {
            erros.adicionar("O título da aula é obrigatório.");
        } else if (request.titulo().trim().length() < 3) {
            erros.adicionar("O título da aula deve ter pelo menos 3 caracteres.");
        } else if (request.titulo().trim().length() > 255) {
            erros.adicionar("O título da aula deve ter no máximo 255 caracteres.");
        }

        if (request.link_do_video() == null || request.link_do_video().isBlank()) {
            erros.adicionar("O link do vídeo é obrigatório.");
        } else if (request.link_do_video().trim().length() > 255) {
            erros.adicionar("O link do vídeo deve ter no máximo 255 caracteres.");
        } else if (!linkEhValido(request.link_do_video())) {
            erros.adicionar("O link do vídeo deve começar com http:// ou https://.");
        }

        return erros;
    }

    private void validarIdAula(Long idAula, ListaString erros) {
        if (idAula == null) {
            erros.adicionar("O id da aula é obrigatório.");
        } else if (idAula <= 0) {
            erros.adicionar("O id da aula deve ser maior que zero.");
        }
    }

    private void validarIdModulo(Long idModulo, ListaString erros) {
        if (idModulo == null) {
            erros.adicionar("O id do módulo é obrigatório.");
        } else if (idModulo <= 0) {
            erros.adicionar("O id do módulo deve ser maior que zero.");
        }
    }

    private void validarIdCurso(Long idCurso, ListaString erros) {
        if (idCurso == null) {
            erros.adicionar("O id do curso é obrigatório.");
        } else if (idCurso <= 0) {
            erros.adicionar("O id do curso deve ser maior que zero.");
        }
    }

    private void normalizarDados(Aula aula) {
        aula.setTitulo(aula.getTitulo().trim());
        aula.setLink(aula.getLink().trim());

        if (aula.getDescricao_aula() != null) {
            String descricao = aula.getDescricao_aula().trim();

            if (descricao.isBlank()) {
                aula.setDescricao_aula(null);
            } else {
                aula.setDescricao_aula(descricao);
            }
        }
    }

    private boolean moduloExiste(Long idModulo, Long idCurso) {
        Modulo modulo = moduloRepository.findById(idModulo, idCurso);
        return modulo != null;
    }

    private boolean existeTituloNoModulo(
            String titulo,
            Long idModulo,
            Long idCurso,
            Long idAulaIgnorada
    ) {
        return aulaRepository.findAll()
                .stream()
                .anyMatch(aula ->
                        aula.getId_curso().equals(idCurso)
                                && aula.getId_modulo().equals(idModulo)
                                && aula.getTitulo() != null
                                && aula.getTitulo().equals(titulo)
                                && (
                                idAulaIgnorada == null
                                        || !aula.getId_aula().equals(idAulaIgnorada)
                        )
                );
    }

    private boolean linkEhValido(String link) {
        String linkTratado = link.trim();

        return linkTratado.startsWith("http://")
                || linkTratado.startsWith("https://");
    }

    private void adicionarTodos(ListaString destino, ListaString origem) {
        for (String erro : origem.listar()) {
            destino.adicionar(erro);
        }
    }
}