package com.challenge.rental_cars_spring_api.core.queries.dtos;

import com.challenge.rental_cars_spring_api.core.domain.Pago;

import java.math.BigDecimal;
import java.util.List;

public record ListarAlugueisQueryResult(List<ListarAlugueisQueryResultItem> alugueis, BigDecimal valorNaoPago) {

    public static ListarAlugueisQueryResult from(List<ListarAlugueisQueryResultItem> alugueis) {
        BigDecimal valorNaoPago = alugueis.stream()
                .filter(aluguel -> aluguel.pago() == Pago.NAO)
                .map(ListarAlugueisQueryResultItem::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ListarAlugueisQueryResult(alugueis, valorNaoPago);
    }
}
