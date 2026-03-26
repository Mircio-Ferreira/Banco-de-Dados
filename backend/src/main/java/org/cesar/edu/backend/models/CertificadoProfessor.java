package org.cesar.edu.backend.models;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoProfessor {
    private String cpf_professor;
    private String titulo_certificado;
}
