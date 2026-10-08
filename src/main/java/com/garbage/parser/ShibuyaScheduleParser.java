package com.garbage.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.garbage.parser.dto.ScheduleSourceDto;

public class ShibuyaScheduleParser {

    public static List<ScheduleSourceDto> parse(String url)
            throws Exception {

        List<ScheduleSourceDto> result =
                new ArrayList<>();

        Document doc = Jsoup.connect(url)
                .get();

        System.out.println(
                "페이지 제목 : " + doc.title()
        );

        Elements tables = doc.select("table");

        for (int tableIndex = 0;
             tableIndex < 2;
             tableIndex++) {

            Element table = tables.get(tableIndex);

            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) {

                Elements cells =
                        rows.get(i).select("th, td");

                if (cells.size() != 6) {
                    continue;
                }

                ScheduleSourceDto dto =
                        new ScheduleSourceDto();

                dto.setTownName(
                        cells.get(1).text().trim()
                );

                dto.setDistrictName(
                        cells.get(2).text().trim()
                );

                dto.setBurnableSchedule(
                        cells.get(3).text().trim()
                );

                dto.setNonBurnableSchedule(
                        cells.get(4).text().trim()
                );

                dto.setResourceSchedule(
                        cells.get(5).text().trim()
                );

                /*
                 * 일반 지역
                 * → 08:00
                 *
                 * 일부 번화가 지역
                 * → 07:30
                 */
                if (tableIndex == 0) {
                    dto.setCollectionTimeLimit("08:00");
                } else {
                    dto.setCollectionTimeLimit("07:30");
                }

                result.add(dto);
            }
        }

        return result;
    }
    
    public static void main(String[] args)
            throws Exception {

        String url =
                "https://www.city.shibuya.tokyo.jp/"
                + "kurashi/gomi/kateigomi/gomid.html";

        List<ScheduleSourceDto> sources =
                ShibuyaScheduleParser.parse(url);

        System.out.println(
                "파싱된 지역 수 = "
                + sources.size()
        );

        for (ScheduleSourceDto dto : sources) {

            System.out.println(
                    dto.getTownName()
                    + " | "
                    + dto.getDistrictName()
                    + " | "
                    + dto.getBurnableSchedule()
                    + " | "
                    + dto.getNonBurnableSchedule()
                    + " | "
                    + dto.getResourceSchedule()
                    + " | "
                    + dto.getCollectionTimeLimit()
            );
        }
    }
}