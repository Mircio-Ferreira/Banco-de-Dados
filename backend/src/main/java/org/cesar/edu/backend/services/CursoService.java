package org.cesar.edu.backend.services;

import org.cesar.edu.backend.dtos.requests.CursoRequest;
import org.cesar.edu.backend.dtos.responses.CursoResponse;
import org.cesar.edu.backend.models.*;
import org.cesar.edu.backend.repositories.*;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.cesar.edu.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;
    private final LecionaRepository lecionaRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaCursoRepository categoriaCursoRepository;

    @Autowired
    public CursoService(CursoRepository cursoRepository, ProfessorRepository professorRepository, LecionaRepository lecionaRepository, CategoriaRepository categoriaRepository, CategoriaCursoRepository categoriaCursoRepository) {
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
        this.lecionaRepository = lecionaRepository;
        this.categoriaRepository = categoriaRepository;
        this.categoriaCursoRepository = categoriaCursoRepository;
    }


    @Transactional
    public ResultService createCurso(CursoRequest curso) {
        ListaString erros = new ListaString();
        boolean valido = true;
        boolean realizado = false;

        ResultService result = validateCurso(curso);
        if (!result.isValid()) {
            return result;
        }

        try {
            boolean salvoCurso = cursoRepository.save(CursoRequest.toEntity(curso));
            if (!salvoCurso) {
                throw new RuntimeException("Erro ao salvar o curso");
            }

            Long idCursoGerado = cursoRepository.findByNome(curso.nomeCurso()).getId_curso();

            if (curso.categorias() != null && !curso.categorias().isEmpty()) {
                for (String categoria : curso.categorias()) {
                    Categoria existeCategoria = categoriaRepository.findByNome(categoria);
                    boolean foiSalvo;
                    if (existeCategoria != null) {
                        foiSalvo = categoriaCursoRepository.save(new CursoCategoria(idCursoGerado, existeCategoria.getId_categoria()));
                    } else {
                        boolean categoriaCriada = categoriaRepository.save(categoria);
                        if (!categoriaCriada) {
                            throw new RuntimeException("Erro ao salvar o categoria");
                        }
                        Categoria nomeCategoria = categoriaRepository.findByNome(categoria);
                        foiSalvo = categoriaCursoRepository.save(new CursoCategoria(idCursoGerado, nomeCategoria.getId_categoria()));
                    }
                    if (!foiSalvo) {
                        throw new RuntimeException("Erro ao salvar o categoria vinculada a curso");
                    }
                }
            }


            Leciona leciona = new Leciona();
            leciona.setId_curso(idCursoGerado);
            leciona.setCpf_professor(curso.cpfProfessor());

            boolean salvoLeciona = lecionaRepository.save(leciona);
            if (!salvoLeciona) {
                throw new RuntimeException("Erro ao salvar o leciona");
            }

            realizado = true;
            return new ResultService(valido, realizado, erros);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            erros.adicionar(e.getMessage());
            return new ResultService(valido, realizado, erros);
        }
    }

    @Transactional
    public ResultService updateCurso(CursoRequest cursoDto, Long id_curso) {
        ListaString erros = new ListaString();
        boolean valido = true;
        boolean realizado = false;

        Curso cursoExistente = cursoRepository.findById(id_curso);
        if (cursoExistente == null) {
            valido = false;
            erros.adicionar("Curso não encontrado para o ID informado.");
            return new ResultService(valido, realizado, erros);
        }

        Professor professor = professorRepository.findByCpf(cursoDto.cpfProfessor());
        if (professor == null || StringUtils.estaVazia(professor.getCpf())) {
            valido = false;
            erros.adicionar("Professor inexistente.");
            return new ResultService(valido, realizado, erros);
        }


        try {
            cursoExistente.setNome_curso(cursoDto.nomeCurso());
            cursoExistente.setPreco(cursoDto.preco());
            cursoExistente.setDescricao_curso(cursoDto.descricaoCurso());

            boolean atualizouCurso = cursoRepository.update(cursoExistente, id_curso);
            if (!atualizouCurso) {
                throw new RuntimeException("Erro ao atualizar os dados do curso no banco.");
            }
            boolean limpouLecionaAntigo = lecionaRepository.deleteByIdCurso(id_curso);
            if (!limpouLecionaAntigo) {
                throw new RuntimeException("Erro ao desvincular o professor antigo do curso.");
            }

            Leciona leciona = new Leciona();
            leciona.setId_curso(id_curso);
            leciona.setCpf_professor(cursoDto.cpfProfessor());

            boolean salvouLeciona = lecionaRepository.save(leciona);
            if (!salvouLeciona) {
                throw new RuntimeException("Erro ao vincular o novo professor ao curso.");
            }

            boolean limpouCategorias = categoriaCursoRepository.deleteByIdCurso(id_curso);
            if (!limpouCategorias) {
                throw new RuntimeException("Erro ao limpar as categorias antigas do curso.");
            }

            if (cursoDto.categorias() != null && !cursoDto.categorias().isEmpty()) {
                for (String nomeCategoria : cursoDto.categorias()) {

                    Categoria categoriaEncontrada = categoriaRepository.findByNome(nomeCategoria);

                    if (categoriaEncontrada == null) {
                        boolean categoriaCriada = categoriaRepository.save(nomeCategoria);
                        if (!categoriaCriada) {
                            throw new RuntimeException("Erro ao criar a categoria: " + nomeCategoria);
                        }
                        categoriaEncontrada = categoriaRepository.findByNome(nomeCategoria);
                    }

                    boolean salvoVinculo = categoriaCursoRepository.save(
                            new CursoCategoria(id_curso, categoriaEncontrada.getId_categoria())
                    );

                    if (!salvoVinculo) {
                        throw new RuntimeException("Erro ao vincular a categoria ao curso.");
                    }
                }
            }

            realizado = true;
            return new ResultService(valido, realizado, erros);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            erros.adicionar(e.getMessage());
            return new ResultService(valido, realizado, erros);
        }
    }

    @Transactional
    public ResultService delete(Long id_curso) {
        ListaString erros = new ListaString();
        boolean valido = true;
        boolean realizado = false;

        Curso cursoExistente = cursoRepository.findById(id_curso);
        if (cursoExistente == null) {
            valido = false;
            erros.adicionar("Curso não encontrado para o ID informado.");
            return new ResultService(valido, realizado, erros);
        }

        try {
            boolean limpouCategorias = categoriaCursoRepository.deleteByIdCurso(id_curso);
            if (!limpouCategorias) {
                throw new RuntimeException("Erro ao remover as categorias vinculadas a este curso.");
            }

            boolean deletouCurso = cursoRepository.delete(id_curso);
            if (!deletouCurso) {
                throw new RuntimeException("Erro ao deletar o curso do banco de dados.");
            }

            realizado = true;
            return new ResultService(valido, realizado, erros);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            erros.adicionar(e.getMessage());
            return new ResultService(valido, realizado, erros);
        }
    }

    public CursoResponse findResponseById(Long id_curso) {
        Curso curso = cursoRepository.findById(id_curso);
        if (curso == null) {
            return null;
        }
        List<CursoCategoria> relacoes = categoriaCursoRepository.findByCurso(id_curso);

        List<Categoria> categorias = new ArrayList<>();
        for (CursoCategoria relacao : relacoes) {
            Categoria cat = categoriaRepository.findById(relacao.getId_categoria());
            if (cat != null) {
                categorias.add(cat);
            }
        }
        List<Leciona> lecionas = lecionaRepository.findAllByIdCurso(id_curso);

        return CursoResponse.fromEntity(curso, categorias, lecionas);
    }

    public List<CursoResponse> findAllResponses() {
        List<Curso> cursos = cursoRepository.findAll();

        return cursos.stream().map(curso -> {
            List<CursoCategoria> relacoes = categoriaCursoRepository.findByCurso(curso.getId_curso());

            List<Categoria> categorias = new ArrayList<>();
            for (CursoCategoria relacao : relacoes) {
                Categoria cat = categoriaRepository.findById(relacao.getId_categoria());
                if (cat != null) {
                    categorias.add(cat);
                }
            }

            List<Leciona> lecionas = lecionaRepository.findAllByIdCurso(curso.getId_curso());

            return CursoResponse.fromEntity(curso, categorias, lecionas);

        }).toList();
    }

    private ResultService validateCurso(CursoRequest cursoDto) {
        ListaString erros = new ListaString();
        boolean valido = true;
        boolean realizado = false;

        Curso curso = CursoRequest.toEntity(cursoDto);
        Professor professor = professorRepository.findByCpf(cursoDto.cpfProfessor());
        if (professor == null || StringUtils.estaVazia(professor.getCpf())) {
            valido = false;
            erros.adicionar("Professor inexistente");
            return new ResultService(valido, realizado, erros);
        }

        Curso cursoExistente = cursoRepository.findByNome(curso.getNome_curso());
        if (cursoExistente != null) {
            valido = false;
            erros.adicionar("Já existe um curso cadastrado com o nome '" + curso.getNome_curso() + "'.");
            return new ResultService(valido, realizado, erros);
        }

        return new ResultService(valido, realizado, erros);
    }

    //consulta 1: consulta de cursos e suas comprar
    public List<ConsultaCursoComCompras> cursosComCompras() {
        List<ConsultaCursoComCompras> cursos = cursoRepository.cursosComCompras();

        if (cursos == null || cursos.isEmpty()) {
            return List.of();
        }

        for (ConsultaCursoComCompras curso : cursos) {

            if (curso == null) {
                throw new IllegalStateException("Foi encontrado um curso inválido na consulta.");
            }

            if (curso.getId_curso() == null || curso.getId_curso() <= 0) {
                throw new IllegalStateException("Curso com ID inválido encontrado.");
            }

            if (curso.getNome_curso() == null || curso.getNome_curso().isBlank()) {
                throw new IllegalStateException("Curso sem nome encontrado.");
            }

            if (curso.getPreco() == null || curso.getPreco() < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNome_curso() + "' possui preço inválido."
                );
            }

            if (curso.getTotal_compras() == null || curso.getTotal_compras() < 1) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNome_curso() + "' não possui compras válidas."
                );
            }

            if (curso.getReceita_estimada() == null || curso.getReceita_estimada() < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNome_curso() + "' possui receita estimada inválida."
                );
            }

            double receitaEsperada = curso.getPreco() * curso.getTotal_compras();

            if (Math.abs(receitaEsperada - curso.getReceita_estimada()) > 0.01) {
                throw new IllegalStateException(
                        "Receita estimada inconsistente para o curso '" + curso.getNome_curso() + "'."
                );
            }
        }

        return cursos;
    }

    //consulta 2: lista de modulos e suas respectivas aulas
    public List<ConsultaPegarModulosEAulas> pegarModulosEAulas(Long id_curso) {
        if (id_curso == null || id_curso <= 0) {
            throw new IllegalArgumentException("O ID do curso deve ser informado e maior que zero.");
        }

        List<ConsultaPegarModulosEAulas> modulosEAulas =
                cursoRepository.pegarModulosEAulas(id_curso);

        if (modulosEAulas == null || modulosEAulas.isEmpty()) {
            return List.of();
        }

        for (ConsultaPegarModulosEAulas item : modulosEAulas) {

            if (item == null) {
                throw new IllegalStateException("Foi encontrado um item inválido na consulta.");
            }

            Modulo modulo = item.getModulo();

            if (modulo == null) {
                throw new IllegalStateException("Foi encontrado um registro sem módulo.");
            }

            if (modulo.getId_modulo() == null || modulo.getId_modulo() <= 0) {
                throw new IllegalStateException("Foi encontrado um módulo com ID inválido.");
            }

            if (modulo.getTitulo() == null || modulo.getTitulo().isBlank()) {
                throw new IllegalStateException("Foi encontrado um módulo sem título.");
            }

            if (item.getAulas() == null) {
                throw new IllegalStateException(
                        "A lista de aulas do módulo '" + modulo.getTitulo() + "' está inválida."
                );
            }

            for (Aula aula : item.getAulas()) {

                if (aula == null) {
                    throw new IllegalStateException(
                            "Foi encontrada uma aula inválida no módulo '"
                                    + modulo.getTitulo() + "'."
                    );
                }

                if (aula.getId_aula() == null || aula.getId_aula() <= 0) {
                    throw new IllegalStateException(
                            "Foi encontrada uma aula com ID inválido no módulo '"
                                    + modulo.getTitulo() + "'."
                    );
                }

                if (aula.getTitulo() == null || aula.getTitulo().isBlank()) {
                    throw new IllegalStateException(
                            "Foi encontrada uma aula sem título no módulo '"
                                    + modulo.getTitulo() + "'."
                    );
                }
            }
        }

        return modulosEAulas;
    }

    //consulta 3 : pega os cursos mais baratos
    public List<ConsultaCursoBarato> pegarCursosBaratos() {
        List<ConsultaCursoBarato> cursos = cursoRepository.pegarCursosBaratos();

        if (cursos == null || cursos.isEmpty()) {
            return List.of();
        }

        for (ConsultaCursoBarato curso : cursos) {

            if (curso == null) {
                throw new IllegalStateException("Foi encontrado um curso inválido na consulta.");
            }

            if (curso.getId_curso() == null || curso.getId_curso() <= 0) {
                throw new IllegalStateException("Curso com ID inválido encontrado.");
            }

            if (curso.getNome_curso() == null || curso.getNome_curso().isBlank()) {
                throw new IllegalStateException("Curso sem nome encontrado.");
            }

            if (curso.getPreco() == null) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNome_curso() + "' está sem preço."
                );
            }

            if (curso.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNome_curso() + "' possui preço inválido."
                );
            }
        }

        return cursos;
    }

    //function 1: pega as horas totais de um curso
    public Integer pegarHorasTotais(Long id_curso) {
        if (id_curso == null || id_curso <= 0) {
            throw new IllegalArgumentException("O ID do curso deve ser informado e maior que zero.");
        }

        Integer cargaHorariaTotal = cursoRepository.pegarHorasTotais(id_curso);

        if (cargaHorariaTotal == null) {
            return 0;
        }

        if (cargaHorariaTotal < 0) {
            throw new IllegalStateException("A carga horária total do curso não pode ser negativa.");
        }

        return cargaHorariaTotal;
    }

    //procedure 1: aplica um desconto em todos os cursos de uma determinada categoria
    public boolean aplicarDescontoEmCategoria(String categoria, Double desconto) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("A categoria deve ser informada.");
        }

        if (desconto == null) {
            throw new IllegalArgumentException("O desconto deve ser informado.");
        }

        if (desconto.isNaN() || desconto.isInfinite()) {
            throw new IllegalArgumentException("O desconto informado é inválido.");
        }

        if (desconto < 0 || desconto > 100) {
            throw new IllegalArgumentException("O desconto deve estar entre 0 e 100.");
        }

        boolean descontoAplicado = cursoRepository.aplicarDescontoEmCategoria(
                categoria.trim(),
                desconto
        );

        if (!descontoAplicado) {
            throw new IllegalStateException(
                    "Não foi possível aplicar o desconto. Verifique se a categoria existe."
            );
        }

        return true;
    }

    //view 2: retorna as informações geral do curso como receita, media do preco geral, total de compras etc
    public List<ViewResumoGeralCurso> viewResumoGeralCurso() {
        List<ViewResumoGeralCurso> cursos = cursoRepository.viewResumoGeralCurso();

        if (cursos == null || cursos.isEmpty()) {
            return List.of();
        }

        for (ViewResumoGeralCurso curso : cursos) {

            if (curso == null) {
                throw new IllegalStateException("Foi encontrado um curso inválido na view.");
            }

            if (curso.getIdCurso() == null || curso.getIdCurso() <= 0) {
                throw new IllegalStateException("Curso com ID inválido encontrado.");
            }

            if (curso.getNomeCurso() == null || curso.getNomeCurso().isBlank()) {
                throw new IllegalStateException("Curso sem nome encontrado.");
            }

            if (curso.getPreco() == null) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' está sem preço."
                );
            }

            if (curso.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui preço inválido."
                );
            }

            if (curso.getTotalCompras() == null || curso.getTotalCompras() < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui total de compras inválido."
                );
            }

            if (curso.getReceitaEstimada() == null) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' está sem receita estimada."
                );
            }

            if (curso.getReceitaEstimada().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui receita estimada inválida."
                );
            }

            if (curso.getMediaPrecoGeral() == null) {
                throw new IllegalStateException("A média geral de preço não foi calculada.");
            }

            if (curso.getMediaPrecoGeral().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("A média geral de preço está inválida.");
            }

            if (curso.getClassificacaoPreco() == null || curso.getClassificacaoPreco().isBlank()) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' está sem classificação de preço."
                );
            }

            if (!curso.getClassificacaoPreco().equals("ACIMA_DA_MEDIA")
                    && !curso.getClassificacaoPreco().equals("NA_MEDIA")
                    && !curso.getClassificacaoPreco().equals("ABAIXO_DA_MEDIA")) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui classificação de preço inválida."
                );
            }

            BigDecimal receitaEsperada = curso.getPreco()
                    .multiply(BigDecimal.valueOf(curso.getTotalCompras()));

            if (receitaEsperada.compareTo(curso.getReceitaEstimada()) != 0) {
                throw new IllegalStateException(
                        "Receita estimada inconsistente para o curso '" + curso.getNomeCurso() + "'."
                );
            }
        }

        return cursos;
    }
    //mesma coisa da view anterior mas com o curso especificado
    public List<ViewResumoGeralCurso> viewResumoGeralCursos(Long id_curso) {
        if (id_curso == null || id_curso <= 0) {
            throw new IllegalArgumentException("O ID do curso deve ser informado e maior que zero.");
        }

        List<ViewResumoGeralCurso> cursos = cursoRepository.viewResumoGeralCursos(id_curso);

        if (cursos == null || cursos.isEmpty()) {
            return List.of();
        }

        for (ViewResumoGeralCurso curso : cursos) {

            if (curso == null) {
                throw new IllegalStateException("Foi encontrado um curso inválido na view.");
            }

            if (curso.getIdCurso() == null || curso.getIdCurso() <= 0) {
                throw new IllegalStateException("Curso com ID inválido encontrado.");
            }

            if (!curso.getIdCurso().equals(id_curso)) {
                throw new IllegalStateException("A view retornou um curso diferente do ID solicitado.");
            }

            if (curso.getNomeCurso() == null || curso.getNomeCurso().isBlank()) {
                throw new IllegalStateException("Curso sem nome encontrado.");
            }

            if (curso.getPreco() == null) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' está sem preço."
                );
            }

            if (curso.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui preço inválido."
                );
            }

            if (curso.getTotalCompras() == null || curso.getTotalCompras() < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui total de compras inválido."
                );
            }

            if (curso.getReceitaEstimada() == null) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' está sem receita estimada."
                );
            }

            if (curso.getReceitaEstimada().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui receita estimada inválida."
                );
            }

            if (curso.getMediaPrecoGeral() == null) {
                throw new IllegalStateException("A média geral de preço não foi calculada.");
            }

            if (curso.getMediaPrecoGeral().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("A média geral de preço está inválida.");
            }

            if (curso.getClassificacaoPreco() == null || curso.getClassificacaoPreco().isBlank()) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' está sem classificação de preço."
                );
            }

            if (!curso.getClassificacaoPreco().equals("ACIMA_DA_MEDIA")
                    && !curso.getClassificacaoPreco().equals("NA_MEDIA")
                    && !curso.getClassificacaoPreco().equals("ABAIXO_DA_MEDIA")) {
                throw new IllegalStateException(
                        "Curso '" + curso.getNomeCurso() + "' possui classificação de preço inválida."
                );
            }

            BigDecimal receitaEsperada = curso.getPreco()
                    .multiply(BigDecimal.valueOf(curso.getTotalCompras()));

            if (receitaEsperada.compareTo(curso.getReceitaEstimada()) != 0) {
                throw new IllegalStateException(
                        "Receita estimada inconsistente para o curso '" + curso.getNomeCurso() + "'."
                );
            }
        }

        return cursos;
    }

    //trigger + consulta: essa consulta é para ver o historico da atualização de preços dos cursos pelo trigger
    public List<LogPrecoCurso> pegarHistoricoPrecoCurso(Long id_curso) {
        if (id_curso == null || id_curso <= 0) {
            throw new IllegalArgumentException("O ID do curso deve ser informado e maior que zero.");
        }

        List<LogPrecoCurso> historico = cursoRepository.pegarHistoricoPrecoCurso(id_curso);

        if (historico == null || historico.isEmpty()) {
            return List.of();
        }

        for (LogPrecoCurso log : historico) {

            if (log == null) {
                throw new IllegalStateException("Foi encontrado um registro de log inválido.");
            }

            if (log.getIdLog() == null || log.getIdLog() <= 0) {
                throw new IllegalStateException("Foi encontrado um log com ID inválido.");
            }

            if (log.getIdCurso() == null || log.getIdCurso() <= 0) {
                throw new IllegalStateException("Foi encontrado um log com ID de curso inválido.");
            }

            if (!log.getIdCurso().equals(id_curso)) {
                throw new IllegalStateException("O histórico retornou um curso diferente do solicitado.");
            }

            if (log.getPrecoAntigo() == null) {
                throw new IllegalStateException("Foi encontrado um log sem preço antigo.");
            }

            if (log.getPrecoNovo() == null) {
                throw new IllegalStateException("Foi encontrado um log sem preço novo.");
            }

            if (log.getPrecoAntigo().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("Foi encontrado um log com preço antigo inválido.");
            }

            if (log.getPrecoNovo().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("Foi encontrado um log com preço novo inválido.");
            }

            if (log.getPrecoAntigo().compareTo(log.getPrecoNovo()) == 0) {
                throw new IllegalStateException("Foi encontrado um log sem alteração real de preço.");
            }

            if (log.getDataAlteracao() == null) {
                throw new IllegalStateException("Foi encontrado um log sem data de alteração.");
            }

            if (log.getUsuarioBanco() == null || log.getUsuarioBanco().isBlank()) {
                throw new IllegalStateException("Foi encontrado um log sem usuário do banco.");
            }
        }

        return historico;
    }
}
