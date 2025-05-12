package com.challenge.rental_cars_spring_api.core.queries.spacifications;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AluguelSpecifications {

    public static Specification<Aluguel> comDataAluguel(LocalDate data) {
    return (root, query, cb) -> {
        if (data == null) {
            return null;
        }
        return cb.equal(root.get("dataAluguel"), data);
    };
}


    public static Specification<Aluguel> comModeloCarro(String modelo) {
        return (root, query, cb) -> {
            if (modelo == null) {
                return null;
            }
            Join<Object, Object> carro = root.join("carro"); // junta com a entidade Carro
            return cb.like(cb.lower(carro.get("modelo")), "%" + modelo.toLowerCase() + "%");
        };
    }
}