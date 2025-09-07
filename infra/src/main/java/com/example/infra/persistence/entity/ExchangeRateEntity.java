package com.example.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "exchange_rates",
        indexes = {
                @Index(name = "idx_base_currency", columnList = "base_currency"),
                @Index(name = "idx_base_target", columnList = "base_currency, target_currency")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_base_target", columnNames = {"base_currency", "target_currency"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_currency", nullable = false, length = 10)
    private String baseCurrency;

    @Column(name = "target_currency", nullable = false, length = 10)
    private String targetCurrency;

    @Column(name = "average_bid", nullable = false)
    private double averageBid;

    @Column(name = "average_ask", nullable = false)
    private double averageAsk;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;
}
