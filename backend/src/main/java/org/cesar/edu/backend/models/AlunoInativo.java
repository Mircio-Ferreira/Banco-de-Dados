package org.cesar.edu.backend.models;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AlunoInativo {

    private String cpfAluno;
    private String nomeAluno;

    private Long idCurso;
    private String nomeCurso;

    private LocalDate dataCompra;
    private LocalDate ultimaAulaAssistida;
    private LocalDate dataReferenciaInatividade;

    private Integer diasInativo;

    private String motivo;
    private LocalDateTime dataAtualizacao;
}