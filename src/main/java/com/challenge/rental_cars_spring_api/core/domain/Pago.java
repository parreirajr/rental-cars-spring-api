package com.challenge.rental_cars_spring_api.core.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Pago {
    SIM(true),
    NAO(false);

    private final Boolean pago;

    Pago(Boolean pago) {
        this.pago = pago;
    }

    public Boolean estaPago() {
        return pago;
    }

    public String valor() {
        return this.name();
    }

    public static Pago fromBoolean(Boolean pago) {
        for (Pago p : Pago.values()) {
            if (p.estaPago().equals(pago)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Valor booleano inválido para o enum Pago: " + pago);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name();
    }
}
