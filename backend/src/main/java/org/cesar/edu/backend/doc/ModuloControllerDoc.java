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
import org.cesar.edu.backend.dtos.requests.ModuloRequest;
import org.cesar.edu.backend.dtos.responses.ModuloResponse;
import org.cesar.edu.backend.models.Modulo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(
        name = "Módulos",
        description = "Endpoints responsáveis pelo gerenciamento dos módulos dos cursos"
)
public interface ModuloControllerDoc {

    @Operation(
            summary = "Listar todos os módulos",
            description = """
                    Retorna todos os módulos cadastrados no sistema.
                    
                    Cada módulo pertence a um curso e possui título, carga horária e descrição.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Módulos listados com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ModuloResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar listar os módulos.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Erro ao listar módulos: mensagem do erro"
                            )
                    )
            )
    })
    ResponseEntity<?> findAll();

    @Operation(
            summary = "Buscar módulo por ID do curso e ID do módulo",
            description = """
                    Busca um módulo específico a partir do ID do curso e do ID do módulo.
                    
                    Como a tabela módulo possui chave primária composta por id_curso e id_modulo,
                    os dois valores são necessários para localizar corretamente o registro.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Módulo encontrado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ModuloResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID do curso ou ID do módulo inválido.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "O id do curso deve ser maior que zero."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Módulo não encontrado.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Módulo não encontrado."
                            )
                    )
            )
    })
    ResponseEntity<?> findById(
            @Parameter(
                    description = "ID do curso ao qual o módulo pertence.",
                    example = "1",
                    required = true
            )
            Long idCurso,

            @Parameter(
                    description = "ID do módulo que será buscado.",
                    example = "2",
                    required = true
            )
            Long idModulo
    );

    @Operation(
            summary = "Cadastrar novo módulo",
            description = """
                    Cadastra um novo módulo vinculado a um curso existente.
                    
                    Regras aplicadas:
                    - O curso informado precisa existir.
                    - O título do módulo é obrigatório.
                    - A carga horária deve ser maior que zero.
                    - Não deve existir outro módulo com o mesmo título exatamente igual dentro do mesmo curso.
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados necessários para cadastrar um módulo.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModuloRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "id_curso": 1,
                                      "titulo": "Introdução ao Backend",
                                      "carga_horaria": 20,
                                      "descricao": "Módulo inicial do curso"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Módulo cadastrado com sucesso.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Módulo cadastrado com sucesso."
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
                                              "O título do módulo é obrigatório.",
                                              "A carga horária deve ser maior que zero."
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar cadastrar o módulo.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Não foi possível salvar o módulo."
                                            ]
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> save(ModuloRequest request);

    @Operation(
            summary = "Atualizar módulo",
            description = """
                    Atualiza os dados de um módulo existente.
                    
                    O módulo é localizado pelo ID do curso e pelo ID do módulo.
                    
                    Regras aplicadas:
                    - O ID do curso da URL precisa ser igual ao ID do curso enviado no corpo da requisição.
                    - O módulo precisa existir.
                    - O título é obrigatório.
                    - A carga horária deve ser maior que zero.
                    - Não deve existir outro módulo com o mesmo título exatamente igual dentro do mesmo curso.
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados do módulo.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModuloRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "id_curso": 1,
                                      "titulo": "Backend com Java",
                                      "carga_horaria": 30,
                                      "descricao": "Módulo atualizado"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Módulo atualizado com sucesso.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Módulo atualizado com sucesso."
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
                                              "O id_curso da URL precisa ser igual ao id_curso do corpo da requisição."
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar atualizar o módulo.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Não foi possível atualizar o módulo."
                                            ]
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> update(
            @Parameter(
                    description = "ID do curso ao qual o módulo pertence.",
                    example = "1",
                    required = true
            )
            Long idCurso,

            @Parameter(
                    description = "ID do módulo que será atualizado.",
                    example = "2",
                    required = true
            )
            Long idModulo,

            ModuloRequest request
    );

    @Operation(
            summary = "Deletar módulo",
            description = """
                    Remove um módulo específico a partir do ID do curso e do ID do módulo.
                    
                    Caso a tabela de aulas esteja configurada com chave estrangeira usando ON DELETE CASCADE,
                    as aulas relacionadas a esse módulo também serão removidas automaticamente pelo banco.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Módulo deletado com sucesso.",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Módulo deletado com sucesso."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID do curso ou ID do módulo inválido, ou módulo não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Módulo não encontrado para exclusão."
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao tentar deletar o módulo.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class)
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              "Não foi possível deletar o módulo."
                                            ]
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> delete(
            @Parameter(
                    description = "ID do curso ao qual o módulo pertence.",
                    example = "1",
                    required = true
            )
            Long idCurso,

            @Parameter(
                    description = "ID do módulo que será deletado.",
                    example = "2",
                    required = true
            )
            Long idModulo
    );
    @Operation(
            summary = "Buscar módulo por título e curso",
            description = """
                    Busca um módulo específico a partir do título informado e do ID do curso ao qual ele pertence.
                    
                    Caso exista um módulo com o título informado dentro do curso especificado,
                    o sistema retorna os dados completos do módulo.
                    
                    Caso nenhum módulo seja encontrado, retorna uma mensagem informando que a consulta não foi encontrada.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Módulo encontrado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Modulo.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nenhum módulo encontrado para o título e curso informados.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "Consulta não encontrada"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao tentar buscar o módulo.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "Erro interno no servidor"
                            )
                    )
            )
    })
    ResponseEntity<?> findByTitulo(

            @Parameter(
                    description = "Título do módulo que será buscado.",
                    example = "Introdução ao Java",
                    required = true
            )
            @PathVariable String titulo,

             @Parameter(
                     description = "ID do curso ao qual o módulo pertence.",
                     example = "1",
                     required = true
             )
            @PathVariable Long id_curso
    );
}