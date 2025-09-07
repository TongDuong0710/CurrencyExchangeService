package com.example.api.configurations;

import com.example.api.constant.APIConstants;
import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.ofPattern;

@Log4j2
@Component
public class FeignLogger extends Logger {
    private static final Marker senderMarker = MarkerManager.getMarker("SENDER");
    private static final Marker receiverMarker = MarkerManager.getMarker("RECEIVER");

    private static final String SPAN_ID_KEY = "SPAN_ID";

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (!log.isInfoEnabled()) {
            return;
        }

        // generate a new span ID per request
        String spanId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(SPAN_ID_KEY, spanId);

        byte[] body = ObjectUtils.isEmpty(request.body()) ? new byte[0] : request.body();
        String jsonData = new String(body);

        String prefixLog =
                String.format(
                        "\n[%s][%s][%s] ",
                        java.time.ZonedDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ssXXX")),
                        MDC.get(APIConstants.REQUEST_ID_KEY),
                        MDC.get(SPAN_ID_KEY));

        StringBuilder sb = new StringBuilder(prefixLog);
        sb.append(String.format("FEIGN REQUEST URL = [%s] METHOD = [%s]",
                request.url(), request.httpMethod().name()));

        sb.append(prefixLog);
        appendHeaders(sb, request.headers());
        sb.append(prefixLog);

        if (ObjectUtils.isNotEmpty(jsonData)) {
            sb.append("Body = ").append(jsonData);
        }

        log.info(senderMarker, sb.toString());
    }

    @Override
    protected Response logAndRebufferResponse(
            String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {

        log(configKey, "Executed in %d ms", elapsedTime);

        byte[] body =
                ObjectUtils.isEmpty(response.body())
                        ? new byte[]{}
                        : Util.toByteArray(response.body().asInputStream());
        int status = response.status();

        String prefixLog =
                String.format(
                        "\n[%s][%s][%s] ",
                        java.time.ZonedDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ssXXX")),
                        MDC.get(APIConstants.REQUEST_ID_KEY),
                        MDC.get(SPAN_ID_KEY));

        StringBuilder sb = new StringBuilder(prefixLog);
        sb.append(String.format("FEIGN RESPONSE URL = [%s] METHOD = [%s] STATUS = [%d]",
                response.request().url(), response.request().httpMethod().name(), status));

        sb.append(prefixLog);
        appendHeaders(sb, response.headers());
        sb.append(prefixLog);

        String jsonData = new String(body);
        if (ObjectUtils.isNotEmpty(jsonData)) {
            sb.append("Body = ").append(jsonData);
        }

        log.info(receiverMarker, sb.toString());

        // clean up MDC after response is logged
        MDC.remove(SPAN_ID_KEY);

        return response.toBuilder().body(body).build();
    }

    protected void log(String configKey, String format, Object... args) {
        log.debug(this.format(configKey, format, args));
    }

    protected String format(String configKey, String format, Object... args) {
        return String.format(methodTag(configKey) + format, args);
    }

    private void appendHeaders(StringBuilder sb, Map<String, Collection<String>> headers) {
        sb.append("Headers = [");
        StringJoiner joiner = new StringJoiner(",", "", "");
        headers.forEach((key, value) -> joiner.add(String.format("%s: %s", key, value)));
        sb.append(joiner);
        sb.append(" ]");
    }
}
