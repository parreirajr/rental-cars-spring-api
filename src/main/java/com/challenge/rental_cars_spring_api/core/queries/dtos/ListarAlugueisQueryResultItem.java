package com.challenge.rental_cars_spring_api.core.queries.dtos;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Pago;
import com.challenge.rental_cars_spring_api.core.domain.Telefone;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ListarAlugueisQueryResultItem(LocalDate dataAluguel, String modeloCarro, Integer km, String nomeCliente, Telefone telefone, LocalDate dataDevolucao, BigDecimal valor, Pago pago) {

    public static ListarAlugueisQueryResultItem from(Aluguel aluguel) {
        return new ListarAlugueisQueryResultItem(
                aluguel.getDataAluguel(),
                aluguel.getCarro().getModelo(),
                aluguel.getCarro().getKm(),
                aluguel.getCliente().getNome(),
                new Telefone(aluguel.getCliente().getTelefone()),
                aluguel.getDataDevolucao(),
                aluguel.getValor(),
                Pago.fromBoolean(aluguel.getPago())
        );
    }
}
