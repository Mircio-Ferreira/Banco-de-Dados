package org.cesar.edu.backend.controllers;

import org.cesar.edu.backend.doc.ModuloControllerDoc;
import org.cesar.edu.backend.dtos.requests.ModuloRequest;
import org.cesar.edu.backend.dtos.responses.ModuloResponse;
import org.cesar.edu.backend.models.Modulo;
import org.cesar.edu.backend.services.ModuloService;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modulo")
public class ModuloController implements ModuloControllerDoc {

    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(moduloService.findAll());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar módulos: " + e.getMessage());
        }
    }

    @GetMapping("/{id_curso}/{id_modulo}")
    public ResponseEntity<?> findById(
            @PathVariable("id_curso") Long idCurso,
            @PathVariable("id_modulo") Long idModulo
    ) {
        try {
            ModuloResponse modulo = moduloService.findById(idCurso, idModulo);
            return ResponseEntity.ok(modulo);
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

    @GetMapping("/{id_curso}/{titulo}")
    public ResponseEntity<?> findByTitulo(@PathVariable String titulo, @PathVariable Long id_curso) {
        try{
            Modulo modulo = moduloService.findByTitulo(titulo, id_curso);
            if(modulo == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consulta não encontrada");
            }
            return ResponseEntity.ok(modulo);
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ModuloRequest request) {
        ResultService result = moduloService.save(request);

        return montarResposta(
                result,
                HttpStatus.CREATED,
                "Módulo cadastrado com sucesso."
        );
    }

    @PutMapping("/update/{id_curso}/{id_modulo}")
    public ResponseEntity<?> update(
            @PathVariable("id_curso") Long idCurso,
            @PathVariable("id_modulo") Long idModulo,
            @RequestBody ModuloRequest request
    ) {
        ResultService result = moduloService.update(idCurso, idModulo, request);

        return montarResposta(
                result,
                HttpStatus.OK,
                "Módulo atualizado com sucesso."
        );
    }

    @DeleteMapping("/delete/{id_curso}/{id_modulo}")
    public ResponseEntity<?> delete(
            @PathVariable("id_curso") Long idCurso,
            @PathVariable("id_modulo") Long idModulo
    ) {
        ResultService result = moduloService.delete(idCurso, idModulo);

        return montarResposta(
                result,
                HttpStatus.OK,
                "Módulo deletado com sucesso."
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