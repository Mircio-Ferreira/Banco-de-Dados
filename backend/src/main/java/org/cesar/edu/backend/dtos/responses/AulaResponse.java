package org.cesar.edu.backend.dtos.responses;

import org.cesar.edu.backend.models.Aula;

public record AulaResponse(
        String titulo,
        String link_do_video,
        String descricao
) {
    public static AulaResponse fromEntity(Aula aula) {
        return new AulaResponse(
                aula.getTitulo(),
                aula.getLink(),
                aula.getDescricao_aula()
        );
    }
}
