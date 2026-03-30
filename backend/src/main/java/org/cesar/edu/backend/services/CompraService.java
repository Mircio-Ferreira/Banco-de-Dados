package org.cesar.edu.backend.services;

import org.cesar.edu.backend.dtos.requests.CompraRequest;
import org.cesar.edu.backend.dtos.responses.CompraResponse;
import org.cesar.edu.backend.models.Aluno;
import org.cesar.edu.backend.models.Compra;
import org.cesar.edu.backend.models.Curso;
import org.cesar.edu.backend.repositories.AlunoRepository;
import org.cesar.edu.backend.repositories.CompraRepository;
import org.cesar.edu.backend.repositories.CursoRepository;
import org.cesar.edu.backend.utils.ListaString;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final CursoRepository cursoRepository;
    private final AlunoRepository alunoRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository, CursoRepository cursoRepository, AlunoRepository alunoRepository) {
        this.compraRepository = compraRepository;
        this.cursoRepository = cursoRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public ResultService createCompra(CompraRequest compraRequest) {
        ListaString erros = new ListaString();
        boolean valido = true;
        boolean realizado = false;

        Curso curso = cursoRepository.findById(compraRequest.id_curso());
        if (curso == null) {
            valido = false;
            erros.adicionar("Curso não encontrado para o ID informado.");
            return new ResultService(valido, realizado, erros);
        }

        Aluno aluno = alunoRepository.findByCpf(compraRequest.cpf_aluno());
        if (aluno == null) {
            valido = false;
            erros.adicionar("Aluno não encontrado para o CPF informado.");
            return new ResultService(valido, realizado, erros);
        }

        try {
            Compra compraExistente = compraRepository.findByIdCpf(compraRequest.id_curso(), compraRequest.cpf_aluno());
            if (compraExistente != null) {
                valido = false;
                erros.adicionar("Este aluno já possui este curso.");
                return new ResultService(valido, realizado, erros);
            }
        } catch (EmptyResultDataAccessException e) {
        }

        try {
            Compra novaCompra = CompraRequest.toEntity(compraRequest);
            novaCompra.setData_compra(LocalDate.now());

            boolean salvou = compraRepository.save(novaCompra);
            if (!salvou) {
                throw new RuntimeException("Erro interno ao registrar a compra no banco de dados.");
            }

            realizado = true;
            return new ResultService(valido, realizado, erros);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            erros.adicionar("Erro ao processar compra: " + e.getMessage());
            return new ResultService(valido, realizado, erros);
        }
    }

    @Transactional
    public ResultService deleteCompra(Long id_curso, String cpf_aluno) {
        ListaString erros = new ListaString();
        boolean valido = true;
        boolean realizado = false;

        try {
            compraRepository.findByIdCpf(id_curso, cpf_aluno);
        } catch (EmptyResultDataAccessException e) {
            valido = false;
            erros.adicionar("Registro de compra não encontrado.");
            return new ResultService(valido, realizado, erros);
        }

        try {
            boolean deletou = compraRepository.delete(id_curso, cpf_aluno);
            if (!deletou) {
                throw new RuntimeException("Erro ao deletar a compra do banco de dados.");
            }

            realizado = true;
            return new ResultService(valido, realizado, erros);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            erros.adicionar(e.getMessage());
            return new ResultService(valido, realizado, erros);
        }
    }

    public CompraResponse findResponseById(Long id_curso, String cpf_aluno) {
        try {
            Compra compra = compraRepository.findByIdCpf(id_curso, cpf_aluno);
            return CompraResponse.fromEntity(compra);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<CompraResponse> findAllResponses() {
        List<Compra> compras = compraRepository.findAll();
        return compras.stream().map(CompraResponse::fromEntity).toList();
    }

    public List<CompraResponse> findResponsesByAluno(String cpf_aluno) {
        List<Compra> compras = compraRepository.findByAluno(cpf_aluno);
        return compras.stream().map(CompraResponse::fromEntity).toList();
    }
}