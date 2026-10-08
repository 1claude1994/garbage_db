package com.garbage.collector;

import java.util.List;

import org.springframework.stereotype.Service;

import com.garbage.domain.service.CollectionSchedule;
import com.garbage.service.CollectionScheduleService;

@Service
public class CollectionScheduleCollector {

    private static final String SOURCE_URL =
            "https://www.city.shibuya.tokyo.jp/"
            + "kurashi/gomi/kateigomi/gomid.html";

    private static final Long MUNICIPALITY_ID = 1L;

    private static final Long BURNABLE_CATEGORY_ID = 1L;

    private static final Long NON_BURNABLE_CATEGORY_ID = 2L;

    private static final Long RESOURCE_CATEGORY_ID = 3L;

    private static final Long SOURCE_ID = 1L;

    private final CollectionScheduleService collectionScheduleService;

    public CollectionScheduleCollector(
            CollectionScheduleService collectionScheduleService) {

        this.collectionScheduleService =
                collectionScheduleService;
    }

    public List<CollectionSchedule> collect()
            throws Exception {

        System.out.println(
                "===== 渋谷区 수거일정 수집 시작 ====="
        );

        List<CollectionSchedule> schedules =
                collectionScheduleService.importSchedules(
                        SOURCE_URL,
                        MUNICIPALITY_ID,
                        BURNABLE_CATEGORY_ID,
                        NON_BURNABLE_CATEGORY_ID,
                        RESOURCE_CATEGORY_ID,
                        SOURCE_ID
                );

        System.out.println(
                "===== 渋谷区 수거일정 수집 완료 ====="
        );

        System.out.println(
                "수거일정 저장 건수 = "
                + schedules.size()
        );

        return schedules;
    }
}