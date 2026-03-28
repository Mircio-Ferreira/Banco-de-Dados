package org.cesar.edu.backend.utils;

import org.cesar.edu.backend.models.CertificadoProfessor;

public class StringUtils {
    public static boolean estaVazia(String str) {
        if(str == null) {
            return true;
        }
        for(int i = 0; i < str.length(); i++) {
            char atual = str.charAt(i);
            if(atual != ' ') {
                return false;
            }
        }
        return true;
    }
    public static boolean tamanhoExcedido(String str, int tamanho) {
        if(tamanho < 0) return false;
        if(str == null) {
            return tamanho > 0 ? true : false;
        }
        if(str.length() > tamanho){
            return true;
        }

        return false;
    }
    public static boolean tamanhoMenor(String str, int tamanho) {
        if(tamanho < 0) return false;
        if(str == null) {
            return tamanho > 0 ? true : false;
        }
        if(str.length() < tamanho){
            return true;
        }

        return false;
    }
    public static boolean emailValido(String email) {
        if(email == null) return false;
        if(email.indexOf('@') < 0 || email.indexOf('.') < 0) {
            return false;
        }
        return true;
    }
    public static boolean telefoneValido(String tel) {
        if(tel == null) return false;
        if(tel.indexOf('(') < 0 || tel.indexOf(')') < 0) {
            return false;
        }
        String numeroPuro = tel.substring(tel.indexOf(')') + 1, tel.length());
        if(numeroPuro.length() > 9 || numeroPuro.length() < 8) {
            return false;
        }
        return true;
    }
    public static boolean cpfValido(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) return false;

        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Character.getNumericValue(cpf.charAt(i));
                soma += digito * (10 - i);
            }

            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                int digito = Character.getNumericValue(cpf.charAt(i));
                soma += digito * (11 - i);
            }

            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;

            return digito1 == Character.getNumericValue(cpf.charAt(9)) &&
                    digito2 == Character.getNumericValue(cpf.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }
    public static boolean contemCaracterEspecial(String texto) {
        return texto.matches(".*[^a-zA-Z0-9 ].*");
    }
    public static boolean contemSenhaValidaNumerosLetras(String senha) {
        if (senha == null || senha.isBlank()) {
            return false;
        }

        boolean temLetra = false;
        boolean temNumero = false;

        for (int i = 0; i < senha.length(); i++) {
            char c = senha.charAt(i);

            if (Character.isLetter(c)) {
                temLetra = true;
            }

            if (Character.isDigit(c)) {
                temNumero = true;
            }
        }

        return temLetra && temNumero;
    }
    public static boolean certificadoValido(CertificadoProfessor certificado) {
        if (certificado == null) return false;

        if (certificado.getCpf_professor() == null || certificado.getCpf_professor().isBlank()) {
            return false;
        }

        if (!cpfValido(certificado.getCpf_professor())) {
            return false;
        }

        if (certificado.getTitulo_certificado() == null || certificado.getTitulo_certificado().isBlank()) {
            return false;
        }

        if (certificado.getTitulo_certificado().length() < 3 || certificado.getTitulo_certificado().length() > 150) {
            return false;
        }

        return true;
    }
}
