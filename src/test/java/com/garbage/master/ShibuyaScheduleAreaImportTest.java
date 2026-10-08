package com.garbage.master;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShibuyaScheduleAreaImportTest {

    @Autowired
    private ShibuyaScheduleAreaImportService importService;

    @Test
    void importAreas() throws Exception {

        String url =
                "https://www.city.shibuya.tokyo.jp/"
                + "kurashi/gomi/kateigomi/gomid.html";

        importService.importAreas(url, 1L);
    }
}