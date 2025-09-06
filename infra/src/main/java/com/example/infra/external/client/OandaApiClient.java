package com.example.infra.external.client;


import com.example.infra.external.dto.OandaApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "oanda-api",
        url = "${oanda.api.url}", // configurable in application.yml
        configuration = OandaFeignConfiguration.class
)
public interface OandaApiClient {

    @GetMapping("/cc-api/currencies")
    OandaApiResponse getRates(
            @RequestParam("base") String baseCurrency,
            @RequestParam("quote") String quoteCurrency,
            @RequestParam(value = "data_type", required = false) String dataType,
            @RequestParam(value = "start_date", required = false) String startDate,
            @RequestParam(value = "end_date", required = false) String endDate
    );
}