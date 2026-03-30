package org.cesar.edu.backend.dtos.responses;

import org.cesar.edu.backend.models.Compra;

import java.time.LocalDate;

public record CompraResponse(
        Long id_curso,
        String cpf_aluno,
        LocalDate data_compra
) {
    public static CompraResponse fromEntity(Compra compra) {
        return new CompraResponse(
                compra.getId_curso(),
                compra.getCpf_aluno(),
                compra.getData_compra()
        );
    }
}