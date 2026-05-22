package org.cesar.edu.backend.models;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaCursoBarato {
    String nome_curso;
    Long id_curso;
    BigDecimal preco;
}
