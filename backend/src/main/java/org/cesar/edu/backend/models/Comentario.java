package org.cesar.edu.backend.models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {
    private Long id_comentario;
    private Long id_aula;
    private Long id_curso;
    private String cpf_aluno;
    private String cpf_professor;
    private LocalDate data_criacao;
    private String conteudo;
    private Long comentario_pai;
}