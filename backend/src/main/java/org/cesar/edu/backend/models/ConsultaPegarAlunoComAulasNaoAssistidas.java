package org.cesar.edu.backend.models;

import lombok.*;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ConsultaPegarAlunoComAulasNaoAssistidas {
    private String cpf;
    private String nome_aluno;
    private String nome_curso;
    private Long idCurso;
}
