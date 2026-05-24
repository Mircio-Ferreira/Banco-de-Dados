package org.cesar.edu.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.cesar.edu.backend.models.Modulo;

public record ModuloRequest (
        @NotNull Long id_curso,
        @NotNull @NotBlank String titulo,
        @NotNull @Positive Integer carga_horaria,
        String descricao
)
{
    public Modulo toEntity() {
        Modulo modulo = new Modulo();

        modulo.setId_curso(this.id_curso);
        modulo.setTitulo(this.titulo);
        modulo.setCargaHoraria(this.carga_horaria);
        modulo.setDescricao_curso(this.descricao);

        return modulo;
    }
}
