package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holidays")
public class HolidayApiController {

    private final HolidayClient holidayClient;

    public HolidayApiController(HolidayClient holidayClient) {
        this.holidayClient = holidayClient;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getHolidays(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        HolidayClient.HolidayFetchResult result = holidayClient.fetchHolidays();
        Map<String, String> holidays = result.holidays();

        Map<String, String> responseBody;
        if (from == null && to == null) {
            responseBody = holidays;
        } else {
            Map<String, String> filteredHolidays = new TreeMap<>();
            holidays.forEach((date, name) -> {
                LocalDate holidayDate = LocalDate.parse(date);
                boolean afterFrom = from == null || !holidayDate.isBefore(from);
                boolean beforeTo = to == null || !holidayDate.isAfter(to);

                if (afterFrom && beforeTo) {
                    filteredHolidays.put(date, name);
                }
            });
            responseBody = filteredHolidays;
        }

        if (result.unavailable()) {
            return ResponseEntity.ok()
                    .header("X-Holidays-Unavailable", "true")
                    .body(responseBody);
        }

        return ResponseEntity.ok(responseBody);
    }
}
