package com.garbage;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.raw.RawRecord;
import com.garbage.raw.ImportBatchService;
import com.garbage.raw.RawRecordSaveService;
import com.garbage.repository.raw.RawRecordRepository;

//@Component
public class DeletedTestRunner implements CommandLineRunner {

    private final ImportBatchService importBatchService;
    private final RawRecordRepository rawRecordRepository;
    private final RawRecordSaveService rawRecordSaveService;

    public DeletedTestRunner(
            ImportBatchService importBatchService,
            RawRecordRepository rawRecordRepository,
            RawRecordSaveService rawRecordSaveService) {

        this.importBatchService = importBatchService;
        this.rawRecordRepository = rawRecordRepository;
        this.rawRecordSaveService = rawRecordSaveService;
    }

    @Override
    public void run(String... args) {

        System.out.println(
                "===== DELETED 테스트 Batch 생성 시작 ====="
        );

        /*
         * 1. Batch 17 조회
         *
         * Batch 17에는
         * 自動INSERTテスト品目
         * 가 포함되어 있다.
         */
        ImportBatch oldBatch =
                importBatchService.findById(17L);

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
                "새 Batch 생성 완료"
                        + " | batchId = " + newBatch.getBatchId()
        );

        /*
         * 3. Batch 17의 RawRecord 전체 조회
         */
        List<RawRecord> oldRecords =
                rawRecordRepository.findByBatchId(17L);

        /*
         * 4. 自動INSERTテスト品目을 제외하고 복사
         */
        int copiedCount = 0;
        int deletedCount = 0;

        for (RawRecord oldRecord : oldRecords) {

            String rawData = oldRecord.getRawData();

            /*
             * 테스트 품목이면 복사하지 않는다.
             */
            if (rawData != null
                    && rawData.contains("自動INSERTテスト品目")) {

                deletedCount++;

                System.out.println(
                        "DELETED 테스트 품목 제외"
                                + " | item = 自動INSERTテスト品目"
                );

                continue;
            }

            rawRecordSaveService.save(
                    newBatch.getBatchId(),
                    oldRecord.getDocumentId(),
                    oldRecord.getResponseId(),
                    oldRecord.getRecordType(),
                    oldRecord.getSourceRowNumber(),
                    oldRecord.getRawData(),
                    oldRecord.getRawText(),
                    null
            );

            copiedCount++;
        }

        System.out.println(
                "기존 RawRecord 복사 완료"
                        + " | copied = " + copiedCount
                        + " | deleted = " + deletedCount
        );

        /*
         * 5. Batch SUCCESS 처리
         *
         * 225개 → 224개가 되어야 한다.
         */
        importBatchService.completeBatch(
                newBatch.getBatchId(),
                copiedCount,
                copiedCount,
                1,
                0,
                copiedCount,
                0
        );

        System.out.println(
                "===== DELETED 테스트 Batch 생성 완료 ====="
                        + " | batchId = " + newBatch.getBatchId()
        );
    }
}