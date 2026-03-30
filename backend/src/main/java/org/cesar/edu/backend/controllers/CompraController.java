package org.cesar.edu.backend.controllers;

import jakarta.validation.Valid;
import org.cesar.edu.backend.doc.CompraControllerDoc;
import org.cesar.edu.backend.dtos.requests.CompraRequest;
import org.cesar.edu.backend.dtos.responses.CompraResponse;
import org.cesar.edu.backend.services.CompraService;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/compra")
public class CompraController implements CompraControllerDoc {

    private final CompraService compraService;

    @Autowired
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @Override
    @PostMapping
    public ResponseEntity<?> createCompra(@Valid @RequestBody CompraRequest compraRequest) {
        ResultService result = compraService.createCompra(compraRequest);

        if (!result.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getError().listar());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Compra registrada com sucesso!");
    }

    @Override
    @DeleteMapping("/curso/{id_curso}/aluno/{cpf_aluno}")
    public ResponseEntity<?> deleteCompra(@PathVariable Long id_curso, @PathVariable String cpf_aluno) {
        ResultService result = compraService.deleteCompra(id_curso, cpf_aluno);

        if (!result.isValid()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getError().listar());
        }

        return ResponseEntity.ok("Compra deletada com sucesso!");
    }

    @Override
    @GetMapping("/curso/{id_curso}/aluno/{cpf_aluno}")
    public ResponseEntity<?> findById(@PathVariable Long id_curso, @PathVariable String cpf_aluno) {
        CompraResponse compraResponse = compraService.findResponseById(id_curso, cpf_aluno);

        if (compraResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registro de compra não encontrado.");
        }

        return ResponseEntity.ok(compraResponse);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<CompraResponse>> findAll() {
        List<CompraResponse> compras = compraService.findAllResponses();
        return ResponseEntity.ok(compras);
    }

    @Override
    @GetMapping("/aluno/{cpf_aluno}")
    public ResponseEntity<List<CompraResponse>> findByAluno(@PathVariable String cpf_aluno) {
        List<CompraResponse> compras = compraService.findResponsesByAluno(cpf_aluno);
        return ResponseEntity.ok(compras);
    }
}