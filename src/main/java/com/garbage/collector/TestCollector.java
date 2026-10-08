package com.garbage.collector;

import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.raw.RawRecord;
import com.garbage.raw.RawImportService;
import org.springframework.stereotype.Component;

@Component
public class TestCollector implements Collector {

    private final RawImportService rawImportService;

    public TestCollector(RawImportService rawImportService) {
        this.rawImportService = rawImportService;
    }

    @Override
    public void collect() {

        // 1. ImportBatch 시작
        ImportBatch batch =
                rawImportService.startImport(
                        1L,     // sourceId
                        null,   // apiSourceId
                        1L      // documentId
                );

        System.out.println(
                "Collector 시작"
                + " | batchId = " + batch.getBatchId()
                + " | sourceId = " + batch.getSourceId()
                + " | documentId = " + batch.getDocumentId()
        );

        try {

            // 2. 실제 수집 데이터라고 가정
            String rawData = """
                    {
                        "ごみの品目": "燃やすごみ",
                        "インデックス": "001",
                        "説明": "可燃ごみとして出してください",
                        "GIS搭載用住所": "渋谷区渋谷1丁目"
                    }
                    """;

            // 3. RawRecord 저장
            RawRecord record =
                    rawImportService.saveRecord(
                            batch.getBatchId(),
                            1L,           // documentId
                            null,         // responseId
                            "WASTE_ITEM",
                            1,
                            rawData,
                            "燃やすごみ / 可燃ごみとして出してください",
                            null
                    );

            System.out.println(
                    "Collector RawRecord 저장 완료"
                    + " | recordId = " + record.getRecordId()
            );

            // 4. Batch 완료
            batch =
                    rawImportService.completeImport(
                            batch.getBatchId(),
                            1L,   // municipalityId
                            1,    // recordsFound
                            1,    // recordsProcessed
                            1,    // recordsInserted
                            0,    // recordsUpdated
                            0,    // recordsSkipped
                            0     // errorCount
                    );

            System.out.println(
                    "Collector 완료"
                    + " | batchId = " + batch.getBatchId()
                    + " | status = " + batch.getStatus()
            );

        } catch (Exception e) {

            rawImportService.failImport(
                    batch.getBatchId(),
                    e.getMessage()
            );

            throw e;
        }
    }
}