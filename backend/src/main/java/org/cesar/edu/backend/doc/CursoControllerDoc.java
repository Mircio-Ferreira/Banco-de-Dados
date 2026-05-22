package org.cesar.edu.backend.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cesar.edu.backend.dtos.requests.CursoRequest;
import org.cesar.edu.backend.dtos.requests.DescontoGeral;
import org.cesar.edu.backend.dtos.responses.CursoResponse;
import org.cesar.edu.backend.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

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
    // ======================== CONSULTAS / RELATÓRIOS DE CURSOS ========================

    @Operation(
            summary = "Listar Cursos com Compras",
            description = """
                Retorna os cursos que possuem compras registradas.

                Esse endpoint é útil para identificar quais cursos já foram adquiridos por alunos.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de cursos com compras retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ConsultaCursoComCompras.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao buscar cursos com compras.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> cursosCompras();


    @Operation(
            summary = "Listar Módulos e Aulas de um Curso",
            description = """
                Retorna os módulos e aulas pertencentes a um curso específico.

                O curso é buscado pelo ID informado na URL.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Módulos e aulas do curso retornados com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ConsultaPegarModulosEAulas.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao buscar módulos e aulas do curso.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> cursosModulosAulas(
            @Parameter(
                    description = "ID do curso que terá seus módulos e aulas consultados.",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_curso
    );


    @Operation(
            summary = "Listar Cursos Baratos",
            description = """
                Retorna uma lista de cursos considerados baratos de acordo com a regra definida no banco ou no serviço.

                Esse endpoint pode ser usado para exibir cursos promocionais ou cursos com menor preço.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de cursos baratos retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ConsultaCursoBarato.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao buscar cursos baratos.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> cursosBaratos();


    @Operation(
            summary = "Buscar Horas Totais de um Curso",
            description = """
                Retorna a quantidade total de horas de um curso específico.

                O cálculo é feito a partir do ID do curso informado na URL.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quantidade total de horas retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao calcular as horas totais do curso.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> cursosHorasTotais(
            @Parameter(
                    description = "ID do curso que terá a carga horária total consultada.",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_curso
    );


    @Operation(
            summary = "Aplicar Desconto Geral por Categoria",
            description = """
                Aplica um desconto em todos os cursos pertencentes a uma determinada categoria.

                O corpo da requisição deve conter:
                - categoria;
                - desconto.

                Retorna true caso a operação seja realizada com sucesso.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Desconto aplicado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao aplicar desconto.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> desconto(
            @RequestBody DescontoGeral dto
    );


    @Operation(
            summary = "Listar Resumo Geral dos Cursos",
            description = """
                Retorna um resumo geral de todos os cursos.

                Esse endpoint pode trazer informações agregadas dos cursos, como dados de compra,
                aulas, módulos, progresso ou outros indicadores definidos na view do banco.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resumo geral dos cursos retornado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ViewResumoGeralCurso.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao buscar o resumo geral dos cursos.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> resumoGeral();


    @Operation(
            summary = "Buscar Resumo Geral de um Curso por ID",
            description = """
                Retorna o resumo geral de um curso específico.

                O curso é filtrado pelo ID informado na URL.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resumo geral do curso retornado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ViewResumoGeralCurso.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao buscar o resumo geral do curso.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> resumoGeral(
            @Parameter(
                    description = "ID do curso que terá o resumo geral consultado.",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_curso
    );


    @Operation(
            summary = "Listar Histórico de Preço de um Curso",
            description = """
                Retorna o histórico de alterações de preço de um curso específico.

                Esse endpoint consulta os registros gerados pela tabela de log de preço do curso.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Histórico de preço do curso retornado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = LogPrecoCurso.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao buscar o histórico de preço do curso.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    ResponseEntity<?> logPreco(
            @Parameter(
                    description = "ID do curso que terá o histórico de preço consultado.",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_curso
    );
}