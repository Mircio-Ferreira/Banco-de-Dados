package org.cesar.edu.backend.models;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Leciona {
    private String cpf_professor;
    private Long id_curso;
}
