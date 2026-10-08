package com.garbage.parser;

/**
 * 일본어 요일 문자열을 CollectionScheduleDto에서 사용하는
 * 숫자 값으로 변환하는 클래스
 *
 * dayOfWeek 기준
 * 1 = 월요일
 * 2 = 화요일
 * 3 = 수요일
 * 4 = 목요일
 * 5 = 금요일
 * 6 = 토요일
 * 7 = 일요일
 */
public class DayOfWeekParser {

    public static Integer parse(String day) {

        if (day == null) {
            return null;
        }

        return switch (day.trim()) {
            case "月" -> 1;
            case "火" -> 2;
            case "水" -> 3;
            case "木" -> 4;
            case "金" -> 5;
            case "土" -> 6;
            case "日" -> 7;
            default -> null;
        };
    }
}