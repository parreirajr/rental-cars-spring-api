package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarCarrosQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ListarCarrosQueryTest {

    private CarroRepository carroRepository;
    private ListarCarrosQuery listarCarrosQuery;

    @BeforeEach
    void setUp() {
        carroRepository = Mockito.mock(CarroRepository.class);
        listarCarrosQuery = new ListarCarrosQuery(carroRepository);
    }

    @Test
    void shouldReturnListOfMappedCars() {
        // Arrange
        Carro carro1 = new Carro(1L, "Gol", "2020", 5, 154748, "Volkswagen", new BigDecimal("185.00"));
        Carro carro2 = new Carro(2L, "Gol", "2022", 5, 87554, "Volkswagen", new BigDecimal("199.85"));

        when(carroRepository.findAll()).thenReturn(List.of(carro1, carro2));

        // Act
        List<ListarCarrosQueryResultItem> result = listarCarrosQuery.execute();

        // Assert
        assertThat(result).hasSize(2);

        assertThat(result.getFirst().id()).isEqualTo(1L);
        assertThat(result.getFirst().modelo()).isEqualTo("Gol");
        assertThat(result.getFirst().ano()).isEqualTo("2020");
        assertThat(result.getFirst().qtdPassageiros()).isEqualTo(5);
        assertThat(result.getFirst().km()).isEqualTo(154748);
        assertThat(result.getFirst().fabricante()).isEqualTo("Volkswagen");
        assertThat(result.getFirst().vlrDiaria()).isEqualTo(new BigDecimal("185.00"));

        assertThat(result.getLast().id()).isEqualTo(2L);
        assertThat(result.getLast().modelo()).isEqualTo("Gol");
        assertThat(result.getLast().ano()).isEqualTo("2022");
        assertThat(result.getLast().qtdPassageiros()).isEqualTo(5);
        assertThat(result.getLast().km()).isEqualTo(87554);
        assertThat(result.getLast().fabricante()).isEqualTo("Volkswagen");
        assertThat(result.getLast().vlrDiaria()).isEqualTo(new BigDecimal("199.85"));

        verify(carroRepository, times(1)).findAll();
    }

    @Test
    void shouldResultEmptyListWhenNoCarsAreFound() {
        // Arrange
        when(carroRepository.findAll()).thenReturn(List.of());

        // Act
        List<ListarCarrosQueryResultItem> result = listarCarrosQuery.execute();

        // Assert
        assertThat(result).isEmpty();
        verify(carroRepository, times(1)).findAll();
    }
}
