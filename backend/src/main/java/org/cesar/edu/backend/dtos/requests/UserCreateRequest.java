package org.cesar.edu.backend.dtos.requests;

import jakarta.validation.constraints.NotNull;
import org.cesar.edu.backend.models.Aluno;
import org.cesar.edu.backend.models.CertificadoProfessor;
import org.cesar.edu.backend.models.Professor;
import org.cesar.edu.backend.models.Telefone;

import java.util.ArrayList;
import java.util.List;

public record UserCreateRequest(
        @NotNull String cpf,
        @NotNull String nome,
        @NotNull String email,
        @NotNull String senha,
        String logradouro,
        Integer numero,
        String cep,
        List<String> telefones,
        List<String> certificados
) {

    public static Professor toEntityProfessor(UserCreateRequest userDto) {
        Professor professor = new Professor();

        professor.setCpf(userDto.cpf());
        professor.setNome(userDto.nome());
        professor.setEmail(userDto.email());
        professor.setSenha(userDto.senha());
        professor.setLogradouro(userDto.logradouro());
        professor.setNumero(userDto.numero());
        professor.setCep(userDto.cep());

        List<Telefone> listaTelefones = new ArrayList<>();
        if (userDto.telefones() != null) {
            for (String numeroTelefone : userDto.telefones()) {
                Telefone telefone = new Telefone();
                telefone.setCpf_usuario(userDto.cpf());
                telefone.setNumero(numeroTelefone);
                listaTelefones.add(telefone);
            }
        }
        professor.setTelefones(listaTelefones);

        List<CertificadoProfessor> listaCertificados = new ArrayList<>();
        if (userDto.certificados() != null) {
            for (String tituloCertificado : userDto.certificados()) {
                CertificadoProfessor certificado = new CertificadoProfessor();
                certificado.setCpf_professor(userDto.cpf());
                certificado.setTitulo_certificado(tituloCertificado);
                listaCertificados.add(certificado);
            }
        }
        professor.setCertificados(listaCertificados);

        return professor;
    }

    public static Aluno toEntityAluno(UserCreateRequest userDto) {
        Aluno aluno = new Aluno();

        aluno.setCpf(userDto.cpf());
        aluno.setNome(userDto.nome());
        aluno.setEmail(userDto.email());
        aluno.setSenha(userDto.senha());
        aluno.setLogradouro(userDto.logradouro());
        aluno.setNumero(userDto.numero());
        aluno.setCep(userDto.cep());

        List<Telefone> listaTelefones = new ArrayList<>();
        if (userDto.telefones() != null) {
            for (String numeroTelefone : userDto.telefones()) {
                Telefone telefone = new Telefone();
                telefone.setCpf_usuario(userDto.cpf());
                telefone.setNumero(numeroTelefone);
                listaTelefones.add(telefone);
            }
        }
        aluno.setTelefones(listaTelefones);

        return aluno;
    }
}