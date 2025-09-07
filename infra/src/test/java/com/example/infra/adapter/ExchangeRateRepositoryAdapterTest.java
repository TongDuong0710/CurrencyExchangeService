package com.example.infra.adapter;

import com.example.domain.model.ExchangeRate;
import com.example.infra.persistence.adapter.ExchangeRateRepositoryAdapter;
import com.example.infra.persistence.entity.ExchangeRateEntity;
import com.example.infra.persistence.mapper.ExchangeRateEntityMapper;
import com.example.infra.persistence.repository.JpaExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateRepositoryAdapterTest {

    @Mock
    private JpaExchangeRateRepository jpaRepository;

    @Mock
    private ExchangeRateEntityMapper mapper;

    @InjectMocks
    private ExchangeRateRepositoryAdapter adapter;

    private ExchangeRate domain;
    private ExchangeRateEntity entity;

    @BeforeEach
    void setUp() {
        domain = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .averageBid(1.0)
                .averageAsk(1.2)
                .updatedTime(LocalDateTime.now())
                .build();

        entity = new ExchangeRateEntity();
        entity.setBaseCurrency("USD");
        entity.setTargetCurrency("EUR");
        entity.setAverageBid(1.0);
        entity.setAverageAsk(1.2);
        entity.setUpdatedTime(domain.getUpdatedTime());
    }

    @Test
    void save_shouldInsertWhenNotExist() {
        when(jpaRepository.findByBaseCurrencyAndTargetCurrency("USD", "EUR"))
                .thenReturn(Optional.empty());
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        ExchangeRate result = adapter.save(domain);

        assertThat(result.getBaseCurrency()).isEqualTo("USD");
        verify(jpaRepository).save(entity);
    }

    @Test
    void save_shouldUpdateWhenExist() {
        when(jpaRepository.findByBaseCurrencyAndTargetCurrency("USD", "EUR"))
                .thenReturn(Optional.of(entity));
        doAnswer(inv -> {
            ExchangeRate src = inv.getArgument(0);
            ExchangeRateEntity target = inv.getArgument(1);
            target.setAverageBid(src.getAverageBid());
            target.setAverageAsk(src.getAverageAsk());
            return null;
        }).when(mapper).updateEntityFromDomain(any(), any());
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        ExchangeRate result = adapter.save(domain);

        assertThat(result.getTargetCurrency()).isEqualTo("EUR");
        verify(mapper).updateEntityFromDomain(domain, entity);
    }

    @Test
    void saveAll_shouldInsertAndUpdate() {
        ExchangeRate newDomain = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("JPY")
                .averageBid(110.0)
                .averageAsk(111.0)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRateEntity newEntity = new ExchangeRateEntity();
        newEntity.setBaseCurrency("USD");
        newEntity.setTargetCurrency("JPY");

        when(jpaRepository.findByBaseCurrencyAndTargetCurrency("USD", "EUR"))
                .thenReturn(Optional.of(entity));
        when(jpaRepository.findByBaseCurrencyAndTargetCurrency("USD", "JPY"))
                .thenReturn(Optional.empty());
        when(mapper.toEntity(newDomain)).thenReturn(newEntity);

        List<ExchangeRateEntity> savedEntities = List.of(entity, newEntity);
        when(jpaRepository.saveAll(any())).thenReturn(savedEntities);
        when(mapper.toDomainList(savedEntities)).thenReturn(List.of(domain, newDomain));

        List<ExchangeRate> result = adapter.saveAll(List.of(domain, newDomain));

        assertThat(result).hasSize(2);
        verify(jpaRepository).saveAll(any());
    }

    @Test
    void findByBaseCurrency_shouldReturnList() {
        when(jpaRepository.findByBaseCurrency("USD")).thenReturn(List.of(entity));
        when(mapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));

        List<ExchangeRate> result = adapter.findByBaseCurrency("USD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetCurrency()).isEqualTo("EUR");
    }

    @Test
    void findByBaseAndTarget_shouldReturnOptional() {
        when(jpaRepository.findByBaseCurrencyAndTargetCurrency("USD", "EUR"))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<ExchangeRate> result = adapter.findByBaseAndTarget("USD", "EUR");

        assertThat(result).isPresent();
        assertThat(result.get().getBaseCurrency()).isEqualTo("USD");
    }

    @Test
    void deleteByBaseCurrency_shouldCallRepo() {
        adapter.deleteByBaseCurrency("USD");

        verify(jpaRepository).deleteByBaseCurrency("USD");
    }
}
