package org.cesar.edu.backend.models;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ViewResumoGeralCurso {

    private Long idCurso;
    private String nomeCurso;
    private BigDecimal preco;
    private Long totalCompras;
    private BigDecimal receitaEstimada;
    private BigDecimal mediaPrecoGeral;
    private String classificacaoPreco;
}
