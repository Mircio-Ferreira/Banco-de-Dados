package org.cesar.edu.backend.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@ToString
@EqualsAndHashCode
public class Professor extends User{
    List<CertificadoProfessor> certificados;
    public Professor() {}
    public Professor(String cpf, String nome, String email, String senha,
                     String logradouro, Integer numero, String cep, List<Telefone> telefones, List<CertificadoProfessor> certificados) {
        super(cpf,nome,email,senha,logradouro,numero,cep,telefones);
        this.certificados = certificados;
    }
}
