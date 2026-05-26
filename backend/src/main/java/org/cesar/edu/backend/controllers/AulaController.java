package org.cesar.edu.backend.controllers;

import org.cesar.edu.backend.doc.AulaControllerDoc;
import org.cesar.edu.backend.dtos.requests.AulaRequest;
import org.cesar.edu.backend.dtos.responses.AulaResponse;
import org.cesar.edu.backend.models.Aula;
import org.cesar.edu.backend.services.AulaService;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/aula")
public class AulaController implements AulaControllerDoc {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(aulaService.findAll());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar aulas: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id_curso}/{id_modulo}/{id_aula}")
    public ResponseEntity<?> findById(
            @PathVariable("id_curso") Long idCurso,
            @PathVariable("id_modulo") Long idModulo,
            @PathVariable("id_aula") Long idAula
    ) {
        try {
            AulaResponse aula = aulaService.findById(idAula, idModulo, idCurso);
            return ResponseEntity.ok(aula);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id_curso}/{id_modulo}/{titulo}")
    public ResponseEntity<?> findByTitulo(
            @PathVariable Long id_curso,
            @PathVariable Long id_modulo,
            @PathVariable String titulo
    ) {
        try {
            Aula aula = aulaService.findByTitulo(titulo, id_curso, id_modulo);

            if (aula == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Aula não encontrada");
            }

            return ResponseEntity.ok(aula);
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody AulaRequest request) {
        ResultService result = aulaService.save(request);

        return montarResposta(
                result,
                HttpStatus.CREATED,
                "Aula cadastrada com sucesso."
        );
    }

    @PutMapping("/update/{id_curso}/{id_modulo}/{id_aula}")
    public ResponseEntity<?> update(
            @PathVariable("id_curso") Long idCurso,
            @PathVariable("id_modulo") Long idModulo,
            @PathVariable("id_aula") Long idAula,
            @RequestBody AulaRequest request
    ) {
        ResultService result = aulaService.update(idAula, idModulo, idCurso, request);

        return montarResposta(
                result,
                HttpStatus.OK,
                "Aula atualizada com sucesso."
        );
    }

    @DeleteMapping("/delete/{id_curso}/{id_modulo}/{id_aula}")
    public ResponseEntity<?> delete(
            @PathVariable("id_curso") Long idCurso,
            @PathVariable("id_modulo") Long idModulo,
            @PathVariable("id_aula") Long idAula
    ) {
        ResultService result = aulaService.delete(idAula, idModulo, idCurso);

        return montarResposta(
                result,
                HttpStatus.OK,
                "Aula deletada com sucesso."
        );
    }

    private ResponseEntity<?> montarResposta(
            ResultService result,
            HttpStatus statusSucesso,
            String mensagemSucesso
    ) {
        if (!result.isValid()) {
            return ResponseEntity
                    .badRequest()
                    .body(result.getError().listar());
        }

        if (!result.isRealized()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(result.getError().listar());
        }

        return ResponseEntity
                .status(statusSucesso)
                .body(mensagemSucesso);
    }
}