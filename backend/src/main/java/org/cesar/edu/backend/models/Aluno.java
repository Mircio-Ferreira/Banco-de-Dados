package org.cesar.edu.backend.models;

import java.util.List;

public class Aluno extends User {
    public Aluno(String cpf, String nome, String email, String senha,
                 String logradouro, Integer numero, String cep, List<Telefone> telefones){
        super(cpf,nome,email,senha,logradouro,numero,cep,telefones);
    }
}
