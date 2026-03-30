package org.cesar.edu.backend.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cesar.edu.backend.dtos.requests.CursoRequest;
import org.cesar.edu.backend.dtos.responses.CursoResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Cursos", description = "Endpoints para gerenciamento de Cursos, incluindo professores e categorias.")
public interface CursoControllerDoc {

    @Operation(summary = "Cadastrar um novo curso", description = "Cria um novo curso no sistema, vinculando as categorias informadas e o professor responsável.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Curso criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos dados enviados (ex: campos obrigatórios vazios)."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor ao tentar salvar no banco de dados.")
    })
    ResponseEntity<?> createCurso(CursoRequest cursoRequest);


    @Operation(summary = "Atualizar um curso", description = "Atualiza os dados de um curso existente através do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, ID não encontrado ou professor inexistente."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor ao tentar atualizar.")
    })
    ResponseEntity<?> updateCurso(
            @Parameter(description = "ID do curso a ser atualizado") Long id,
            CursoRequest cursoRequest);


    @Operation(summary = "Deletar um curso", description = "Remove um curso do sistema e limpa todos os seus vínculos com professores e categorias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso deletado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro interno ao tentar remover os vínculos do curso."),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado para o ID informado.")
    })
    ResponseEntity<?> deleteCurso(
            @Parameter(description = "ID do curso a ser deletado") Long id);


    @Operation(summary = "Buscar curso por ID", description = "Retorna os detalhes completos de um curso específico, incluindo suas categorias e professor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado.")
    })
    ResponseEntity<?> findById(
            @Parameter(description = "ID do curso a ser buscado") Long id);


    @Operation(summary = "Listar todos os cursos", description = "Retorna uma lista com todos os cursos cadastrados no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos recuperada com sucesso (pode retornar lista vazia).")
    })
    ResponseEntity<List<CursoResponse>> findAll();
}