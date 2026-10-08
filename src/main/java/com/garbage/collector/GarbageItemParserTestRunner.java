package com.garbage.collector;

import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class GarbageItemParserTestRunner implements CommandLineRunner {

    private static final String SOURCE_URL =
            "https://www.city.shibuya.tokyo.jp/"
            + "kurashi/gomi/kateigomi/gomi_hinmoku.html";

    private final GarbageItemParser garbageItemParser;

    public GarbageItemParserTestRunner(
            GarbageItemParser garbageItemParser) {

        this.garbageItemParser = garbageItemParser;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println(
                "===== 渋谷区ごみ品目 Parser 테스트 시작 ====="
        );

        String html =
                Jsoup.connect(SOURCE_URL)
                        .get()
                        .html();

        System.out.println(
                "공식 사이트 HTML 수집 완료"
        );

        List<GarbageItemData> items =
                garbageItemParser.parse(html);
        
        System.out.println(
                "===== 분류별 상세 데이터 ====="
        );

        for (GarbageItemData item : items) {

            String category = item.getCategory();

            if ("拠点回収".equals(category)
                    || "拠点回収または可燃ごみ".equals(category)
                    || "拠点回収または不燃ごみ".equals(category)
                    || "拠点回収または資源".equals(category)
                    || "個別".equals(category)
                    || "区では収集しません。".equals(category)
                    || "区で収集できないもの".equals(category)) {

                System.out.println(
                        "품목 = " + item.getItemName()
                        + " | 분류 = " + item.getCategory()
                        + " | 출し方・注意点 = "
                        + item.getInstruction()
                );
            }
        }

        System.out.println(
                "============================"
        );

        System.out.println(
                "Parser 완료"
                + " | 수집 품목 수 = " + items.size()
        );

        System.out.println(
                "===== 渋谷区ごみ品目 Parser 테스트 종료 ====="
        );
    }
}