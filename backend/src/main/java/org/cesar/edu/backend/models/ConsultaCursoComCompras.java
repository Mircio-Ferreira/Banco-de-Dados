package org.cesar.edu.backend.models;

import lombok.*;

@Getter @Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ConsultaCursoComCompras {
    private Long id_curso;
    private String nome_curso;
    private Double preco;
    private Long total_compras;
    private Double receita_estimada;
}
