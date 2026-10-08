package com.garbage.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * 渋谷区 수거요일 문자열을 분석하는 클래스
 */
public class ScheduleDayParser {

    public static List<Integer> parseRegularDays(String schedule) {

        List<Integer> result = new ArrayList<>();

        if (schedule == null || schedule.isBlank()) {
            return result;
        }

        String[] days = schedule.split("・");

        for (String day : days) {

            day = day.trim();
            day = day.replace("曜日", "");

            Integer dayOfWeek = DayOfWeekParser.parse(day);

            if (dayOfWeek != null) {
                result.add(dayOfWeek);
            }
        }

        return result;
    }

    public static Integer parseMonthlyDay(String schedule) {

        if (schedule == null || schedule.isBlank()) {
            return null;
        }

        String dayPart = schedule.substring(2);
        dayPart = dayPart.replace("曜日", "");

        return DayOfWeekParser.parse(dayPart);
    }

    public static String parseWeekPattern(String schedule) {

        if (schedule == null || schedule.isBlank()) {
            return null;
        }

        if (!schedule.startsWith("第")) {
            return null;
        }

        return schedule.substring(0, 2);
    }


    // ==============================
    // 테스트용 main()
    // ==============================
    public static void main(String[] args) {

        System.out.println(
            ScheduleDayParser.parseRegularDays("水曜日・土曜日")
        );

        System.out.println(
            ScheduleDayParser.parseRegularDays("火曜日")
        );

        System.out.println(
            ScheduleDayParser.parseMonthlyDay("第2月曜日")
        );

        System.out.println(
            ScheduleDayParser.parseWeekPattern("第2月曜日")
        );
    }
}