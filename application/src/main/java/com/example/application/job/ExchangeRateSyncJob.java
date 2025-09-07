package com.example.application.job;

import com.example.application.dto.CurrencyDto;
import com.example.application.service.CurrencyApplicationService;
import com.example.application.service.ExchangeRateApplicationService;
import com.example.application.service.ExchangeRateAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateSyncJob {

    private final ExchangeRateApplicationService exchangeRateService;
    private final CurrencyApplicationService currencyService;
    private final ExchangeRateAsyncService exchangeRateAsyncService;

    //    @Scheduled(cron = "0 0 */6 * * *")  // Run every 6 hours.
    @Scheduled(cron = "0 * * * * *") // run every minute for testing
    public void syncExchangeRatesAsync() {
        // generate new request ID
        String requestId = java.util.UUID.randomUUID().toString();
        MDC.put("X-Request-ID", requestId);

        log.info("Starting ASYNC scheduled synchronization of exchange rates... [requestId={}]", requestId);

        try {
            List<CurrencyDto> currencies = currencyService.getAllCurrencies();

            if (currencies.isEmpty()) {
                log.warn("No currencies found in DB. Skipping sync.");
                return;
            }

            // run async for each base currency
            List<CompletableFuture<Void>> futures = currencies.stream()
                    .map(c -> exchangeRateAsyncService.fetchAndSaveRates(c.getCode()))
                    .toList();

            // wait for all async tasks to finish
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            log.info("Exchange rate synchronization completed (ASYNC). [requestId={}]", requestId);
        } catch (Exception e) {
            log.error("Error during exchange rate synchronization [requestId={}]", requestId, e);
        } finally {
            // clear MDC after job ends
            MDC.remove("X-Request-ID");
        }
    }
}
