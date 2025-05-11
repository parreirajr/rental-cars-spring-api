package com.challenge.rental_cars_spring_api.core.domain.exceptions;

public enum ApiErrorCode {

    FILE_EMPTY("ARQ001", "Arquivo vazio."),
    INVALID_FILE_EXTENSION("ARQ002", "Extensão de arquivo inválida. Somente arquivos com extensão .rtn são permitidos."),
    FILE_READ_ERROR("ARQ003", "Erro ao processar arquivo."),
    MAX_FILE_SIZE_EXCEED("ARQ004", "Arquivo excede o tamanho máximo permitido."),
    BUSINESS_RULE_VIOLATION("BNS001", "Regra de negócio violada."),
    UNEXPECTED_ERROR("GEN001", "Erro interno inesperado."),
    RESOURCE_NOT_FOUND("GEN002", "Recurso não encontrado");

    private final String codigo;
    private final String mensagem;

    ApiErrorCode(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    public String codigo() {
        return this.codigo;
    }

    public String mensagem() {
        return this.mensagem;
    }
}
