package com.garbage.parser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.garbage.dto.CollectionScheduleDto;
import com.garbage.parser.dto.ScheduleSourceDto;

/**
 * ScheduleSourceDto를 CollectionScheduleDto 목록으로 변환하는 클래스
 *
 * ScheduleSourceDto:
 *   渋谷区 공식 HTML에서 파싱한 원본 수거요일 정보
 *
 * CollectionScheduleDto:
 *   Oracle COLLECTION_SCHEDULE 테이블에 넣기 위한 DTO
 *
 * 중요한 점:
 *
 * 水曜日・土曜日
 * → DTO 2개 생성
 *
 * 第2月曜日
 * → DTO 1개 생성
 *    dayOfWeek = 1
 *    weekPattern = 第2
 */
public class ScheduleDtoConverter {

    /**
     * 하나의 ScheduleSourceDto를
     * 여러 개의 CollectionScheduleDto로 변환한다.
     *
     * @param source 파싱된 지역 수거요일 정보
     * @param municipalityId 자치단체 ID
     * @param areaId 지역 ID
     * @param burnableCategoryId 가연성 쓰레기 category_id
     * @param nonBurnableCategoryId 불연성 쓰레기 category_id
     * @param resourceCategoryId 자원 category_id
     * @param sourceId 원본 데이터 source_id
     * @return CollectionScheduleDto 목록
     */
    public static List<CollectionScheduleDto> convert(
            ScheduleSourceDto source,
            Long municipalityId,
            Long areaId,
            Long burnableCategoryId,
            Long nonBurnableCategoryId,
            Long resourceCategoryId,
            Long sourceId) {

        List<CollectionScheduleDto> result = new ArrayList<>();

        if (source == null) {
            return result;
        }

        /*
         * 공식 사이트의 수거 마감시간
         *
         * 일반 지역 → 08:00
         * 일부 번화가 → 07:30
         */
        LocalTime collectionTimeLimit = null;

        if (source.getCollectionTimeLimit() != null
                && !source.getCollectionTimeLimit().isBlank()) {

            collectionTimeLimit =
                    LocalTime.parse(
                            source.getCollectionTimeLimit()
                    );
        }

        /*
         * 可燃ごみ
         *
         * 예:
         * 水曜日・土曜日
         *
         * → dayOfWeek 3
         * → dayOfWeek 6
         */
        List<Integer> burnableDays =
                ScheduleDayParser.parseRegularDays(
                        source.getBurnableSchedule()
                );

        for (Integer dayOfWeek : burnableDays) {

            result.add(
                    createSchedule(
                            municipalityId,
                            areaId,
                            burnableCategoryId,
                            dayOfWeek,
                            null,
                            collectionTimeLimit,
                            sourceId
                    )
            );
        }

        /*
         * 不燃ごみ
         *
         * 예:
         * 第2月曜日
         *
         * → dayOfWeek = 1
         * → weekPattern = 第2
         */
        Integer nonBurnableDay =
                ScheduleDayParser.parseMonthlyDay(
                        source.getNonBurnableSchedule()
                );

        String nonBurnableWeekPattern =
                ScheduleDayParser.parseWeekPattern(
                        source.getNonBurnableSchedule()
                );

        if (nonBurnableDay != null) {

            result.add(
                    createSchedule(
                            municipalityId,
                            areaId,
                            nonBurnableCategoryId,
                            nonBurnableDay,
                            nonBurnableWeekPattern,
                            collectionTimeLimit,
                            sourceId
                    )
            );
        }

        /*
         * 資源
         *
         * 예:
         * 火曜日
         *
         * → dayOfWeek = 2
         */
        List<Integer> resourceDays =
                ScheduleDayParser.parseRegularDays(
                        source.getResourceSchedule()
                );

        for (Integer dayOfWeek : resourceDays) {

            result.add(
                    createSchedule(
                            municipalityId,
                            areaId,
                            resourceCategoryId,
                            dayOfWeek,
                            null,
                            collectionTimeLimit,
                            sourceId
                    )
            );
        }

        return result;
    }

    /**
     * CollectionScheduleDto 하나를 생성한다.
     */
    private static CollectionScheduleDto createSchedule(
            Long municipalityId,
            Long areaId,
            Long categoryId,
            Integer dayOfWeek,
            String weekPattern,
            LocalTime collectionTimeLimit,
            Long sourceId) {

        CollectionScheduleDto dto =
                new CollectionScheduleDto();

        dto.setMunicipalityId(municipalityId);
        dto.setAreaId(areaId);
        dto.setCategoryId(categoryId);

        dto.setDayOfWeek(dayOfWeek);
        dto.setWeekPattern(weekPattern);

        dto.setCollectionTimeLimit(
                collectionTimeLimit
        );

        /*
         * 현재 HTML은 적용 시작일/종료일을 제공하지 않으므로
         * 일단 null.
         */
        dto.setEffectiveFrom(null);
        dto.setEffectiveTo(null);

        dto.setNotes(null);

        dto.setSourceId(sourceId);

        return dto;
    }
    
    public static void main(String[] args) {

        ScheduleSourceDto source =
                new ScheduleSourceDto();

        source.setTownName("上原");
        source.setDistrictName("1丁目");

        source.setBurnableSchedule("水曜日・土曜日");
        source.setNonBurnableSchedule("第2月曜日");
        source.setResourceSchedule("火曜日");

        source.setCollectionTimeLimit("08:00");

        List<CollectionScheduleDto> result =
                ScheduleDtoConverter.convert(
                        source,

                        1L,  // municipalityId
                        10L, // areaId

                        1L,  // BURNABLE
                        2L,  // NON_BURNABLE
                        3L,  // RESOURCE

                        1L   // sourceId
                );

        System.out.println(
                "생성된 일정 DTO 개수 = "
                + result.size()
        );

        for (CollectionScheduleDto dto : result) {

            System.out.println(
                    "municipalityId = "
                    + dto.getMunicipalityId()
                    + " | areaId = "
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
}