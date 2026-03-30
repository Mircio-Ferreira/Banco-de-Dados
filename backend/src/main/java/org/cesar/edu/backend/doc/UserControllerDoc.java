package org.cesar.edu.backend.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cesar.edu.backend.dtos.requests.UserCreateRequest;
import org.cesar.edu.backend.dtos.requests.UserLoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários (Alunos e Professores) e Autenticação.")
public interface UserControllerDoc {


    @Operation(
            summary = "Buscar Usuário Genérico por CPF",
            description = "Busca um usuário pelo CPF, identifica automaticamente se é Aluno ou Professor, e retorna o perfil completo. O DTO de resposta inclui o campo 'tipoUsuario' ('ALUNO' ou 'PROFESSOR') para facilitar a renderização no Front-end."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso. Retorna o perfil completo e seu tipo."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado na base de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor ao buscar o usuário.")
    })

    ResponseEntity<?> buscarUsuarioGenerico(
            @Parameter(description = "CPF do usuário (Aluno ou Professor)") @PathVariable String cpf);
    @Operation(summary = "Efetuar Login", description = "Autentica um usuário (Aluno ou Professor) retornando seus dados correspondentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso."),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos.")
    })
    ResponseEntity<?> efetuarLogin(@RequestBody UserLoginRequest dto);

    // ======================== PROFESSOR ========================

    @Operation(summary = "Criar Professor", description = "Cadastra um novo usuário com perfil de Professor, incluindo seus telefones e certificados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Professor criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos dados enviados."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao tentar salvar o professor.")
    })
    ResponseEntity<?> createProfessor(@RequestBody UserCreateRequest dto);

    @Operation(summary = "Listar Professores", description = "Retorna uma lista contendo as informações básicas de todos os professores cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de professores recuperada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Nenhum professor encontrado.")
    })
    ResponseEntity<?> listarProfessor();

    @Operation(summary = "Buscar Professor por CPF", description = "Busca o perfil completo de um professor específico, incluindo os cursos que leciona.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    ResponseEntity<?> pegarProfessor(
            @Parameter(description = "CPF do professor") @PathVariable String cpf);

    @Operation(summary = "Atualizar Professor", description = "Atualiza os dados cadastrais de um professor. É necessário confirmar a identidade pelo cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos dados enviados."),
            @ApiResponse(responseCode = "403", description = "Acesso negado: o CPF logado não corresponde ao perfil que está sendo alterado."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao atualizar.")
    })
    ResponseEntity<?> atualizarProfessor(
            @RequestBody UserCreateRequest dto,
            @Parameter(description = "CPF do professor a ser alterado") @PathVariable String cpf,
            @Parameter(description = "CPF do usuário logado", required = false) @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado);

    @Operation(summary = "Deletar Professor", description = "Remove o cadastro de um professor e todos os seus vínculos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor deletado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Professor não encontrado para exclusão."),
            @ApiResponse(responseCode = "403", description = "Acesso negado: o CPF logado não tem permissão para excluir esta conta."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao deletar.")
    })
    ResponseEntity<?> deletarProfessor(
            @Parameter(description = "CPF do professor a ser deletado") @PathVariable String cpf,
            @Parameter(description = "CPF do usuário logado", required = false) @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado);

    // ======================== ALUNO ========================

    @Operation(summary = "Criar Aluno", description = "Cadastra um novo usuário com perfil de Aluno, incluindo seus telefones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos dados enviados."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao tentar salvar o aluno.")
    })
    ResponseEntity<?> criarAluno(@RequestBody UserCreateRequest dto);

    @Operation(summary = "Listar Alunos", description = "Retorna uma lista contendo as informações básicas de todos os alunos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos recuperada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Nenhum aluno encontrado.")
    })
    ResponseEntity<?> listarAlunos();

    @Operation(summary = "Buscar Aluno por CPF", description = "Busca o perfil completo de um aluno específico, incluindo seus cursos comprados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado.")
    })
    ResponseEntity<?> pegarAluno(
            @Parameter(description = "CPF do aluno") @PathVariable String cpf);

    @Operation(summary = "Atualizar Aluno", description = "Atualiza os dados cadastrais de um aluno. É necessário confirmar a identidade pelo cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do aluno atualizados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos dados enviados."),
            @ApiResponse(responseCode = "403", description = "Acesso negado: o CPF logado não corresponde ao perfil que está sendo alterado."),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para atualização.")
    })
    ResponseEntity<?> atualizarAluno(
            @Parameter(description = "CPF do aluno a ser alterado") @PathVariable String cpf,
            @RequestBody UserCreateRequest dto,
            @Parameter(description = "CPF do usuário logado", required = false) @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado);

    @Operation(summary = "Deletar Aluno", description = "Remove o cadastro de um aluno e todos os seus vínculos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno deletado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação."),
            @ApiResponse(responseCode = "403", description = "Acesso negado: o CPF logado não tem permissão para excluir esta conta."),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para exclusão.")
    })
    ResponseEntity<?> deletarAluno(
            @Parameter(description = "CPF do aluno a ser deletado") @PathVariable String cpf,
            @Parameter(description = "CPF do usuário logado", required = false) @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado);
}