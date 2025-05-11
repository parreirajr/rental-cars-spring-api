package com.challenge.rental_cars_spring_api.core.queries.dtos;

import com.challenge.rental_cars_spring_api.core.domain.Carro;

import java.math.BigDecimal;

public record ListarCarrosQueryResultItem(Long id, String modelo, String ano, Integer qtdPassageiros, Integer km,
                                          String fabricante, BigDecimal vlrDiaria) {

    public static ListarCarrosQueryResultItem from(Carro carro) {
        return new ListarCarrosQueryResultItem(carro.getId(), carro.getModelo(), carro.getAno(), carro.getQtdPassageiros(), carro.getKm(), carro.getFabricante(), carro.getVlrDiaria());
    }
}
