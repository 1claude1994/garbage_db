package com.garbage;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.raw.RawRecord;
import com.garbage.raw.ImportBatchService;
import com.garbage.raw.RawRecordSaveService;
import com.garbage.raw.RawImportService;
import com.garbage.repository.raw.RawRecordRepository;

//@Component
public class RawImportAutoApplyTestRunner implements CommandLineRunner {

    private final ImportBatchService importBatchService;
    private final RawRecordRepository rawRecordRepository;
    private final RawRecordSaveService rawRecordSaveService;
    private final RawImportService rawImportService;

    public RawImportAutoApplyTestRunner(
            ImportBatchService importBatchService,
            RawRecordRepository rawRecordRepository,
            RawRecordSaveService rawRecordSaveService,
            RawImportService rawImportService) {

        this.importBatchService = importBatchService;
        this.rawRecordRepository = rawRecordRepository;
        this.rawRecordSaveService = rawRecordSaveService;
        this.rawImportService = rawImportService;
    }

    @Override
    public void run(String... args) {

        System.out.println(
                "===== RawImport 자동 UPDATED 반영 테스트 시작 ====="
        );

        /*
         * 1. Batch 22 조회
         */
        ImportBatch oldBatch =
                importBatchService.findById(26L);

        /*
         * 2. 새로운 Batch 시작
         */
        ImportBatch newBatch =
                importBatchService.startBatch(
                        oldBatch.getSourceId(),
                        oldBatch.getApiSourceId(),
                        oldBatch.getDocumentId()
                );

        System.out.println(
                "새 Batch 생성"
                        + " | batchId = " + newBatch.getBatchId()
        );

        /*
         * 3. Batch 26의 RawRecord 전체 복사
         *
         * 단, アイロン은 分類과 出し方・注意点을 변경한다.
         */
        List<RawRecord> oldRecords =
                rawRecordRepository.findByBatchId(26L);

        int copiedCount = 0;

        for (RawRecord oldRecord : oldRecords) {

            String rawData = oldRecord.getRawData();

            /*
             * アイロン 데이터만 변경
             */
            if (rawData != null
                    && rawData.contains("\"品名\": \"アイロン\"")) {

                rawData = """
                        {
                            "分類": "粗大ごみ",
                            "品名": "アイロン",
                            "出し方・注意点": "テスト変更後の排出方法です（3回目）"
                        }
                        """;

                System.out.println(
                        "アイロン 데이터 변경"
                                + " | 分類 + 出し方・注意点"
                );
            }

            rawRecordSaveService.save(
                    newBatch.getBatchId(),
                    oldRecord.getDocumentId(),
                    oldRecord.getResponseId(),
                    oldRecord.getRecordType(),
                    oldRecord.getSourceRowNumber(),
                    rawData,
                    rawData,
                    null
            );

            copiedCount++;
        }

        System.out.println(
                "기존 RawRecord 복사 완료"
                        + " | copied = " + copiedCount
        );

        /*
         * 4. 실제 RawImportService.completeImport() 호출
         *
         * Batch SUCCESS
         * → 이전 SUCCESS Batch 조회
         * → ChangeDetectionService
         * → ChangeHistory 저장
         * → ChangeHistory 조회
         * → ChangeApplyService.apply()
         * → DataChangeHistory 저장
         */
        rawImportService.completeImport(
                newBatch.getBatchId(),
                1L,
                copiedCount,
                copiedCount,
                0,
                0,
                0,
                0
        );

        System.out.println(
                "===== RawImport 자동 UPDATED 반영 테스트 종료 ====="
        );
    }
}