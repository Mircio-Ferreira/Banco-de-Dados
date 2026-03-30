package org.cesar.edu.backend.models;

import lombok.*;

import java.time.LocalDate;

@Getter  @Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Compra {
    private Long id_curso;
    private String cpf_aluno;
    private LocalDate data_compra;
}
