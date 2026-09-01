package com.example.todoapp;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        YearMonth currentMonth = getTargetMonth(year, month);
        YearMonth previousMonth = currentMonth.minusMonths(1);
        YearMonth nextMonth = currentMonth.plusMonths(1);

        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("fromDate", currentMonth.atDay(1));
        model.addAttribute("toDate", currentMonth.atEndOfMonth());
        model.addAttribute("weeks", createWeeks(currentMonth));
        model.addAttribute("previousYear", previousMonth.getYear());
        model.addAttribute("previousMonth", previousMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());
        model.addAttribute("weekdays", List.of("日", "月", "火", "水", "木", "金", "土"));
        return "calendar";
    }

    private YearMonth getTargetMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            return YearMonth.now();
        }
        return YearMonth.of(year, month);
    }

    private List<List<CalendarDay>> createWeeks(YearMonth targetMonth) {
        List<List<CalendarDay>> weeks = new ArrayList<>();
        List<CalendarDay> week = new ArrayList<>();
        LocalDate firstDay = targetMonth.atDay(1);
        int leadingEmptyDays = firstDay.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < leadingEmptyDays; i++) {
            week.add(null);
        }

        for (int day = 1; day <= targetMonth.lengthOfMonth(); day++) {
            LocalDate date = targetMonth.atDay(day);
            week.add(new CalendarDay(date, date.toString(), day));

            if (week.size() == 7) {
                weeks.add(week);
                week = new ArrayList<>();
            }
        }

        if (!week.isEmpty()) {
            while (week.size() < 7) {
                week.add(null);
            }
            weeks.add(week);
        }

        return weeks;
    }

    public record CalendarDay(LocalDate date, String dateText, int dayOfMonth) {
    }
}
