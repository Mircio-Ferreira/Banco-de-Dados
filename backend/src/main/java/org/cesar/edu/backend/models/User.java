package org.cesar.edu.backend.models;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    private String cpf;
    private String nome;
    private String email;
    private String senha;
    private String logradouro;
    private Integer numero;
    private String cep;
    private List<Telefone> telefones;
}
