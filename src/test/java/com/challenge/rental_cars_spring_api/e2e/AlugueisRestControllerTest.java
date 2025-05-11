package com.challenge.rental_cars_spring_api.e2e;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.domain.Cliente;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AlugueisRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AluguelRepository aluguelRepository;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        aluguelRepository.deleteAll();
        carroRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void shouldImportRtnFileSuccessfully() throws Exception {
        String content = "03152024010220240105";
        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "RentReport.rtn",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                content.getBytes()
        );

        mockMvc.perform(multipart("/alugueis/importar").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Arquivo importado com sucesso."));
    }

    @Test
    void shouldReturnErrorIfFileIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "RentReport.rtn",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/alugueis/importar").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("ARQ001"))
                .andExpect(jsonPath("$.mensagem").value("Arquivo vazio."));
    }

    @Test
    void shouldReturnErrorIfExtensionIsInvalid() throws Exception {
        String content = "03152024010220240105";
        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "RentReport.txt",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                content.getBytes()
        );

        mockMvc.perform(multipart("/alugueis/importar").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("ARQ002"))
                .andExpect(jsonPath("$.mensagem").value("Extensão de arquivo inválida. Somente arquivos com extensão .rtn são permitidos."));
    }

    @Test
    void shouldListRentalsSuccessfully() throws Exception {
        Carro carro1 = new Carro(null, "Gol", "2020", 5, 154748, "Volkswagen", new BigDecimal("185.00"));
        Carro carro2 = new Carro(null, "Uno", "2022", 5, 87554, "Fiat", new BigDecimal("199.85"));

        carroRepository.save(carro1);
        carroRepository.save(carro2);

        Cliente cliente1 = new Cliente(null, "Carlos Eduardo Anthony Isaac de Paula", "83413989073", "01981669301", "44992220053");
        Cliente cliente2 = new Cliente(null, "Priscila Márcia Sebastiana Costa", "06154891091", "13079541770", "49997839265");

        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);

        aluguelRepository.save(new Aluguel(null, carro1, cliente1, LocalDate.of(2022, 2, 10), LocalDate.of(2022, 1, 12), new BigDecimal("399.70"), true));
        aluguelRepository.save(new Aluguel(null, carro2, cliente2, LocalDate.of(2022, 12, 29), LocalDate.of(2023, 1, 6), new BigDecimal("2740.00"), true));
        aluguelRepository.save(new Aluguel(null, carro1, cliente1, LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 4), new BigDecimal("300.00"), false));

        mockMvc.perform(get("/alugueis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alugueis", hasSize(3)))
                .andExpect(jsonPath("$.alugueis[0].dataAluguel").value("2022-02-10"))
                .andExpect(jsonPath("$.alugueis[1].modeloCarro").value("Uno"))
                .andExpect(jsonPath("$.alugueis[2].telefone").value("+55(44)99222-0053"))
                .andExpect(jsonPath("$.valorNaoPago").value(300));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/alugueis").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alugueis", hasSize(0)))
                .andExpect(jsonPath("$.valorNaoPago").value(0));
    }

}
