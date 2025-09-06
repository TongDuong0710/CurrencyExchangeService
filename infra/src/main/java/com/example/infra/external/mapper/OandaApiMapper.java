package com.example.infra.external.mapper;

import com.example.domain.model.ExchangeRate;
import com.example.infra.configurations.MapStructCentralConfig;
import com.example.infra.external.dto.OandaApiResponse;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapStructCentralConfig.class)
public interface OandaApiMapper {

    default List<ExchangeRate> toDomain(OandaApiResponse response) {
        if (response == null || response.getRates() == null) {
            return List.of();
        }
        return response.getRates().entrySet().stream()
                .map(entry -> new ExchangeRate(
                        response.getBase(),
                        entry.getKey(),
                        entry.getValue(),
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList());
    }
}
