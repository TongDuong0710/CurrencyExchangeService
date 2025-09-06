package com.example.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@Order(3)
public class WebserviceLoggingFilter extends OncePerRequestFilter {

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("application/*+json")
    );

    private static boolean isValidMediaType(ContentCachingRequestWrapper request) {
        if (ObjectUtils.isNotEmpty(request.getContentType())) {
            MediaType mediaType = MediaType.valueOf(request.getContentType());
            return VISIBLE_TYPES.stream().anyMatch(mt -> mt.includes(mediaType));
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            if (isAsyncDispatch(request)) {
                filterChain.doFilter(request, response);
            } else {
                doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
            }
        } finally {
            long duration = System.currentTimeMillis() - start;
            MDC.put("latency", String.valueOf(duration));
            log.info("Request completed: {} {} -> {} ({} ms)",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
            MDC.remove("latency");
        }
    }


    private void doFilterWrapped(ContentCachingRequestWrapper request,
                                 ContentCachingResponseWrapper response,
                                 FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isValidMediaType(request)) {
                logRequestBody(request);
            }
            filterChain.doFilter(request, response);
        } finally {
            if (isValidMediaType(request)) {
                logResponseBody(request, response);
            }
        }
    }

    private void logRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        String body = new String(content);
        String queryString = request.getQueryString();

        String prefix = prefixLog();

        StringBuilder sb = new StringBuilder(prefix);
        sb.append("INCOMING REQUEST - ")
                .append(String.format("PATH=[%s] METHOD=[%s] PARAMS=[%s]",
                        request.getRequestURI(),
                        request.getMethod(),
                        queryString));

        appendHeaders(sb, Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader)));

        if (ObjectUtils.isNotEmpty(body)) {
            sb.append(prefix).append("Body=").append(body);
        }
        log.info(sb.toString());
    }

    private void logResponseBody(ContentCachingRequestWrapper request,
                                 ContentCachingResponseWrapper response) throws IOException {
        int status = response.getStatus();
        String body = new String(response.getContentAsByteArray());
        response.copyBodyToResponse();

        String prefix = prefixLog();

        StringBuilder sb = new StringBuilder(prefix);
        sb.append("OUTGOING RESPONSE - ")
                .append(String.format("PATH=[%s] METHOD=[%s] STATUS=[%d]",
                        request.getRequestURI(),
                        request.getMethod(),
                        status));

        appendHeaders(sb, response.getHeaderNames()
                .stream()
                .collect(Collectors.toMap(h -> h, response::getHeader)));

        if (ObjectUtils.isNotEmpty(body)) {
            sb.append(prefix).append("Body=").append(body);
        }
        log.info(sb.toString());
    }

    private String prefixLog() {
        return "\n[" + ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "]"
                + "[RequestId:" + MDC.get("X-Request-ID") + "] ";
    }

    private void appendHeaders(StringBuilder sb, Map<String, String> headers) {
        sb.append(" Headers=[");
        StringJoiner joiner = new StringJoiner(", ");
        headers.forEach((k, v) -> joiner.add(k + ": " + v));
        sb.append(joiner).append(" ]");
    }

    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        return (request instanceof ContentCachingRequestWrapper)
                ? (ContentCachingRequestWrapper) request
                : new ContentCachingRequestWrapper(request);
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        return (response instanceof ContentCachingResponseWrapper)
                ? (ContentCachingResponseWrapper) response
                : new ContentCachingResponseWrapper(response);
    }
}
