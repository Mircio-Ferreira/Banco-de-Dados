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
    private Long id_curso;
    private Long id_compra;
    private Long id_comentário_pai;
    private String cpf;
    private LocalDate data_criacao;
    private String conteudo;
}
