package org.cesar.edu.backend.services;

import org.cesar.edu.backend.dtos.requests.ModuloRequest;
import org.cesar.edu.backend.dtos.responses.ModuloResponse;
import org.cesar.edu.backend.models.Modulo;
import org.cesar.edu.backend.repositories.ModuloRepository;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuloService {

    private final ModuloRepository moduloRepository;

    @Autowired
    public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    public List<ModuloResponse> findAll() {
        return moduloRepository.findAll()
                .stream()
                .map(ModuloResponse::fromEntity)
                .toList();
    }

    public Modulo findByTitulo(String titulo, Long id_curso) {
        if (titulo == null || titulo.isBlank() || id_curso == null || id_curso < 0) {
            return null;
        }
        return moduloRepository.findByTitulo(titulo, id_curso);
    }

    public ModuloResponse findById(Long idCurso, Long idModulo) {
        ListaString erros = new ListaString();

        validarIdCurso(idCurso, erros);
        validarIdModulo(idModulo, erros);

        if (erros.tamanho() > 0) {
            throw new IllegalArgumentException(erros.toString());
        }

        Modulo modulo = moduloRepository.findById(idModulo, idCurso);

        if (modulo == null) {
            throw new RuntimeException("Módulo não encontrado.");
        }

        return ModuloResponse.fromEntity(modulo);
    }

    public ResultService save(ModuloRequest request) {
        ListaString erros = validarRequest(request);

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        Modulo modulo = request.toEntity();
        normalizarDados(modulo);

        if (existeTituloNoCurso(modulo.getTitulo(), modulo.getId_curso(), null)) {
            erros.adicionar("Já existe um módulo com esse título neste curso.");
            return new ResultService(false, false, erros);
        }

        try {
            boolean salvou = moduloRepository.save(modulo);

            if (!salvou) {
                erros.adicionar("Não foi possível salvar o módulo.");
                return new ResultService(true, false, erros);
            }

            return new ResultService(true, true, erros);

        } catch (DataIntegrityViolationException e) {
            erros.adicionar("O curso informado não existe ou os dados são inválidos.");
            return new ResultService(false, false, erros);

        } catch (Exception e) {
            erros.adicionar("Erro inesperado ao salvar módulo: " + e.getMessage());
            return new ResultService(false, false, erros);
        }
    }

    public ResultService update(Long idCurso, Long idModulo, ModuloRequest request) {
        ListaString erros = new ListaString();

        validarIdCurso(idCurso, erros);
        validarIdModulo(idModulo, erros);

        ListaString errosRequest = validarRequest(request);
        adicionarTodos(erros, errosRequest);

        if (request != null && request.id_curso() != null && idCurso != null) {
            if (!request.id_curso().equals(idCurso)) {
                erros.adicionar("O id_curso da URL precisa ser igual ao id_curso do corpo da requisição.");
            }
        }

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        Modulo moduloExistente = moduloRepository.findById(idModulo, idCurso);

        if (moduloExistente == null) {
            erros.adicionar("Módulo não encontrado para atualização.");
            return new ResultService(false, false, erros);
        }

        Modulo moduloAtualizado = request.toEntity();
        normalizarDados(moduloAtualizado);

        if (existeTituloNoCurso(moduloAtualizado.getTitulo(), idCurso, idModulo)) {
            erros.adicionar("Já existe outro módulo com esse título neste curso.");
            return new ResultService(false, false, erros);
        }

        try {
            boolean atualizou = moduloRepository.update(moduloAtualizado, idModulo, idCurso);

            if (!atualizou) {
                erros.adicionar("Não foi possível atualizar o módulo.");
                return new ResultService(true, false, erros);
            }

            return new ResultService(true, true, erros);

        } catch (DataIntegrityViolationException e) {
            erros.adicionar("O curso informado não existe ou os dados são inválidos.");
            return new ResultService(false, false, erros);

        } catch (Exception e) {
            erros.adicionar("Erro inesperado ao atualizar módulo: " + e.getMessage());
            return new ResultService(false, false, erros);
        }
    }

    public ResultService delete(Long idCurso, Long idModulo) {
        ListaString erros = new ListaString();

        validarIdCurso(idCurso, erros);
        validarIdModulo(idModulo, erros);

        if (erros.tamanho() > 0) {
            return new ResultService(false, false, erros);
        }

        Modulo modulo = moduloRepository.findById(idModulo, idCurso);

        if (modulo == null) {
            erros.adicionar("Módulo não encontrado para exclusão.");
            return new ResultService(false, false, erros);
        }

        try {
            boolean deletou = moduloRepository.delete(idCurso, idModulo);

            if (!deletou) {
                erros.adicionar("Não foi possível deletar o módulo.");
                return new ResultService(true, false, erros);
            }

            return new ResultService(true, true, erros);

        } catch (Exception e) {
            erros.adicionar("Erro inesperado ao deletar módulo: " + e.getMessage());
            return new ResultService(false, false, erros);
        }
    }

    private ListaString validarRequest(ModuloRequest request) {
        ListaString erros = new ListaString();

        if (request == null) {
            erros.adicionar("Os dados do módulo são obrigatórios.");
            return erros;
        }

        validarIdCurso(request.id_curso(), erros);

        if (request.titulo() == null || request.titulo().isBlank()) {
            erros.adicionar("O título do módulo é obrigatório.");
        } else if (request.titulo().trim().length() < 3) {
            erros.adicionar("O título do módulo deve ter pelo menos 3 caracteres.");
        }

        if (request.carga_horaria() == null) {
            erros.adicionar("A carga horária é obrigatória.");
        } else if (request.carga_horaria() <= 0) {
            erros.adicionar("A carga horária deve ser maior que zero.");
        }

        return erros;
    }

    private void validarIdCurso(Long idCurso, ListaString erros) {
        if (idCurso == null) {
            erros.adicionar("O id do curso é obrigatório.");
        } else if (idCurso <= 0) {
            erros.adicionar("O id do curso deve ser maior que zero.");
        }
    }

    private void validarIdModulo(Long idModulo, ListaString erros) {
        if (idModulo == null) {
            erros.adicionar("O id do módulo é obrigatório.");
        } else if (idModulo <= 0) {
            erros.adicionar("O id do módulo deve ser maior que zero.");
        }
    }

    private void normalizarDados(Modulo modulo) {
        modulo.setTitulo(modulo.getTitulo().trim());

        if (modulo.getDescricao_curso() != null) {
            String descricao = modulo.getDescricao_curso().trim();

            if (descricao.isBlank()) {
                modulo.setDescricao_curso(null);
            } else {
                modulo.setDescricao_curso(descricao);
            }
        }
    }

    private boolean existeTituloNoCurso(String titulo, Long idCurso, Long idModuloIgnorado) {
        return moduloRepository.findAll()
                .stream()
                .anyMatch(modulo ->
                        modulo.getId_curso().equals(idCurso)
                                && modulo.getTitulo() != null
                                && modulo.getTitulo().equals(titulo)
                                && (
                                idModuloIgnorado == null
                                        || !modulo.getId_modulo().equals(idModuloIgnorado)
                        )
                );
    }

    private void adicionarTodos(ListaString destino, ListaString origem) {
        for (String erro : origem.listar()) {
            destino.adicionar(erro);
        }
    }
}