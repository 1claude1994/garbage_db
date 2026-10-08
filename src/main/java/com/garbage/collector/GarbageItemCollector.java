package com.garbage.collector;

import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garbage.domain.raw.ImportBatch;
import com.garbage.raw.RawImportService;

@Service
@Transactional
public class GarbageItemCollector {

    private static final String SOURCE_URL =
            "https://www.city.shibuya.tokyo.jp/"
            + "kurashi/gomi/kateigomi/gomi_hinmoku.html";

    private static final Long SOURCE_ID = 1L;

    private static final Long MUNICIPALITY_ID = 1L;

    private final GarbageItemParser garbageItemParser;

    private final RawImportService rawImportService;

    private final ObjectMapper objectMapper;

    public GarbageItemCollector(
            GarbageItemParser garbageItemParser,
            RawImportService rawImportService,
            ObjectMapper objectMapper) {

        this.garbageItemParser = garbageItemParser;

        this.rawImportService = rawImportService;

        this.objectMapper = objectMapper;
    }

    public ImportBatch collect() {

        ImportBatch batch = null;

        try {

            System.out.println(
                    "===== 渋谷区ごみ品目 수집 시작 ====="
            );

            /*
             * 1. 공식 사이트 HTML 가져오기
             */

            String html =
                    Jsoup.connect(SOURCE_URL)
                            .get()
                            .html();

            System.out.println(
                    "공식 사이트 HTML 수집 완료"
            );

            /*
             * 2. Parser 실행
             */

            List<GarbageItemData> items =
                    garbageItemParser.parse(html);

            System.out.println(
                    "Parser 완료"
                    + " | 수집 품목 수 = " + items.size()
            );

            /*
             * 3. Import Batch 시작
             */

            batch =
                    rawImportService.startImport(
                            SOURCE_ID,
                            null,
                            1L
                    );

            System.out.println(
                    "ImportBatch 생성 완료"
                    + " | batchId = " + batch.getBatchId()
            );

            /*
             * 4. Parser 결과를 RawRecord로 저장
             */

            int processedCount = 0;

            for (int i = 0; i < items.size(); i++) {

                GarbageItemData item = items.get(i);

                String rawData =
                        objectMapper.writeValueAsString(
                                new RawItemData(
                                        item.getCategory(),
                                        item.getItemName(),
                                        item.getInstruction()
                                )
                        );

                rawImportService.saveRecord(
                        batch.getBatchId(),
                        1L,
                        null,
                        "GARBAGE_ITEM",
                        i + 1,
                        rawData,
                        rawData,
                        null
                );

                processedCount++;
            }

            /*
             * 5. Import 완료
             *
             * 이전 SUCCESS Batch 조회
             * → ChangeDetection
             * → ChangeApply
             * 가 자동으로 실행된다.
             */

            batch = rawImportService.completeImport(
                    batch.getBatchId(),
                    MUNICIPALITY_ID,
                    items.size(),
                    processedCount,
                    0,
                    0,
                    0,
                    0
            );

            System.out.println(
                    "===== 渋谷区ごみ品目 수집 완료 ====="
                    + " | batchId = " + batch.getBatchId()
                    + " | records = " + items.size()
            );
            
            return batch;

        } catch (Exception e) {

            System.out.println(
                    "===== 渋谷区ごみ品目 수집 실패 ====="
            );

            if (batch != null) {

                rawImportService.failImport(
                        batch.getBatchId(),
                        e.getMessage()
                );
            }

            throw new IllegalStateException(
                    "渋谷区ごみ品目 수집 실패",
                    e
            );
        }
    }

    /*
     * Raw에 저장할 JSON 구조
     *
     * {
     *   "分類": "...",
     *   "品名": "...",
     *   "出し方・注意点": "..."
     * }
     */

    private static class RawItemData {

        @JsonProperty("分類")
        private final String category;

        @JsonProperty("品名")
        private final String itemName;

        @JsonProperty("出し方・注意点")
        private final String instruction;

        public RawItemData(
                String category,
                String itemName,
                String instruction) {

            this.category = category;

            this.itemName = itemName;

            this.instruction = instruction;
        }

        public String getCategory() {

            return category;
        }

        public String getItemName() {

            return itemName;
        }

        public String getInstruction() {

            return instruction;
        }
    }
}