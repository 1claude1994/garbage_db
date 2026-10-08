package com.garbage.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FeeParser {

    public static List<FeeSourceDto> parse(String url)
            throws Exception {

        List<FeeSourceDto> result =
                new ArrayList<>();

        Document doc = Jsoup.connect(url)
                .get();

        System.out.println(
                "요금 페이지 제목 = " + doc.title()
        );

        Elements tables = doc.select("table");

        System.out.println(
                "요금 테이블 수 = " + tables.size()
        );

        for (Element table : tables) {

            Elements rows = table.select("tr");

            for (Element row : rows) {

                Elements cells =
                        row.select("th, td");

                if (cells.size() < 4) {
                    continue;
                }

                String itemName =
                        cells.get(0).text().trim();

                String amountText =
                        cells.get(1).text().trim();

                String aTicket =
                        cells.get(2).text().trim();

                String bTicket =
                        cells.get(3).text().trim();

                if ("表示名".equals(itemName)
                        || "費用".equals(amountText)) {
                    continue;
                }

                if (itemName.isBlank()) {
                    continue;
                }

                String normalizedAmount =
                        amountText
                                .replace(",", "")
                                .replace("円", "")
                                .trim();

                int amount;

                try {
                    amount =
                            Integer.parseInt(
                                    normalizedAmount
                            );
                } catch (NumberFormatException e) {
                    continue;
                }

                FeeSourceDto dto =
                        new FeeSourceDto();

                dto.setItemName(itemName);
                dto.setAmount(amount);
                dto.setATicket(aTicket);
                dto.setBTicket(bTicket);

                result.add(dto);
            }
        }

        return result;
    }

    public static class FeeSourceDto {

        private String itemName;
        private Integer amount;
        private String aTicket;
        private String bTicket;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getATicket() {
            return aTicket;
        }

        public void setATicket(String aTicket) {
            this.aTicket = aTicket;
        }

        public String getBTicket() {
            return bTicket;
        }

        public void setBTicket(String bTicket) {
            this.bTicket = bTicket;
        }
    }
}