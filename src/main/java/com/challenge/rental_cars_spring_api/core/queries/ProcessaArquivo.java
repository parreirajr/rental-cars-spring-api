package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.domain.Cliente;
import com.challenge.rental_cars_spring_api.core.domain.exceptions.ApiErrorCode;
import com.challenge.rental_cars_spring_api.core.domain.exceptions.BusinessException;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ProcessaArquivo {

    private final AluguelRepository aluguelRepository;
    private final CarroRepository carroRepository;
    private final ClienteRepository clienteRepository;

    private static final Logger logger = Logger.getLogger(ProcessaArquivo.class.getName());

    public void execute(InputStream arquivo) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(arquivo));
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;

            int carrId = Integer.parseInt(linha.substring(0, 2).trim());
            Optional<Carro> carro = carroRepository.findById((long) carrId);
            if (carro.isEmpty()) {
                logger.warning("O Carro com o id \"" + carrId + "\" não foi encontrado.");
                continue;
            }

            int clienteId = Integer.parseInt(linha.substring(2, 4).trim());
            Optional<Cliente> cliente = clienteRepository.findById((long) clienteId);
            if (cliente.isEmpty()) {
                logger.warning("O Cliente com o id \"" + clienteId + "\" não foi encontrado.");
                continue;
            }

            Aluguel aluguel = new Aluguel();
            aluguel.setCarro(carro.get());
            aluguel.setCliente(cliente.get());
            LocalDate dataAluguel = LocalDate.parse(linha.substring(4, 12).trim(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            aluguel.setDataAluguel(dataAluguel);
            LocalDate dataDevolucao = LocalDate.parse(linha.substring(12, 20).trim(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            aluguel.setDataDevolucao(dataDevolucao);
            long numeroDiasAlugado = ChronoUnit.DAYS.between(dataAluguel, dataDevolucao);
            BigDecimal valor = BigDecimal.valueOf(numeroDiasAlugado).multiply(carro.get().getVlrDiaria());
            aluguel.setValor(valor);
            aluguel.setPago(true);
            aluguelRepository.save(aluguel);
        }
    }
}
