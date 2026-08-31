package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

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
    public Map<String, String> getHolidays(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        Map<String, String> holidays = holidayClient.fetchHolidays();

        if (from == null && to == null) {
            return holidays;
        }

        Map<String, String> filteredHolidays = new TreeMap<>();
        holidays.forEach((date, name) -> {
            LocalDate holidayDate = LocalDate.parse(date);
            boolean afterFrom = from == null || !holidayDate.isBefore(from);
            boolean beforeTo = to == null || !holidayDate.isAfter(to);

            if (afterFrom && beforeTo) {
                filteredHolidays.put(date, name);
            }
        });

        return filteredHolidays;
    }
}
