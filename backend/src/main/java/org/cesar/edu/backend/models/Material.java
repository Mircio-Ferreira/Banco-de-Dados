package org.cesar.edu.backend.models;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    private Long id_material;
    private Long id_aula;
    private Long id_modulo;
    private Long id_curso;
    private String link_material;
    private String nome;
}
