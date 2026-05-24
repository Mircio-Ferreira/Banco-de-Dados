package org.cesar.edu.backend.dtos.responses;

import org.cesar.edu.backend.models.Modulo;

public record ModuloResponse(
        String titulo,
        Integer carga_horaria,
        String descricao
) {
    public static ModuloResponse fromEntity(Modulo modulo) {
        return new ModuloResponse(
                modulo.getTitulo(),
                modulo.getCargaHoraria(),
                modulo.getDescricao_curso()
        );
    }
}
