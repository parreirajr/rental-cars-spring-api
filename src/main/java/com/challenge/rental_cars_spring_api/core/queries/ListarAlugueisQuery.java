package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResult;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarAlugueisQuery {

    private final AluguelRepository aluguelRepository;

    public ListarAlugueisQueryResult execute(){
        List<ListarAlugueisQueryResultItem> alugueis = aluguelRepository.findAll()
                .stream()
                .map(ListarAlugueisQueryResultItem::from)
                .collect(Collectors.toList());

        return ListarAlugueisQueryResult.from(alugueis);
    }
}
