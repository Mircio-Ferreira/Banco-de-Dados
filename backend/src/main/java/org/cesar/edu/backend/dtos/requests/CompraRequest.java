package org.cesar.edu.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.cesar.edu.backend.models.Compra;

public record CompraRequest(
        @NotNull(message = "O ID do curso é obrigatório.")
        Long id_curso,

        @NotBlank(message = "O CPF do aluno é obrigatório.")
        String cpf_aluno
) {
    public static Compra toEntity(CompraRequest dto) {
        Compra compra = new Compra();
        compra.setId_curso(dto.id_curso());
        compra.setCpf_aluno(dto.cpf_aluno());
        return compra;
    }
}