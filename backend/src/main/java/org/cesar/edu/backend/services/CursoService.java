package org.cesar.edu.backend.services;

import org.cesar.edu.backend.dtos.requests.CursoRequest;
import org.cesar.edu.backend.models.*;
import org.cesar.edu.backend.repositories.*;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.cesar.edu.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;
    private final LecionaRepository lecionaRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaCursoRepository categoriaCursoRepository;
    @Autowired
    public CursoService(CursoRepository cursoRepository, ProfessorRepository professorRepository,LecionaRepository lecionaRepository, CategoriaRepository categoriaRepository ,CategoriaCursoRepository categoriaCursoRepository) {
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

            if(curso.categorias() != null && !curso.categorias().isEmpty()){
                for(String categoria: curso.categorias()){
                    Categoria existeCategoria = categoriaRepository.findByNome(categoria);
                    boolean foiSalvo;
                    if(existeCategoria != null){
                        foiSalvo = categoriaCursoRepository.save(new CursoCategoria(idCursoGerado, existeCategoria.getId_categoria()));
                    }
                    else{
                        boolean categoriaCriada = categoriaRepository.save(categoria);
                        if(!categoriaCriada){
                            throw new RuntimeException("Erro ao salvar o categoria");
                        }
                        Categoria nomeCategoria = categoriaRepository.findByNome(categoria);
                        foiSalvo = categoriaCursoRepository.save(new CursoCategoria(idCursoGerado, nomeCategoria.getId_categoria()));
                    }
                    if(!foiSalvo){
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
    public Curso findById(Long id_curso){
        return cursoRepository.findById(id_curso);
    }
    public List<Curso> findAll(){return cursoRepository.findAll();}

    private ResultService validateCurso(CursoRequest cursoDto){
        ListaString erros = new ListaString();
        boolean valido = true;
        boolean realizado =  false;

        Curso curso = CursoRequest.toEntity(cursoDto);
        Professor professor = professorRepository.findByCpf(cursoDto.cpfProfessor());
        if(professor == null || StringUtils.estaVazia(professor.getCpf())){
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
}
