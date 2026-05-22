package org.cesar.edu.backend.models;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogPrecoCurso {
    private Long idLog;
    private Long idCurso;
    private BigDecimal precoAntigo;
    private BigDecimal precoNovo;
    private LocalDateTime dataAlteracao;
    private String usuarioBanco;
}
