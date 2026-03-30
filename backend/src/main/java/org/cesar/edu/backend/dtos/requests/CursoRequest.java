package org.cesar.edu.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.cesar.edu.backend.models.Categoria;
import org.cesar.edu.backend.models.Curso;

import java.util.ArrayList;
import java.util.List;

public record CursoRequest(

        @NotBlank(message = "O nome do curso é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nomeCurso,

        @NotNull(message = "O preço é obrigatório.")
        @PositiveOrZero(message = "O preço não pode ser negativo.")
        Double preco,

        @Size(max = 500, message = "A descrição não pode ultrapassar 500 caracteres.")
        String descricaoCurso,

        @NotBlank(message = "O CPF do professor responsável é obrigatório.")
        String cpfProfessor,

        List<String> categorias
) {

    public static Curso toEntity(CursoRequest dtoCurso) {
        Curso curso = new Curso();
        curso.setNome_curso(dtoCurso.nomeCurso());
        curso.setPreco(dtoCurso.preco());
        curso.setDescricao_curso(dtoCurso.descricaoCurso());

        return curso;
    }

    public static CursoRequest fromEntity(Curso curso, String cpfProfessor,List<String> categorias) {
        return new CursoRequest(
                curso.getNome_curso(),
                curso.getPreco(),
                curso.getDescricao_curso(),
                cpfProfessor,
                categorias
        );
    }
}