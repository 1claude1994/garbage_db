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
public class InsertedTestRunner implements CommandLineRunner {

    private final ImportBatchService importBatchService;
    private final RawRecordRepository rawRecordRepository;
    private final RawRecordSaveService rawRecordSaveService;

    public InsertedTestRunner(
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
                "===== INSERTED 테스트 Batch 생성 시작 ====="
        );

        /*
         * 1. Batch 15의 정보 조회
         */
        ImportBatch oldBatch =
                importBatchService.findById(15L);

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
         * 3. Batch 15의 RawRecord 전체 조회
         */
        List<RawRecord> oldRecords =
                rawRecordRepository.findByBatchId(15L);

        /*
         * 4. 기존 224개 그대로 복사
         */
        int copiedCount = 0;

        for (RawRecord oldRecord : oldRecords) {

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
        );

        /*
         * 5. 신규 품목 1개 추가
         */
        String newRawData = """
                {
                    "分類": "燃やすごみ",
                    "品名": "自動INSERTテスト品目",
                    "出し方・注意点": "テスト用の排出方法です"
                }
                """;

        rawRecordSaveService.save(
                newBatch.getBatchId(),
                newBatch.getDocumentId(),
                null,
                "WASTE_ITEM",
                9999,
                newRawData,
                newRawData,
                null
        );

        System.out.println(
                "신규 테스트 품목 추가 완료"
                        + " | item = 自動INSERTテスト品目"
        );

        /*
         * 6. Batch SUCCESS 처리
         *
         * RawImportService.completeImport()을 직접 호출하지 않고
         * 여기서는 우선 Batch만 SUCCESS로 만든다.
         */
        importBatchService.completeBatch(
                newBatch.getBatchId(),
                copiedCount + 1,
                copiedCount + 1,
                1,
                0,
                copiedCount,
                0
        );

        System.out.println(
                "===== INSERTED 테스트 Batch 생성 완료 ====="
        );
    }
}