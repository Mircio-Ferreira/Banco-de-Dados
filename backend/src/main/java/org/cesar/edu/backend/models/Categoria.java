package org.cesar.edu.backend.models;

import lombok.*;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Long id_categoria;
    private String nome;
}
