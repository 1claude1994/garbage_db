package com.garbage;

import com.garbage.collector.GarbageItemData;
import com.garbage.collector.GarbageItemJsonConverter;
import com.garbage.collector.GarbageItemParser;
import com.garbage.collector.WebCollector;
import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.raw.RawRecord;
import com.garbage.raw.RawImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class WebGarbageImportRunner implements CommandLineRunner {

    private final WebCollector webCollector;
    private final GarbageItemParser garbageItemParser;
    private final GarbageItemJsonConverter garbageItemJsonConverter;
    private final RawImportService rawImportService;

    public WebGarbageImportRunner(
            WebCollector webCollector,
            GarbageItemParser garbageItemParser,
            GarbageItemJsonConverter garbageItemJsonConverter,
            RawImportService rawImportService) {

        this.webCollector = webCollector;
        this.garbageItemParser = garbageItemParser;
        this.garbageItemJsonConverter = garbageItemJsonConverter;
        this.rawImportService = rawImportService;
    }

    @Override
    public void run(String... args) {

        String url =
                "https://www.city.shibuya.tokyo.jp/kurashi/gomi/kateigomi/gomi_hinmoku.html";

        System.out.println("===== Web 데이터 수집 시작 =====");

        // 1. 웹 페이지 수집
        String html = webCollector.fetch(url);

        // 2. HTML 파싱
        List<GarbageItemData> items =
                garbageItemParser.parse(html);

        System.out.println(
                "웹 페이지 수집 완료"
                + " | 추출 건수 = " + items.size()
        );

        // 3. ImportBatch 생성
        ImportBatch batch =
                rawImportService.startImport(
                        1L,     // sourceId
                        null,   // apiSourceId
                        1L      // documentId
                );

        System.out.println(
                "ImportBatch 생성"
                + " | batchId = " + batch.getBatchId()
        );

        int recordsProcessed = 0;
        int recordsInserted = 0;
        int errorCount = 0;

        try {

            // 4. 파싱된 데이터를 하나씩 RawRecord로 저장
            for (int i = 0; i < items.size(); i++) {

                GarbageItemData item = items.get(i);

                try {

                    // 5. GarbageItemData → JSON
                    String rawData =
                            garbageItemJsonConverter.convert(item);

                    // 6. 검색/확인용 원본 텍스트
                    String rawText =
                            item.getItemName()
                            + " / "
                            + item.getCategory()
                            + " / "
                            + item.getInstruction();

                    // 7. raw.raw_record 저장
                    RawRecord record =
                            rawImportService.saveRecord(
                                    batch.getBatchId(),
                                    1L,         // documentId
                                    null,       // responseId
                                    "WASTE_ITEM",
                                    i + 1,      // sourceRowNumber
                                    rawData,
                                    rawText,
                                    null        // recordHash
                            );

                    recordsProcessed++;
                    recordsInserted++;

                    // 처음 5건만 콘솔 출력
                    if (i < 5) {

                        System.out.println(
                                "RawRecord 저장"
                                + " | recordId = " + record.getRecordId()
                                + " | row = " + (i + 1)
                                + " | item = " + item.getItemName()
                        );
                    }

                } catch (Exception e) {

                    errorCount++;

                    System.out.println(
                            "RawRecord 저장 실패"
                            + " | row = " + (i + 1)
                            + " | item = " + item.getItemName()
                            + " | error = " + e.getMessage()
                    );
                }
            }

            // 8. Batch 완료 처리
            batch =
                    rawImportService.completeImport(
                            batch.getBatchId(),
                            1L,                 // municipalityId
                            items.size(),
                            recordsProcessed,
                            recordsInserted,
                            0,
                            0,
                            errorCount
                    );

            System.out.println("===== Web 데이터 수집 완료 =====");

            System.out.println(
                    "batchId = " + batch.getBatchId()
            );

            System.out.println(
                    "status = " + batch.getStatus()
            );

            System.out.println(
                    "recordsFound = " + batch.getRecordsFound()
            );

            System.out.println(
                    "recordsProcessed = "
                    + batch.getRecordsProcessed()
            );

            System.out.println(
                    "recordsInserted = "
                    + batch.getRecordsInserted()
            );

            System.out.println(
                    "errorCount = " + batch.getErrorCount()
            );

        } catch (Exception e) {

            e.printStackTrace();

            try {
                rawImportService.failImport(
                        batch.getBatchId(),
                        e.getMessage()
                );
            } catch (Exception failException) {
                failException.printStackTrace();
            }

            throw e;
        }
    }
}