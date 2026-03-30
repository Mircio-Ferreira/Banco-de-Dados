package org.cesar.edu.backend.controllers;

import org.cesar.edu.backend.doc.UserControllerDoc;
import org.cesar.edu.backend.dtos.requests.UserCreateRequest;
import org.cesar.edu.backend.dtos.requests.UserLoginRequest;
import org.cesar.edu.backend.dtos.responses.UserResponse;
import org.cesar.edu.backend.models.Aluno;
import org.cesar.edu.backend.models.Professor;
import org.cesar.edu.backend.services.UserService;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDoc {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/{cpf}")
    public ResponseEntity<?> buscarUsuarioGenerico(@PathVariable String cpf) {
        try {
            UserResponse response = userService.buscarUsuarioGenericoPorCpf(cpf);

            if (response == null) {
                return ResponseEntity.status(404).body("Usuário não encontrado.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(@RequestBody UserLoginRequest dto) {
        UserResponse response = userService.realizarLogin(dto);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("E-mail ou senha incorretos.");
    }

    @PostMapping("/professor/create")
    public ResponseEntity<?> createProfessor(@RequestBody UserCreateRequest dto) {
        ResultService result = userService.criarProfessor(dto);
        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(500).body(result.getError().listar());
        }
        return ResponseEntity.status(201).body("Professor criado com sucesso!");
    }

    @GetMapping("/professor")
    public ResponseEntity<?> listarProfessor() {
        List<Professor> professores = userService.listarProfessores();
        if (professores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<UserResponse> dtos = new ArrayList<>();
        for(Professor p : professores) {
            dtos.add(UserResponse.fromProfessor(p,null));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/professor/{cpf}")
    public ResponseEntity<?> pegarProfessor(@PathVariable String cpf) {
        try {
            UserResponse response = userService.pegarPorCpfProfessor(cpf);

            if (response == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/professor/{cpf}")
    public ResponseEntity<?> atualizarProfessor(
            @RequestBody UserCreateRequest dto,
            @PathVariable String cpf,
            @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado) {

        if (isAcessoNegado(cpf, cpfLogado)) {
            return ResponseEntity.status(403).body("Acesso negado: Você só pode alterar o seu próprio perfil.");
        }

        ResultService result = userService.atualizarProfessor(dto, cpf);
        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(500).body(result.getError().listar());
        }
        return ResponseEntity.ok().body("Professor atualizado com sucesso!");
    }

    @DeleteMapping("/professor/{cpf}")
    public ResponseEntity<?> deletarProfessor(
            @PathVariable String cpf,
            @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado) {

        if (isAcessoNegado(cpf, cpfLogado)) {
            return ResponseEntity.status(403).body("Acesso negado: Você não tem permissão para excluir esta conta.");
        }

        ResultService result = userService.deletarProfessor(cpf);
        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(500).body(result.getError().listar());
        }
        return ResponseEntity.ok().body("Professor deletado com sucesso!");
    }

    @PostMapping("/aluno/create")
    public ResponseEntity<?> criarAluno(@RequestBody UserCreateRequest dto) {
        ResultService result = userService.criarAluno(dto);
        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(500).body(result.getError().listar());
        }
        return ResponseEntity.status(201).body("Aluno criado com sucesso!");
    }

    @GetMapping("/aluno")
    public ResponseEntity<?> listarAlunos() {
        List<Aluno> alunos = userService.listarAlunos();
        if (alunos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<UserResponse> dtos = new ArrayList<>();
        for(Aluno a : alunos) {
            dtos.add(UserResponse.fromAluno(a,null));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/aluno/{cpf}")
    public ResponseEntity<?> pegarAluno(@PathVariable String cpf) {
        UserResponse response = userService.pegarPorCpfAluno(cpf);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/aluno/{cpf}")
    public ResponseEntity<?> atualizarAluno(
            @PathVariable String cpf,
            @RequestBody UserCreateRequest dto,
            @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado) {

        if (isAcessoNegado(cpf, cpfLogado)) {
            return ResponseEntity.status(403).body("Acesso negado: Você só pode alterar o seu próprio perfil.");
        }

        ResultService result = userService.atualizarAluno(dto, cpf);
        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(404).body(result.getError().listar());
        }
        return ResponseEntity.ok().body("Dados do aluno atualizados com sucesso!");
    }

    @DeleteMapping("/aluno/{cpf}")
    public ResponseEntity<?> deletarAluno(
            @PathVariable String cpf,
            @RequestHeader(value = "X-User-CPF", required = false) String cpfLogado) {

        if (isAcessoNegado(cpf, cpfLogado)) {
            return ResponseEntity.status(403).body("Acesso negado: Você não tem permissão para excluir esta conta.");
        }

        ResultService result = userService.deletarAluno(cpf);
        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(404).body(result.getError().listar());
        }
        return ResponseEntity.ok().body("Aluno deletado com sucesso!");
    }

    private boolean isAcessoNegado(String cpfAlvo, String cpfLogado) {
        return cpfLogado == null || !cpfLogado.equals(cpfAlvo);
    }
}