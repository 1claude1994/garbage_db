package com.garbage.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class GarbageItemParser {

    public List<GarbageItemData> parse(String html) {

        Document document = Jsoup.parse(html);

        List<GarbageItemData> results = new ArrayList<>();
        Map<String, Integer> categoryCounts = new HashMap<>();

        Elements tables = document.select("table");

        /*
         * 마지막 table은 문의처이므로 제외
         *
         * table[0] ~ table[9] : 쓰레기 품목 데이터
         * table[10]           : 문의처
         */
        for (int i = 0; i < tables.size() - 1; i++) {

            Element table = tables.get(i);

            Elements rows = table.select("tr");

            for (Element row : rows) {

                Elements cells = row.select("th, td");

                if (cells.size() < 2) {
                    continue;
                }
                
                String itemName = cells.get(0).text();

                String category = cells.get(1).text();

                // 테이블 헤더 제외
                if ("品名".equals(itemName)) {
                    continue;
                }

                categoryCounts.put(
                        category,
                        categoryCounts.getOrDefault(category, 0) + 1
                );

                System.out.println(
                        "품목 = " + itemName
                        + " | 분류 = " + category
                );

                String instruction = "";

                if (cells.size() >= 3) {
                    instruction = cells.get(2).text();
                }

//                if ("アイロン".equals(itemName)) {
//                    continue;
//                }
                
                results.add(
                        new GarbageItemData(
                                itemName,
                                category,
                                instruction
                        )
                );
            }
        }
        
        System.out.println(
                "===== 分類별 집계 ====="
        );

        categoryCounts.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Integer>comparingByValue()
                                .reversed()
                )
                .forEach(entry ->
                        System.out.println(
                                entry.getKey()
                                + " = "
                                + entry.getValue()
                        )
                );

        System.out.println(
                "======================"
        );

        return results;
    }
}