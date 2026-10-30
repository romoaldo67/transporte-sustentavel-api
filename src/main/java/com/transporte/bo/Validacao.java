package com.transporte.bo;

import com.transporte.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

final class Validacao {

    static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validacao() {
    }

    static void texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("O campo '" + campo + "' é obrigatório.");
        }
    }

    static void email(String email) {
        texto(email, "email");
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new RegraNegocioException("E-mail inválido.");
        }
    }

    static LocalDate data(String valor, String campo) {
        texto(valor, campo);
        try {
            return LocalDate.parse(valor, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("O campo '" + campo + "' deve estar no formato DD/MM/AAAA.");
        }
    }

    static String hoje() {
        return LocalDate.now().format(FORMATO_DATA);
    }
}
