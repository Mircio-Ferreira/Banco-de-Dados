package org.cesar.edu.backend.models;

import lombok.*;

import java.util.List;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Curso{
    private Long id_curso;
    private String nome_curso;
    private Double preco;
    private String descricao_curso;
}
