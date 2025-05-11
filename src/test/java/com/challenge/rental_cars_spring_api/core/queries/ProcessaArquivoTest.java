package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.domain.Cliente;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.ClienteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProcessaArquivoTest {

    private static final Logger logger = Logger.getLogger(ProcessaArquivo.class.getName());

    private CarroRepository carroRepository;
    private ClienteRepository clienteRepository;
    private AluguelRepository aluguelRepository;
    private ProcessaArquivo processaArquivo;

    private Carro carro;
    private Cliente cliente;

    private InputStream inputStream;
    private StringBuilder logOutput;

    private ConsoleHandler consoleHandler;

    @BeforeEach
    void setUp() {
        carroRepository = Mockito.mock(CarroRepository.class);
        clienteRepository = Mockito.mock(ClienteRepository.class);
        aluguelRepository = Mockito.mock(AluguelRepository.class);
        processaArquivo = new ProcessaArquivo(aluguelRepository, carroRepository, clienteRepository);

        carro = new Carro(3L, "Gol", "2020", 5, 154885, "Volkswagen", new BigDecimal("185.00"));
        cliente = new Cliente();

        String linha = "03152024010220240205";
        inputStream = new ByteArrayInputStream(linha.getBytes());

        logOutput = new StringBuilder();
        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.WARNING);
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                logOutput.append(record.getMessage()).append("\n");
                return "";
            }
        });
        logger.addHandler(consoleHandler);
    }

    @Test
    void shouldSaveRentalWhenCarAndClientExists() throws IOException {
        when(carroRepository.findById(3L)).thenReturn(Optional.of(carro));
        when(clienteRepository.findById(15L)).thenReturn(Optional.of(cliente));

        processaArquivo.execute(inputStream);

        ArgumentCaptor<Aluguel> captor = ArgumentCaptor.forClass(Aluguel.class);
        verify(aluguelRepository, times(1)).save(captor.capture());
        Aluguel aluguel = captor.getValue();

        assertEquals(carro, aluguel.getCarro());
        assertEquals(cliente, aluguel.getCliente());

        LocalDate dataAluguel = LocalDate.of(2024, 1, 2);
        LocalDate dataDevolucao = LocalDate.of(2024, 2, 5);
        long numeroDiasAlugado = ChronoUnit.DAYS.between(dataAluguel, dataDevolucao);
        BigDecimal valorAluguel = BigDecimal.valueOf(numeroDiasAlugado).multiply(carro.getVlrDiaria());

        assertEquals(dataAluguel, aluguel.getDataAluguel());
        assertEquals(dataDevolucao, aluguel.getDataDevolucao());
        assertEquals(valorAluguel, aluguel.getValor());
    }

    @Test
    void shouldIgnoreLineIfCarDoesNotExist() throws IOException {
        when(carroRepository.findById(3L)).thenReturn(Optional.empty());
        when(clienteRepository.findById(15L)).thenReturn(Optional.of(cliente));

        processaArquivo.execute(inputStream);

        verify(aluguelRepository, never()).save(any(Aluguel.class));
        assertTrue(logOutput.toString().contains("O Carro com o id \"3\" não foi encontrado."));
    }

    @Test
    void shouldIgnoreLineIfClientDoesNotExist() throws IOException {
        when(carroRepository.findById(3L)).thenReturn(Optional.of(carro));
        when(clienteRepository.findById(15L)).thenReturn(Optional.empty());

        processaArquivo.execute(inputStream);

        verify(aluguelRepository, never()).save(any(Aluguel.class));
        assertTrue(logOutput.toString().contains("O Cliente com o id \"15\" não foi encontrado."));
    }

    @Test
    void shouldIgnoreBlankLines() throws IOException {
        String blankLine = "\n\n";
        InputStream blankInputStream = new ByteArrayInputStream(blankLine.getBytes());

        processaArquivo.execute(blankInputStream);

        verify(aluguelRepository, never()).save(any(Aluguel.class));
        assertTrue(logOutput.toString().isEmpty());
    }

    @AfterEach
    void tearDown() {
        logger.removeHandler(consoleHandler);
        logOutput.setLength(0);
    }
}