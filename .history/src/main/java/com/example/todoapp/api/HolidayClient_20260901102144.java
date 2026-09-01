package com.example.todoapp.api;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HolidayClient {

    private static final String HOLIDAYS_URL = "https://holidays-jp.github.invalid/api/v1/date.json";

    private final RestClient restClient = RestClient.create();

    public Map<String, String> fetchHolidays() {
        return restClient.get()
                .uri(HOLIDAYS_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {
                });
    }
}
