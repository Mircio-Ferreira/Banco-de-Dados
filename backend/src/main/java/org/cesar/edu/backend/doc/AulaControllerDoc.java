package org.cesar.edu.backend.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cesar.edu.backend.dtos.requests.AulaRequest;
import org.cesar.edu.backend.dtos.responses.AulaResponse;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Aulas",
        description = "Endpoints responsáveis pelo gerenciamento das aulas dos módulos"
)
public interface AulaControllerDoc {

    @Operation(
            summary = "Listar todas as aulas",
            description = """
                    Retorna todas as aulas cadastradas no sistema.

                    Cada aula pertence a um módulo, e cada módulo pertence a um curso.
                    A resposta contém informações como título, link do vídeo e descrição da aula.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Aulas listadas com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AulaResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar listar as aulas.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Erro ao listar aulas: mensagem do erro"
                            )
                    )
            )
    })
    ResponseEntity<?> findAll();

    @Operation(
            summary = "Buscar aula por ID do curso, ID do módulo e ID da aula",
            description = """
                    Busca uma aula específica utilizando os três identificadores principais:

                    - id_curso: curso ao qual o módulo pertence.
                    - id_modulo: módulo ao qual a aula pertence.
                    - id_aula: aula que será buscada.

                    Como a tabela aula possui chave primária composta por id_aula, id_curso e id_modulo,
                    esses valores são usados para localizar a aula corretamente.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Aula encontrada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AulaResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID do curso, módulo ou aula inválido.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "O id da aula deve ser maior que zero."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aula não encontrada.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Aula não encontrada."
                            )
                    )
            )
    })
    ResponseEntity<?> findById(
            @Parameter(
                    description = "ID do curso ao qual o módulo da aula pertence.",
                    example = "1",
                    required = true
            )
            Long idCurso,

            @Parameter(
                    description = "ID do módulo ao qual a aula pertence.",
                    example = "2",
                    required = true
            )
            Long idModulo,

            @Parameter(
                    description = "ID da aula que será buscada.",
                    example = "3",
                    required = true
            )
            Long idAula
    );

    @Operation(
            summary = "Cadastrar nova aula",
            description = """
                    Cadastra uma nova aula dentro de um módulo de um curso.

                    Regras aplicadas:
                    - O curso informado precisa ser válido.
                    - O módulo informado precisa existir dentro do curso.
                    - O título da aula é obrigatório.
                    - O título precisa ter entre 3 e 255 caracteres.
                    - O link do vídeo é obrigatório.
                    - O link do vídeo precisa ter no máximo 255 caracteres.
                    - O link deve começar com http:// ou https://.
                    - Não pode existir outra aula com o mesmo título exatamente igual dentro do mesmo módulo.
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados necessários para cadastrar uma aula.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AulaRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "id_modulo": 1,
                                      "id_curso": 1,
                                      "link_do_video": "https://www.youtube.com/watch?v=abc123",
                                      "titulo": "Introdução à Aula",
                                      "descricao": "Primeira aula do módulo"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Aula cadastrada com sucesso.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Aula cadastrada com sucesso."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou regra de negócio violada.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "O módulo informado não existe para este curso.",
                                              "O link do vídeo deve começar com http:// ou https://."
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar cadastrar a aula.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Não foi possível salvar a aula."
                                            ]
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> save(AulaRequest request);

    @Operation(
            summary = "Atualizar aula",
            description = """
                    Atualiza os dados de uma aula existente.

                    A aula é localizada pelo ID do curso, ID do módulo e ID da aula.

                    Regras aplicadas:
                    - O ID do curso da URL precisa ser igual ao ID do curso enviado no corpo da requisição.
                    - O ID do módulo da URL precisa ser igual ao ID do módulo enviado no corpo da requisição.
                    - A aula precisa existir.
                    - O módulo precisa existir dentro do curso.
                    - O título da aula é obrigatório.
                    - O título precisa ter entre 3 e 255 caracteres.
                    - O link do vídeo é obrigatório.
                    - O link deve começar com http:// ou https://.
                    - Não pode existir outra aula com o mesmo título exatamente igual dentro do mesmo módulo.
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados da aula.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AulaRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "id_modulo": 1,
                                      "id_curso": 1,
                                      "link_do_video": "https://www.youtube.com/watch?v=xyz789",
                                      "titulo": "Aula Atualizada",
                                      "descricao": "Descrição atualizada da aula"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Aula atualizada com sucesso.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Aula atualizada com sucesso."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou regra de negócio violada.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "O id_curso da URL precisa ser igual ao id_curso do corpo da requisição.",
                                              "O id_modulo da URL precisa ser igual ao id_modulo do corpo da requisição."
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar atualizar a aula.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Não foi possível atualizar a aula."
                                            ]
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> update(
            @Parameter(
                    description = "ID do curso ao qual o módulo da aula pertence.",
                    example = "1",
                    required = true
            )
            Long idCurso,

            @Parameter(
                    description = "ID do módulo ao qual a aula pertence.",
                    example = "2",
                    required = true
            )
            Long idModulo,

            @Parameter(
                    description = "ID da aula que será atualizada.",
                    example = "3",
                    required = true
            )
            Long idAula,

            AulaRequest request
    );

    @Operation(
            summary = "Deletar aula",
            description = """
                    Remove uma aula específica a partir do ID do curso, ID do módulo e ID da aula.

                    A exclusão remove somente a aula informada.
                    Não remove o módulo nem o curso relacionados.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Aula deletada com sucesso.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Aula deletada com sucesso."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID do curso, módulo ou aula inválido, ou aula não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Aula não encontrada para exclusão."
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar deletar a aula.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Não foi possível deletar a aula."
                                            ]
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> delete(
            @Parameter(
                    description = "ID do curso ao qual o módulo da aula pertence.",
                    example = "1",
                    required = true
            )
            Long idCurso,

            @Parameter(
                    description = "ID do módulo ao qual a aula pertence.",
                    example = "2",
                    required = true
            )
            Long idModulo,

            @Parameter(
                    description = "ID da aula que será deletada.",
                    example = "3",
                    required = true
            )
            Long idAula
    );
}