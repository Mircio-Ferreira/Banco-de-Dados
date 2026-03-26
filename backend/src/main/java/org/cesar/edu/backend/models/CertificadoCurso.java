package org.cesar.edu.backend.models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CertificadoCurso {
    private Long id_certificado;
    private Long id_curso;
    private String cpf_aluno;
    private LocalDate data_certificado;
}
