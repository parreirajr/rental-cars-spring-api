package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResult;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import com.challenge.rental_cars_spring_api.core.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ListarAlugueisQueryTest {

    private AluguelRepository aluguelRepository;
    private ListarAlugueisQuery listarAlugueisQuery;

    @BeforeEach
    void setUp() {
        aluguelRepository = Mockito.mock(AluguelRepository.class);
        listarAlugueisQuery = new ListarAlugueisQuery(aluguelRepository);
    }

    @Test
    void shouldReturnListOfMappedRentals() {
        Carro carro1 = new Carro(1L, "Gol", "2020", 5, 154748, "Volkswagen", new BigDecimal("185.00"));
        Carro carro2 = new Carro(2L, "Gol", "2022", 5, 87554, "Volkswagen", new BigDecimal("199.85"));

        Cliente cliente1 = new Cliente(1L, "Carlos Eduardo Anthony Isaac de Paula", "83413989073", "01981669301", "44992220053");
        Cliente cliente2 = new Cliente(2L, "Priscila Márcia Sebastiana Costa", "06154891091", "13079541770", "49997839265");

        Aluguel aluguel1 = new Aluguel(1L, carro1, cliente1, LocalDate.of(2022, 2, 10), LocalDate.of(2022, 1, 12), new BigDecimal("399.70"), true);
        Aluguel aluguel2 = new Aluguel(2L, carro2, cliente2, LocalDate.of(2022, 12, 29), LocalDate.of(2023, 1, 6), new BigDecimal("2740.00"), true);
        Aluguel aluguel3 = new Aluguel(2L, carro1, cliente1, LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 4), new BigDecimal("300.00"), false);

        when(aluguelRepository.findAll()).thenReturn(List.of(aluguel1, aluguel2, aluguel3));

        ListarAlugueisQueryResult result = listarAlugueisQuery.execute();

        assertThat(result.valorNaoPago()).isEqualTo(new BigDecimal("300.00"));

        List<ListarAlugueisQueryResultItem> alugueis = result.alugueis();
        assertThat(alugueis).hasSize(3);
        assertThat(alugueis.getFirst().dataAluguel()).isEqualTo(aluguel1.getDataAluguel());
        assertThat(alugueis.getFirst().modeloCarro()).isEqualTo(carro1.getModelo());
        assertThat(alugueis.getFirst().km()).isEqualTo(carro1.getKm());
        assertThat(alugueis.getFirst().nomeCliente()).isEqualTo(cliente1.getNome());
        assertThat(alugueis.getFirst().telefone()).isEqualTo(new Telefone(aluguel1.getCliente().getTelefone()));
        assertThat(alugueis.getFirst().dataDevolucao()).isEqualTo(aluguel1.getDataDevolucao());
        assertThat(alugueis.getFirst().valor()).isEqualTo(aluguel1.getValor());
        assertThat(alugueis.getFirst().pago()).isEqualTo(Pago.SIM);

        verify(aluguelRepository, times(1)).findAll();
    }

    @Test
    void shouldResultEmptyListWhenNoRentalsAreFound() {
        when(aluguelRepository.findAll()).thenReturn(List.of());

        ListarAlugueisQueryResult result = listarAlugueisQuery.execute();

        assertThat(result.valorNaoPago()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.alugueis()).isEmpty();
        verify(aluguelRepository, times(1)).findAll();
    }

}
