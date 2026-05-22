package org.cesar.edu.backend.controllers;

import jakarta.validation.Valid;
import org.cesar.edu.backend.dtos.requests.CursoRequest;
import org.cesar.edu.backend.dtos.requests.DescontoGeral;
import org.cesar.edu.backend.dtos.responses.CursoResponse;
import org.cesar.edu.backend.models.*;
import org.cesar.edu.backend.services.CursoService;
import org.cesar.edu.backend.utils.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/curso")
public class CursoController implements org.cesar.edu.backend.doc.CursoControllerDoc {
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
    @GetMapping("/curso/compras")
    public ResponseEntity<?> cursosCompras() {
        try {
            List<ConsultaCursoComCompras> cursos = cursoService.cursosComCompras();
            return ResponseEntity.ok(cursos);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/curso/{id_curso}/modulos-aulas")
    public  ResponseEntity<?> cursosModulosAulas(@PathVariable Long id_curso) {
        try{
            List<ConsultaPegarModulosEAulas> pegarModulosEAulas = cursoService.pegarModulosEAulas(id_curso);
            return ResponseEntity.ok(pegarModulosEAulas);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/cursos-baratos")
    public ResponseEntity<?> cursosBaratos() {
        try{
            List<ConsultaCursoBarato> pegarCursosBaratos = cursoService.pegarCursosBaratos();
            return ResponseEntity.ok(pegarCursosBaratos);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/curso-horas-totais/{id_curso}")
    public ResponseEntity<?> cursosHorasTotais(@PathVariable Long id_curso) {
        try{
            Integer pegarHorasTotais = cursoService.pegarHorasTotais(id_curso);
            return ResponseEntity.ok(pegarHorasTotais);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/desconto-geral")
    public ResponseEntity<?> desconto(@RequestBody DescontoGeral dto) {
        try{
            boolean aplicarDescontoEmCategoria = cursoService.aplicarDescontoEmCategoria(dto.categoria(), dto.desconto());
            return ResponseEntity.ok(aplicarDescontoEmCategoria);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/resumo-geral")
    public ResponseEntity<?> resumoGeral() {
        try{
            List<ViewResumoGeralCurso> viewResumoGeralCurso = cursoService.viewResumoGeralCurso();
            return ResponseEntity.ok(viewResumoGeralCurso);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/resumo-geral/{id_curso}")
    public ResponseEntity<?> resumoGeral(@PathVariable Long id_curso) {
        try{
            List<ViewResumoGeralCurso> viewResumoGeralCursos = cursoService.viewResumoGeralCursos(id_curso);
            return ResponseEntity.ok(viewResumoGeralCursos);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/log-preco/{id_curso}")
    public ResponseEntity<?> logPreco(@PathVariable Long id_curso) {
        try{
            List<LogPrecoCurso> pegarHistoricoPrecoCurso = cursoService.pegarHistoricoPrecoCurso(id_curso);
            return ResponseEntity.ok(pegarHistoricoPrecoCurso);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
