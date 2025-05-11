package com.challenge.rental_cars_spring_api.core.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class Telefone {

    private static final String CODIGO_PAIS_PADRAO = "55";
    private static final Pattern PADRAO_COM_CODIGO = Pattern.compile("\\d{13}");
    private static final Pattern PADRAO_SEM_CODIGO = Pattern.compile("\\d{11}");

    private final String formatado;

    public Telefone(String numero) {
        String digitos = numero.replaceAll("\\D", "");

        if (PADRAO_SEM_CODIGO.matcher(digitos).matches()) {
            digitos = CODIGO_PAIS_PADRAO + digitos;
        }

        if (!PADRAO_COM_CODIGO.matcher(digitos).matches()) {
            throw new IllegalArgumentException("Número de telefone inválido. Use 11 ou 13 dígitos. Ex: 11987654321 ou 5511987654321.");
        }

        this.formatado = formatar(digitos);
    }

    private String formatar(String digitos) {
        String codigoPais = digitos.substring(0, 2);
        String ddd = digitos.substring(2, 4);
        String parte1 = digitos.substring(4, 9);
        String parte2 = digitos.substring(9);
        return String.format("+%s(%s)%s-%s", codigoPais, ddd, parte1, parte2);
    }

    @JsonValue
    @Override
    public String toString() {
        return formatado;
    }
}