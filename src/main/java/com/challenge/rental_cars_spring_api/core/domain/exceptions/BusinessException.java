package com.challenge.rental_cars_spring_api.core.domain.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ApiErrorCode errorCode;

    public BusinessException(ApiErrorCode errorCode) {
        super(errorCode.mensagem());
        this.errorCode = errorCode;
    }

}
