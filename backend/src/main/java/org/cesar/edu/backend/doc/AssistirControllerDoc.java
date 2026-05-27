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
import org.cesar.edu.backend.models.AssistirAula;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Assistir Aula",
        description = "Endpoints para registrar, consultar e remover aulas assistidas por alunos"
)
public interface AssistirControllerDoc {

    @Operation(
            summary = "Listar todos os registros de aulas assistidas",
            description = "Retorna todos os registros da tabela assistir, mostrando quais alunos assistiram quais aulas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registros encontrados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AssistirAula.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Erro interno no servidor\"")
                    )
            )
    })
    @GetMapping
    ResponseEntity<?> findAll();


    @Operation(
            summary = "Buscar registro de aula assistida",
            description = "Busca um registro específico informando CPF do aluno, ID do curso, ID do módulo e ID da aula."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssistirAula.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro de aula assistida não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Registro de aula assistida não encontrado.\"")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Erro interno no servidor\"")
                    )
            )
    })
    @GetMapping("/{cpf}/{id_curso}/{id_modulo}/{id_aula}")
    ResponseEntity<?> findById(
            @Parameter(
                    description = "CPF do aluno com exatamente 11 números",
                    example = "12345678901",
                    required = true
            )
            @PathVariable String cpf,

            @Parameter(
                    description = "ID do curso",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_curso,

            @Parameter(
                    description = "ID do módulo",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_modulo,

            @Parameter(
                    description = "ID da aula",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_aula
    );


    @Operation(
            summary = "Registrar aula como assistida",
            description = """
                    Registra que um aluno assistiu uma aula.
                    
                    A data assistida não precisa ser enviada no JSON,
                    pois o banco preenche automaticamente com a data atual.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Aula marcada como assistida com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Aula marcada como assistida com sucesso.\"")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou registro duplicado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "[\"O CPF do aluno deve conter exatamente 11 caracteres.\"]")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao salvar o registro",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "[\"Não foi possível registrar que o aluno assistiu a aula.\"]")
                    )
            )
    })
    @PostMapping
    ResponseEntity<?> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para registrar uma aula assistida",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssistirAula.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "cpf_aluno": "12345678901",
                                              "id_aula": 1,
                                              "id_curso": 1,
                                              "id_modulo": 1
                                            }
                                            """
                            )
                    )
            )
            @RequestBody AssistirAula assistirAula
    );


    @Operation(
            summary = "Remover registro de aula assistida",
            description = "Remove o registro que indica que determinado aluno assistiu determinada aula."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro removido com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Registro de aula assistida removido com sucesso.\"")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou registro não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "[\"Registro de aula assistida não encontrado.\"]")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "[\"Não foi possível remover o registro de aula assistida.\"]")
                    )
            )
    })
    @DeleteMapping("/{cpf}/{id_curso}/{id_modulo}/{id_aula}")
    ResponseEntity<?> delete(
            @Parameter(
                    description = "CPF do aluno com exatamente 11 números",
                    example = "12345678901",
                    required = true
            )
            @PathVariable String cpf,

            @Parameter(
                    description = "ID do curso",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_curso,

            @Parameter(
                    description = "ID do módulo",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_modulo,

            @Parameter(
                    description = "ID da aula",
                    example = "1",
                    required = true
            )
            @PathVariable Long id_aula
    );
}