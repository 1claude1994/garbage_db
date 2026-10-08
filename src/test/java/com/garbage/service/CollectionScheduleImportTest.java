package com.garbage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CollectionScheduleImportTest {

    @Autowired
    private CollectionScheduleService scheduleService;

    @Test
    void importSchedules() throws Exception {

        String url =
                "https://www.city.shibuya.tokyo.jp/"
                + "kurashi/gomi/kateigomi/gomid.html";

        scheduleService.importSchedules(
                url,
                1L, // municipalityId
                1L, // BURNABLE
                2L, // NON_BURNABLE
                3L, // RESOURCE
                1L  // sourceId
        );
    }
}