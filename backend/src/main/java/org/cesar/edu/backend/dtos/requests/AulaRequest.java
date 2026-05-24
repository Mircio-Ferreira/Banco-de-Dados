package org.cesar.edu.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.cesar.edu.backend.models.Aula;

public record AulaRequest(
        @NotNull Long id_modulo,
        @NotNull Long id_curso,
        @NotNull @NotBlank String link_do_video,
        @NotNull @NotBlank String titulo,
        String descricao
) {
    public Aula toEntity() {
        Aula aula = new Aula();

        aula.setId_modulo(this.id_modulo);
        aula.setId_curso(this.id_curso);
        aula.setLink(this.link_do_video);
        aula.setTitulo(this.titulo);
        aula.setDescricao_aula(this.descricao);

        return aula;
    }
}
