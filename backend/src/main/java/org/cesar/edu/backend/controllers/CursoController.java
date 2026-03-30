package org.cesar.edu.backend.controllers;

import org.cesar.edu.backend.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/curso")
public class CursoController {
    private CursoService cursoService;
    @Autowired
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

}
