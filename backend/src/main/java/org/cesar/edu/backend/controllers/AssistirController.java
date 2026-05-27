package org.cesar.edu.backend.controllers;

import org.cesar.edu.backend.doc.AssistirControllerDoc;
import org.cesar.edu.backend.models.AssistirAula;
import org.cesar.edu.backend.services.AssistirAulaService;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assistir")
public class AssistirController implements AssistirControllerDoc {

    private final AssistirAulaService assistirAulaService;

    @Autowired
    public AssistirController(AssistirAulaService assistirAulaService) {
        this.assistirAulaService = assistirAulaService;
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(assistirAulaService.findAll());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{cpf}/{id_curso}/{id_modulo}/{id_aula}")
    public ResponseEntity<?> findById(
            @PathVariable String cpf,
            @PathVariable Long id_curso,
            @PathVariable Long id_modulo,
            @PathVariable Long id_aula
    ) {
        try {
            AssistirAula assistirAula = assistirAulaService.findById(
                    cpf,
                    id_aula,
                    id_modulo,
                    id_curso
            );

            if (assistirAula == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Registro de aula assistida não encontrado.");
            }

            return ResponseEntity.ok(assistirAula);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody AssistirAula assistirAula) {
        try {
            ResultService result = assistirAulaService.save(assistirAula);

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
                    .status(HttpStatus.CREATED)
                    .body("Aula marcada como assistida com sucesso.");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{cpf}/{id_curso}/{id_modulo}/{id_aula}")
    public ResponseEntity<?> delete(
            @PathVariable String cpf,
            @PathVariable Long id_curso,
            @PathVariable Long id_modulo,
            @PathVariable Long id_aula
    ) {
        try {
            ResultService result = assistirAulaService.delete(
                    cpf,
                    id_aula,
                    id_modulo,
                    id_curso
            );

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
                    .ok("Registro de aula assistida removido com sucesso.");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}