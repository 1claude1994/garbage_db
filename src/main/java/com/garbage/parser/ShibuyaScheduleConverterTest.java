package com.garbage.parser;

import java.util.List;

import com.garbage.dto.AreaDto;
import com.garbage.dto.CollectionScheduleDto;
import com.garbage.parser.dto.ScheduleSourceDto;

/**
 * 渋谷区 공식 수거요일 데이터를
 * AreaDto와 CollectionScheduleDto로 변환하는
 * 전체 통합 테스트 클래스
 *
 * ※ 현재는 Oracle DB가 없으므로
 *    DB INSERT는 하지 않는다.
 *
 * ※ areaId와 categoryId는 테스트용 ID를 사용한다.
 */
public class ShibuyaScheduleConverterTest {

    public static void main(String[] args)
            throws Exception {

        String url =
                "https://www.city.shibuya.tokyo.jp/"
                + "kurashi/gomi/kateigomi/gomid.html";

        /*
         * 1. 공식 HTML 파싱
         */
        List<ScheduleSourceDto> sources =
                ShibuyaScheduleParser.parse(url);

        System.out.println(
                "================================="
        );

        System.out.println(
                "파싱된 지역 수 = "
                + sources.size()
        );

        System.out.println(
                "================================="
        );

        /*
         * 2. AreaDto 생성
         *
         * municipalityId = 1
         * → 현재 테스트용 渋谷区 ID
         */
        List<AreaDto> areas =
                ScheduleAreaListConverter.convert(
                        sources,
                        1L
                );

        System.out.println(
                "생성된 AreaDto 수 = "
                + areas.size()
        );

        /*
         * 3. AreaDto 출력
         */
        System.out.println();
        System.out.println(
                "===== AREA ====="
        );

        for (int i = 0; i < areas.size(); i++) {

            AreaDto area = areas.get(i);

            System.out.println(
                    "areaId(임시) = " + (i + 1)
                    + " | municipalityId = "
                    + area.getMunicipalityId()
                    + " | areaName = "
                    + area.getAreaName()
                    + " | areaType = "
                    + area.getAreaType()
            );
        }

        /*
         * 4. CollectionScheduleDto 생성
         *
         * 현재 Oracle이 없으므로
         * categoryId는 테스트용으로 사용한다.
         *
         * 1 = BURNABLE
         * 2 = NON_BURNABLE
         * 3 = RESOURCE
         *
         * sourceId = 1
         */
        int totalScheduleCount = 0;

        System.out.println();
        System.out.println(
                "===== COLLECTION SCHEDULE ====="
        );

        for (int i = 0; i < sources.size(); i++) {

            ScheduleSourceDto source =
                    sources.get(i);

            /*
             * AreaDto의 임시 ID와
             * ScheduleSourceDto의 순서를
             * 일단 1:1로 대응시킨다.
             */
            Long areaId = (long) (i + 1);

            List<CollectionScheduleDto> schedules =
                    ScheduleDtoConverter.convert(
                            source,

                            1L,       // municipalityId
                            areaId,   // 임시 areaId

                            1L,       // BURNABLE
                            2L,       // NON_BURNABLE
                            3L,       // RESOURCE

                            1L        // sourceId
                    );

            totalScheduleCount +=
                    schedules.size();

            /*
             * 현재 지역의 일정 출력
             */
            for (CollectionScheduleDto dto
                    : schedules) {

                System.out.println(
                        "areaId = "
                        + dto.getAreaId()
                        + " | categoryId = "
                        + dto.getCategoryId()
                        + " | dayOfWeek = "
                        + dto.getDayOfWeek()
                        + " | weekPattern = "
                        + dto.getWeekPattern()
                        + " | time = "
                        + dto.getCollectionTimeLimit()
                );
            }
        }

        System.out.println();
        System.out.println(
                "================================="
        );

        System.out.println(
                "최종 AreaDto 수 = "
                + areas.size()
        );

        System.out.println(
                "최종 CollectionScheduleDto 수 = "
                + totalScheduleCount
        );

        System.out.println(
                "================================="
        );
    }
}