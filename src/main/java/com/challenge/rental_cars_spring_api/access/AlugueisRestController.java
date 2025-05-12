package com.challenge.rental_cars_spring_api.access;

import com.challenge.rental_cars_spring_api.core.domain.exceptions.ApiErrorCode;
import com.challenge.rental_cars_spring_api.core.domain.exceptions.BusinessException;
import com.challenge.rental_cars_spring_api.core.queries.ListarAlugueisQuery;
import com.challenge.rental_cars_spring_api.core.queries.ProcessaArquivo;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResult;
import com.challenge.rental_cars_spring_api.core.queries.dtos.MensagemResposta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/alugueis")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AlugueisRestController {

    private final ProcessaArquivo processaArquivo;
    private final ListarAlugueisQuery listarAlugueisQuery;

    private static void validarArquivo(MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new BusinessException(ApiErrorCode.FILE_EMPTY);
        }

        if (!Objects.requireNonNull(arquivo.getOriginalFilename()).toLowerCase().endsWith(".rtn")) {
            throw new BusinessException(ApiErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    @Operation(summary = "Importa os dados de alugueis de um arquivo .rtn", description = "Recebe um arquivo (.rtn) contendo dados de alugueis de veículos e os processa.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Arquivo importado com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MensagemResposta.class), examples = @ExampleObject(value = """
            {
                "mensagem": "Arquivo importado com sucesso."
            }
            """))), @ApiResponse(responseCode = "400", description = "Erros no processamento do arquivo.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MensagemResposta.class), examples = {@ExampleObject(name = "Arquivo vazio.", value = """
            {
                "codigo": "ARQ001",
                "mensagem": "Arquivo vazio."
            }
            """), @ExampleObject(name = "Extensão inválida.", value = """
            {
                "codigo": "ARQ002",
                "mensagem": "Extensão de arquivo inválida. Somente arquivos com extensão .rtn são permitidos."
            }
            """)})), @ApiResponse(responseCode = "413", description = "Arquivo excede o tamanho máximo permitido.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MensagemResposta.class), examples = @ExampleObject(value = """
            {
                "codigo": "ARQ004",
                "mensagem": "Arquivo excede o tamanho máximo permitido."
            }
            """))), @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MensagemResposta.class), examples = @ExampleObject(value = """
            {
                "codigo": "GEN001",
                "mensagem": "Erro interno inesperado."
            }
            """)))})
    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MensagemResposta> uploadArquivo(@Parameter(description = "Arquivo .rtn a ser importado. Tamanho máximo 10MB.", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) @RequestPart("arquivo") MultipartFile arquivo) throws IOException {
        validarArquivo(arquivo);

        processaArquivo.execute(arquivo.getInputStream());
        return ResponseEntity.ok(new MensagemResposta("Arquivo importado com sucesso."));
    }

    @Operation(summary = "Lista os alugueis registrados.", description = "Retorna uma lista com todos os alugueis de veículos registrados no sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de alugueis retornada com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ListarAlugueisQueryResult.class), examples = @ExampleObject(value = """
            {
              "alugueis": [
                {
                  "dataAluguel": "2022-01-10",
                  "modeloCarro": "GOL",
                  "km": 87554,
                  "nomeCliente": "Luciana Isis Maitê Ferreira",
                  "telefone": "+55(14)99467-6214",
                  "dataDevolucao": "2022-01-12",
                  "valor": 399.7,
                  "pago": "SIM"
                },
                {
                  "dataAluguel": "2023-01-02",
                  "modeloCarro": "UNO",
                  "km": 445580,
                  "nomeCliente": "Geraldo Henry Bernardo da Conceição",
                  "telefone": "+55(51)98665-6408",
                  "dataDevolucao": "2023-01-04",
                  "valor": 300,
                  "pago": "NAO"
                }
              ],
              "valorNaoPago": 300
            }
            """))), @ApiResponse(responseCode = "500", description = "Erro interno ao listar os alugueis.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MensagemResposta.class), examples = @ExampleObject(value = """
            {
                "codigo": "GEN001",
                "mensagem": "Erro interno inesperado ao listar os alugueis."
            }
            """)))})
    @GetMapping
    public ResponseEntity<ListarAlugueisQueryResult> listarAlugueis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAluguel,
            @RequestParam(required = false) String modeloCarro
    ) {
        return new ResponseEntity<>(listarAlugueisQuery.execute(dataAluguel, modeloCarro), HttpStatus.OK);
    }
}
