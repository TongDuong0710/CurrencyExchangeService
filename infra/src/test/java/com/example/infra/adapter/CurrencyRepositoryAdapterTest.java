package com.example.infra.adapter;

import com.example.domain.model.Currency;
import com.example.infra.persistence.adapter.CurrencyRepositoryAdapter;
import com.example.infra.persistence.entity.CurrencyEntity;
import com.example.infra.persistence.mapper.CurrencyEntityMapper;
import com.example.infra.persistence.repository.JpaCurrencyRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRepositoryAdapterTest {

    @Mock
    private JpaCurrencyRepository jpaRepository;

    @Mock
    private CurrencyEntityMapper mapper;

    @InjectMocks
    private CurrencyRepositoryAdapter adapter;

    private Currency domain;
    private CurrencyEntity entity;

    @BeforeEach
    void setUp() {
        domain = new Currency("USD", "Dollar", LocalDateTime.now(), LocalDateTime.now());
        entity = new CurrencyEntity("USD", "Dollar", domain.getCreatedAt(), domain.getUpdatedAt());
    }

    @Test
    void findAll_shouldReturnDomainList() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));

        List<Currency> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("USD");
        verify(jpaRepository).findAll();
    }

    @Test
    void findByCode_shouldReturnDomain() {
        when(jpaRepository.findById("USD")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Currency> result = adapter.findByCode("USD");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("USD");
        verify(jpaRepository).findById("USD");
    }

    @Test
    void save_shouldReturnSavedDomain() {
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Currency result = adapter.save(domain);

        assertThat(result.getCode()).isEqualTo("USD");
        verify(jpaRepository).save(entity);
    }

    @Test
    void deleteByCode_shouldCallRepository() {
        adapter.deleteByCode("USD");

        verify(jpaRepository).deleteById("USD");
    }
}

