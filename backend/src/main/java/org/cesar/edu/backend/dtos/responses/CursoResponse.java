package org.cesar.edu.backend.dtos.responses;

import org.cesar.edu.backend.models.Categoria;
import org.cesar.edu.backend.models.Curso;
import org.cesar.edu.backend.models.Leciona;

import java.util.List;

public record CursoResponse(
        Long id_curso,
        String nome_curso,
        Double preco,
        String descricao_curso,
        List<Categoria>categorias,
        List<Leciona> lecionas
) {
    public static CursoResponse fromEntity(Curso curso, List<Categoria> categorias, List<Leciona> lecionas) {
        return new CursoResponse(
                curso.getId_curso(),
                curso.getNome_curso(),
                curso.getPreco(),
                curso.getDescricao_curso(),
                categorias,
                lecionas
        );
    }
}
