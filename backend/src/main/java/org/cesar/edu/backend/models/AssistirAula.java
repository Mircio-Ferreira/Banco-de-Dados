package org.cesar.edu.backend.models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AssistirAula {
    private Long id_aula;
    private Long id_curso;
    private Long id_modulo;
    private String cpf_aluno;
    private LocalDate data_assistida;
}
