package org.cesar.edu.backend.dtos.responses;

import org.cesar.edu.backend.models.*;
import java.util.List;
import java.util.stream.Collectors;

public record UserResponse(
        String cpf,
        String nome,
        String email,
        String logradouro,
        Integer numero,
        String cep,
        List<String> telefones,
        List<String> certificados
) {
    public static UserResponse fromProfessor(Professor p) {
        return new UserResponse(
                p.getCpf(),
                p.getNome(),
                p.getEmail(),
                p.getLogradouro(),
                p.getNumero(),
                p.getCep(),
                p.getTelefones() != null ?
                        p.getTelefones().stream().map(Telefone::getNumero).collect(Collectors.toList()) : null,
                p.getCertificados() != null ?
                        p.getCertificados().stream().map(CertificadoProfessor::getTitulo_certificado).collect(Collectors.toList()) : null
        );
    }

    public static UserResponse fromAluno(Aluno a) {
        return new UserResponse(
                a.getCpf(),
                a.getNome(),
                a.getEmail(),
                a.getLogradouro(),
                a.getNumero(),
                a.getCep(),
                a.getTelefones() != null ?
                        a.getTelefones().stream().map(Telefone::getNumero).collect(Collectors.toList()) : null,
                null
        );
    }
}