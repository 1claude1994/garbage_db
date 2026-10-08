package com.garbage.parser.test;

import java.util.List;

import com.garbage.dto.AreaDto;
import com.garbage.dto.CollectionScheduleDto;
import com.garbage.parser.ScheduleAreaListConverter;
import com.garbage.parser.ScheduleDtoConverter;
import com.garbage.parser.ShibuyaScheduleParser;
import com.garbage.parser.dto.ScheduleSourceDto;

public class ShibuyaDataSummaryTest {

/*
 * ============================================================
 * 渋谷区 ごみ 데이터 취합 결과 확인
 *
 * 현재 확인하는 데이터
 *
 * ① 쓰레기 아이템
 *    - 공식 페이지 원본 224건
 *    - 페이지 정보 3건 제외
 *    - 실제 WasteItem 221건
 *    - DisposalRule 221건
 *
 * ② 수거 지역
 *    - 공식 HTML 64개 지역
 *    - AreaDto 64개
 *
 * ③ 수거 스케줄
 *    - CollectionScheduleDto 256건
 *
 * ※ 이 클래스는 DB INSERT를 하지 않는다.
 * ※ Parser / Converter 결과를 눈으로 확인하기 위한 Test 클래스이다.
 * ============================================================
 */


/*
 * ============================================================
 * 渋谷区 공식 수거요일 페이지
 * ============================================================
 */

private static final String SCHEDULE_URL =
        "https://www.city.shibuya.tokyo.jp/kurashi/gomi/kateigomi/gomid.html";


/*
 * ============================================================
 * PostgreSQL 기준 ID
 * ============================================================
 */

private static final Long MUNICIPALITY_ID = 1L;

private static final Long BURNABLE_CATEGORY_ID = 1L;
private static final Long NON_BURNABLE_CATEGORY_ID = 2L;
private static final Long RESOURCE_CATEGORY_ID = 3L;

private static final Long SOURCE_ID = 1L;


/*
 * ============================================================
 * 현재까지 검증된 아이템 데이터
 *
 * 실제 아이템 데이터는 이미 Spring 수집/정규화 과정에서
 * PostgreSQL에 생성되어 있으므로,
 * 이 Test에서는 최종 검증값을 요약해서 보여준다.
 * ============================================================
 */

private static final int RAW_RECORD_COUNT = 224;

private static final int PAGE_INFO_COUNT = 3;

private static final int WASTE_ITEM_COUNT = 221;

private static final int DISPOSAL_RULE_COUNT = 221;


/*
 * 분류별 최종 데이터
 */

private static final int BURNABLE_ITEM_COUNT = 61;

private static final int NON_BURNABLE_ITEM_COUNT = 50;

private static final int RESOURCE_ITEM_COUNT = 59;

private static final int OVERSIZED_ITEM_COUNT = 15;

private static final int OTHER_ITEM_COUNT = 36;


/*
 * 수거 가능 / 불가능
 */

private static final int COLLECTABLE_COUNT = 191;

private static final int NOT_COLLECTABLE_COUNT = 30;


public static void main(String[] args) throws Exception {

    /*
     * ========================================================
     * START
     * ========================================================
     */

    System.out.println();
    System.out.println();
    System.out.println(
            "============================================================"
    );

    System.out.println(
            "              渋谷区 ごみ 데이터 최종 취합 결과"
    );

    System.out.println(
            "============================================================"
    );


    /*
     * ========================================================
     * ① 아이템 데이터
     * ========================================================
     */

    printWasteItemSummary();


    /*
     * ========================================================
     * ② 공식 HTML 스케줄 파싱
     * ========================================================
     */

    System.out.println();
    System.out.println();
    System.out.println(
            "============================================================"
    );

    System.out.println(
            "             ② 수거 지역 / 스케줄 데이터"
    );

    System.out.println(
            "============================================================"
    );


    /*
     * 공식 HTML 파싱
     */

    List<ScheduleSourceDto> sources =
            ShibuyaScheduleParser.parse(
                    SCHEDULE_URL
            );


    System.out.println();
    System.out.println(
            "[1] 공식 HTML 파싱 결과"
    );

    System.out.println(
            "------------------------------------------------------------"
    );

    System.out.println(
            "파싱된 지역 수       : "
            + sources.size()
    );


    /*
     * ========================================================
     * ③ AreaDto 생성
     * ========================================================
     */

    List<AreaDto> areas =
            ScheduleAreaListConverter.convert(
                    sources,
                    MUNICIPALITY_ID
            );


    System.out.println();
    System.out.println(
            "[2] AreaDto 생성 결과"
    );

    System.out.println(
            "------------------------------------------------------------"
    );

    System.out.println(
            "AreaDto 수           : "
            + areas.size()
    );


    /*
     * ========================================================
     * ④ CollectionScheduleDto 생성
     * ========================================================
     */

    int totalScheduleCount = 0;

    int burnableScheduleCount = 0;

    int nonBurnableScheduleCount = 0;

    int resourceScheduleCount = 0;

    int time0800Count = 0;

    int time0730Count = 0;


    /*
     * 지역별 상세 출력
     */

    System.out.println();
    System.out.println(
            "[3] 지역별 수거 스케줄"
    );

    System.out.println(
            "------------------------------------------------------------"
    );


    for (int i = 0; i < sources.size(); i++) {

        ScheduleSourceDto source =
                sources.get(i);

        AreaDto area =
                areas.get(i);


        /*
         * 주의
         *
         * 현재는 Oracle INSERT 전 테스트이므로
         * 임시 areaId를 사용한다.
         *
         * 실제 DAO 작업에서는 Oracle에서 생성된
         * area_id를 사용해야 한다.
         */

        Long testAreaId =
                (long) (i + 1);


        List<CollectionScheduleDto> schedules =
                ScheduleDtoConverter.convert(
                        source,
                        MUNICIPALITY_ID,
                        testAreaId,
                        BURNABLE_CATEGORY_ID,
                        NON_BURNABLE_CATEGORY_ID,
                        RESOURCE_CATEGORY_ID,
                        SOURCE_ID
                );


        totalScheduleCount +=
                schedules.size();


        /*
         * 지역 기본 정보
         */

        System.out.println();

        System.out.println(
                String.format(
                        "[%02d] %s",
                        i + 1,
                        area.getAreaName()
                )
        );

        System.out.println(
                "     배출 시간 : "
                + source.getCollectionTimeLimit()
        );


        /*
         * 스케줄 상세
         */

        for (CollectionScheduleDto schedule
                : schedules) {


            String categoryName =
                    getCategoryName(
                            schedule.getCategoryId()
                    );


            String dayName =
                    getDayName(
                            schedule.getDayOfWeek()
                    );


            String weekPattern =
                    schedule.getWeekPattern();


            String scheduleText;


            if (weekPattern == null) {

                scheduleText =
                        dayName;

            } else {

                scheduleText =
                        weekPattern
                        + " "
                        + dayName;
            }


            System.out.println(
                    String.format(
                            "     %-8s : %-12s | %s",
                            categoryName,
                            scheduleText,
                            schedule.getCollectionTimeLimit()
                    )
            );


            /*
             * 카테고리 통계
             */

            if (BURNABLE_CATEGORY_ID.equals(
                    schedule.getCategoryId())) {

                burnableScheduleCount++;

            } else if (
                    NON_BURNABLE_CATEGORY_ID.equals(
                            schedule.getCategoryId())) {

                nonBurnableScheduleCount++;

            } else if (
                    RESOURCE_CATEGORY_ID.equals(
                            schedule.getCategoryId())) {

                resourceScheduleCount++;
            }


            /*
             * 배출 시간 통계
             */

            if (schedule.getCollectionTimeLimit() != null) {

                String time =
                        schedule
                            .getCollectionTimeLimit()
                            .toString();


                if ("08:00".equals(time)) {

                    time0800Count++;

                } else if ("07:30".equals(time)) {

                    time0730Count++;
                }
            }
        }
    }


    /*
     * ========================================================
     * ⑤ 스케줄 통계
     * ========================================================
     */

    System.out.println();
    System.out.println();
    System.out.println(
            "============================================================"
    );

    System.out.println(
            "                  스케줄 데이터 통계"
    );

    System.out.println(
            "============================================================"
    );


    System.out.println();

    System.out.println(
            "[지역]"
    );

    System.out.println(
            "  지역 수              : "
            + areas.size()
    );


    System.out.println();

    System.out.println(
            "[전체 스케줄]"
    );

    System.out.println(
            "  전체                 : "
            + totalScheduleCount
    );

    System.out.println(
            "  可燃ごみ             : "
            + burnableScheduleCount
    );

    System.out.println(
            "  不燃ごみ             : "
            + nonBurnableScheduleCount
    );

    System.out.println(
            "  資源                 : "
            + resourceScheduleCount
    );


    System.out.println();

    System.out.println(
            "[배출 시간]"
    );

    System.out.println(
            "  08:00                : "
            + time0800Count
    );

    System.out.println(
            "  07:30                : "
            + time0730Count
    );


    /*
     * ========================================================
     * ⑥ 최종 검증
     * ========================================================
     */

    System.out.println();
    System.out.println();
    System.out.println(
            "============================================================"
    );

    System.out.println(
            "                    최종 데이터 검증"
    );

    System.out.println(
            "============================================================"
    );


    System.out.println();

    /*
     * 아이템
     */

    check(
            "RawRecord",
            224,
            RAW_RECORD_COUNT
    );

    check(
            "페이지 정보 제외",
            3,
            PAGE_INFO_COUNT
    );

    check(
            "WasteItem",
            221,
            WASTE_ITEM_COUNT
    );

    check(
            "DisposalRule",
            221,
            DISPOSAL_RULE_COUNT
    );


    /*
     * Area
     */

    check(
            "Area",
            64,
            areas.size()
    );


    /*
     * Schedule
     */

    check(
            "CollectionSchedule",
            256,
            totalScheduleCount
    );

    check(
            "08:00 Schedule",
            208,
            time0800Count
    );

    check(
            "07:30 Schedule",
            48,
            time0730Count
    );


    /*
     * ========================================================
     * ⑦ 최종 성공 여부
     * ========================================================
     */

    boolean success =
            WASTE_ITEM_COUNT == 221
            && DISPOSAL_RULE_COUNT == 221
            && areas.size() == 64
            && totalScheduleCount == 256
            && time0800Count == 208
            && time0730Count == 48;


    System.out.println();
    System.out.println(
            "============================================================"
    );


    if (success) {

        System.out.println(
                "              ★ DATA VALIDATION SUCCESS ★"
        );

    } else {

        System.out.println(
                "              ★ DATA VALIDATION FAILED ★"
        );
    }


    System.out.println(
            "============================================================"
    );

    System.out.println();
}


/*
 * ============================================================
 * 아이템 데이터 요약
 * ============================================================
 */

private static void printWasteItemSummary() {

    System.out.println();
    System.out.println(
            "============================================================"
    );

    System.out.println(
            "                  ① 쓰레기 아이템 데이터"
    );

    System.out.println(
            "============================================================"
    );


    System.out.println();

    System.out.println(
            "[원본 데이터]"
    );

    System.out.println(
            "  RawRecord             : "
            + RAW_RECORD_COUNT
    );

    System.out.println(
            "  페이지 정보            : "
            + PAGE_INFO_COUNT
    );

    System.out.println(
            "  실제 쓰레기 품목       : "
            + WASTE_ITEM_COUNT
    );


    System.out.println();

    System.out.println(
            "[최종 테이블]"
    );

    System.out.println(
            "  WasteItem             : "
            + WASTE_ITEM_COUNT
    );

    System.out.println(
            "  DisposalRule          : "
            + DISPOSAL_RULE_COUNT
    );


    System.out.println();

    System.out.println(
            "[분류별]"
    );

    System.out.println(
            "  可燃ごみ              : "
            + BURNABLE_ITEM_COUNT
    );

    System.out.println(
            "  不燃ごみ              : "
            + NON_BURNABLE_ITEM_COUNT
    );

    System.out.println(
            "  資源                  : "
            + RESOURCE_ITEM_COUNT
    );

    System.out.println(
            "  粗大ごみ              : "
            + OVERSIZED_ITEM_COUNT
    );

    System.out.println(
            "  その他                : "
            + OTHER_ITEM_COUNT
    );


    System.out.println();

    System.out.println(
            "[수거 여부]"
    );

    System.out.println(
            "  수거 가능              : "
            + COLLECTABLE_COUNT
    );

    System.out.println(
            "  수거 불가              : "
            + NOT_COLLECTABLE_COUNT
    );
}


/*
 * ============================================================
 * category_id → 이름
 * ============================================================
 */

private static String getCategoryName(
        Long categoryId) {

    if (categoryId == null) {
        return "UNKNOWN";
    }


    if (BURNABLE_CATEGORY_ID.equals(
            categoryId)) {

        return "可燃ごみ";
    }


    if (NON_BURNABLE_CATEGORY_ID.equals(
            categoryId)) {

        return "不燃ごみ";
    }


    if (RESOURCE_CATEGORY_ID.equals(
            categoryId)) {

        return "資源";
    }


    return "UNKNOWN";
}


/*
 * ============================================================
 * day_of_week → 일본어 요일
 *
 * 1 = 月
 * 2 = 火
 * 3 = 水
 * 4 = 木
 * 5 = 金
 * 6 = 土
 * 7 = 日
 * ============================================================
 */

private static String getDayName(
        Integer dayOfWeek) {

    if (dayOfWeek == null) {
        return "UNKNOWN";
    }


    return switch (dayOfWeek) {

        case 1 -> "月曜日";
        case 2 -> "火曜日";
        case 3 -> "水曜日";
        case 4 -> "木曜日";
        case 5 -> "金曜日";
        case 6 -> "土曜日";
        case 7 -> "日曜日";

        default -> "UNKNOWN";
    };
}


/*
 * ============================================================
 * 검증 결과 출력
 * ============================================================
 */

private static void check(
        String name,
        int expected,
        int actual) {


    if (expected == actual) {

        System.out.println(
                String.format(
                        "  [PASS] %-25s : %d",
                        name,
                        actual
                )
        );

    } else {

        System.out.println(
                String.format(
                        "  [FAIL] %-25s : expected=%d, actual=%d",
                        name,
                        expected,
                        actual
                )
        );
    }
}

}
