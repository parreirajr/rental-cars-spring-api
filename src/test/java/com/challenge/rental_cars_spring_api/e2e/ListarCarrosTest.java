package com.challenge.rental_cars_spring_api.e2e;

import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ListarCarrosTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarroRepository carroRepository;

    @BeforeEach
    void setUp() {
        carroRepository.deleteAll();
    }

    @Test
    void shouldReturnListOfCars() throws Exception {

        carroRepository.save(new Carro(null, "Gol", "2020", 5, 154748, "Volkswagen", new BigDecimal("185.00")));
        carroRepository.save(new Carro(null, "Gol", "2022", 5, 87554, "Volkswagen", new BigDecimal("199.85")));

        mockMvc.perform(get("/carros").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].modelo", is("Gol")))
                .andExpect(jsonPath("$[0].ano", is("2020")))
                .andExpect(jsonPath("$[0].qtdPassageiros", is(5)))
                .andExpect(jsonPath("$[0].km", is(154748)))
                .andExpect(jsonPath("$[0].fabricante", is("Volkswagen")))
                .andExpect(jsonPath("$[0].vlrDiaria", is(185.00)))
                .andExpect(jsonPath("$[1].modelo", is("Gol")))
                .andExpect(jsonPath("$[1].ano", is("2022")))
                .andExpect(jsonPath("$[1].qtdPassageiros", is(5)))
                .andExpect(jsonPath("$[1].km", is(87554)))
                .andExpect(jsonPath("$[1].fabricante", is("Volkswagen")))
                .andExpect(jsonPath("$[1].vlrDiaria", is(199.85)));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/carros").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
