package com.example.todoapp.api;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HolidayClient {

    private static final String HOLIDAYS_URL = "https://holidays-jp.github.io/api/v1/date.json";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;

    public HolidayClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public HolidayFetchResult fetchHolidays() {
        try {
            Map<String, String> holidays = restClient.get()
                    .uri(HOLIDAYS_URL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, String>>() {
                    });

            if (holidays == null) {
                return HolidayFetchResult.unavailableResult();
            }

            return HolidayFetchResult.available(holidays);
        } catch (RestClientException ex) {
            return HolidayFetchResult.unavailableResult();
        }
    }

    public record HolidayFetchResult(Map<String, String> holidays, boolean unavailable) {

        public static HolidayFetchResult available(Map<String, String> holidays) {
            return new HolidayFetchResult(holidays, false);
        }

        public static HolidayFetchResult unavailableResult() {
            return new HolidayFetchResult(Collections.emptyMap(), true);
        }
    }
}
