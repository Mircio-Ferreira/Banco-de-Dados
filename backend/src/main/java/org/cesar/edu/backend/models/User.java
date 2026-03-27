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

    public User(String cpf, String nome, String email,  String senha, String logradouro, Integer numero, String cep) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
    }
}
