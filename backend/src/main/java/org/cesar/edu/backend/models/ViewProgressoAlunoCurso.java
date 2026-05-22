package org.cesar.edu.backend.models;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViewProgressoAlunoCurso {
    private String cpfAluno;
    private Long idCurso;
    private String nomeCurso;
    private Long totalAulas;
    private Long aulasAssistidas;
    private BigDecimal percentualConclusao;
}
