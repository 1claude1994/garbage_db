package com.garbage;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.raw.RawRecord;
import com.garbage.raw.ImportBatchService;
import com.garbage.raw.RawImportService;
import com.garbage.raw.RawRecordSaveService;
import com.garbage.repository.raw.RawRecordRepository;

//@Component
public class RawImportAutoDeletedTestRunner implements CommandLineRunner {

    private final ImportBatchService importBatchService;
    private final RawRecordRepository rawRecordRepository;
    private final RawRecordSaveService rawRecordSaveService;
    private final RawImportService rawImportService;

    public RawImportAutoDeletedTestRunner(
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
                "===== RawImport 자동 DELETED 반영 테스트 시작 ====="
        );

        /*
         * 1. 기존 Batch 조회
         *
         * Batch 26에는 실제 품목 데이터가 존재한다.
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
         * 3. Batch 26의 RawRecord 복사
         *
         * 단, "空き箱 （紙製）"은 제외한다.
         *
         * 기존 Batch에는 존재하지만
         * 새로운 Batch에는 존재하지 않게 만들어
         * DELETED 변경을 발생시킨다.
         */
        List<RawRecord> oldRecords =
                rawRecordRepository.findByBatchId(26L);

        int copiedCount = 0;
        int deletedTargetCount = 0;

        for (RawRecord oldRecord : oldRecords) {

            String rawData = oldRecord.getRawData();

            /*
             * DELETED 테스트 대상 제외
             */
            if (rawData != null
                    && rawData.contains("\"品名\": \"空き箱 （紙製）\"")) {

                System.out.println(
                        "DELETED 테스트 대상 제외"
                                + " | item = 空き箱 （紙製）"
                );

                deletedTargetCount++;
                continue;
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
                        + " | deletedTarget = " + deletedTargetCount
        );

        /*
         * 4. 실제 RawImportService.completeImport() 호출
         *
         * Batch SUCCESS
         * → 이전 SUCCESS Batch 조회
         * → ChangeDetectionService
         * → ChangeHistory 저장
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
                "===== RawImport 자동 DELETED 반영 테스트 종료 ====="
        );
    }
}