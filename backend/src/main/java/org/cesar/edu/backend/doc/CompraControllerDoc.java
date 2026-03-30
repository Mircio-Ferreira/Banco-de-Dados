package org.cesar.edu.backend.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cesar.edu.backend.dtos.requests.CompraRequest;
import org.cesar.edu.backend.dtos.responses.CompraResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Compras", description = "Endpoints para gerenciamento de matrículas/compras de cursos pelos alunos.")
public interface CompraControllerDoc {

    @Operation(summary = "Registrar uma nova compra", description = "Vincula um aluno a um curso, registrando a data atual como data da compra.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compra registrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação, curso/aluno não encontrado, ou aluno já possui o curso."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    ResponseEntity<?> createCompra(CompraRequest compraRequest);


    @Operation(summary = "Deletar uma compra", description = "Remove o vínculo (compra) entre um aluno e um curso específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Registro de compra não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao tentar remover a compra.")
    })
    ResponseEntity<?> deleteCompra(
            @Parameter(description = "ID do curso") Long id_curso,
            @Parameter(description = "CPF do aluno") String cpf_aluno);


    @Operation(summary = "Buscar compra específica", description = "Busca os detalhes de uma compra usando o ID do curso e o CPF do aluno.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Compra não encontrada.")
    })
    ResponseEntity<?> findById(
            @Parameter(description = "ID do curso") Long id_curso,
            @Parameter(description = "CPF do aluno") String cpf_aluno);


    @Operation(summary = "Listar todas as compras", description = "Retorna uma lista com todas as compras registradas no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de compras recuperada com sucesso.")
    })
    ResponseEntity<List<CompraResponse>> findAll();


    @Operation(summary = "Listar compras de um aluno", description = "Retorna todos os cursos que um aluno específico comprou.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de compras do aluno recuperada com sucesso.")
    })
    ResponseEntity<List<CompraResponse>> findByAluno(
            @Parameter(description = "CPF do aluno") String cpf_aluno);
}