package org.cesar.edu.backend.controllers;

import jakarta.validation.Valid;
import org.cesar.edu.backend.dtos.requests.CursoRequest;
import org.cesar.edu.backend.dtos.responses.CursoResponse;
import org.cesar.edu.backend.models.Curso;
import org.cesar.edu.backend.services.CursoService;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/curso")
public class CursoController {
    private CursoService cursoService;
    @Autowired
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    public ResponseEntity<?> createCurso(@Valid @RequestBody CursoRequest cursoRequest) {
        ResultService result = cursoService.createCurso(cursoRequest);

        if (!result.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getError().listar());
        }
        if(!result.isRealized()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getError().listar());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Curso criado com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCurso(@PathVariable Long id, @Valid @RequestBody CursoRequest cursoRequest) {
        ResultService result = cursoService.updateCurso(cursoRequest, id);

        if (!result.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getError().listar());
        }
        if(!result.isRealized()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getError().listar());
        }

        return ResponseEntity.ok("Curso atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCurso(@PathVariable Long id) {
        ResultService result = cursoService.delete(id);

        if (!result.isValid()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getError().listar());
        }
        if (!result.isRealized()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getError().listar());
        }

        return ResponseEntity.ok("Curso deletado com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        CursoResponse cursoResponse = cursoService.findResponseById(id);

        if (cursoResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado.");
        }

        return ResponseEntity.ok(cursoResponse);
    }

    @GetMapping
    public ResponseEntity<List<CursoResponse>> findAll() {
        List<CursoResponse> cursos = cursoService.findAllResponses();

        return ResponseEntity.ok(cursos);
    }
}
