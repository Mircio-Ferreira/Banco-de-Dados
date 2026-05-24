package org.cesar.edu.backend.models;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Modulo {
    private Long id_modulo;
    private Long id_curso;
    private String titulo;
    private Integer cargaHoraria;
    private String descricao_curso;
}
