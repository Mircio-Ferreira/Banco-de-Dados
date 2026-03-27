package org.cesar.edu.backend.models;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Aula {
    private Long id_aula;
    private Long id_curso;
    private Long id_modulo;
    private String descricao_aula;
    private String link;
    private String titulo;
}
