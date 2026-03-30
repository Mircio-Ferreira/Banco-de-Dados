package org.cesar.edu.backend.dtos.responses;

import org.cesar.edu.backend.models.*;
import java.util.List;
import java.util.stream.Collectors;

public record UserResponse(
        String tipoUsuario,
        String cpf,
        String nome,
        String email,
        String logradouro,
        Integer numero,
        String cep,
        List<String> telefones,
        List<String> certificados,
        List<CompraResponse> compras,
        List<CursoResponse> cursosLecionados
) {
    public static UserResponse fromProfessor(Professor p, List<CursoResponse> cursosLecionados) {
        return new UserResponse(
                "PROFESSOR",
                p.getCpf(),
                p.getNome(),
                p.getEmail(),
                p.getLogradouro(),
                p.getNumero(),
                p.getCep(),
                p.getTelefones() != null ?
                        p.getTelefones().stream().map(Telefone::getNumero).collect(Collectors.toList()) : null,
                p.getCertificados() != null ?
                        p.getCertificados().stream().map(CertificadoProfessor::getTitulo_certificado).collect(Collectors.toList()) : null,
                null,
                cursosLecionados
        );
    }

    public static UserResponse fromAluno(Aluno a, List<CompraResponse> compras) {
        return new UserResponse(
                "ALUNO",
                a.getCpf(),
                a.getNome(),
                a.getEmail(),
                a.getLogradouro(),
                a.getNumero(),
                a.getCep(),
                a.getTelefones() != null ?
                        a.getTelefones().stream().map(Telefone::getNumero).collect(Collectors.toList()) : null,
                null,
                compras,
                null
        );
    }
}