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
public class RawImportAutoIntegrationTestRunner implements CommandLineRunner {

    private final ImportBatchService importBatchService;
    private final RawRecordRepository rawRecordRepository;
    private final RawRecordSaveService rawRecordSaveService;
    private final RawImportService rawImportService;

    public RawImportAutoIntegrationTestRunner(
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
            "===== RawImport 자동 통합 테스트 시작 ====="
        );

        /*
         * 기준 Batch
         * 26번 Batch를 복사해서
         *
         * 1. アイロン → UPDATED
         * 2. ウレタン → DELETED
         * 3. 통합테스트신규品目 → INSERTED
         *
         * 를 동시에 발생시킨다.
         */

        Long oldBatchId = 26L;

        ImportBatch oldBatch =
            importBatchService.findById(oldBatchId);

        System.out.println(
            "기준 Batch 조회 완료"
            + " | batchId = " + oldBatch.getBatchId()
            + " | sourceId = " + oldBatch.getSourceId()
        );

        /*
         * 새로운 ImportBatch 생성
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
         * 기존 RawRecord 조회
         */
        List<RawRecord> oldRecords =
            rawRecordRepository.findByBatchId(oldBatchId);

        int copiedCount = 0;
        int updatedTargetCount = 0;
        int deletedTargetCount = 0;

        /*
         * 기존 데이터 복사
         */
        for (RawRecord oldRecord : oldRecords) {

            String rawData = oldRecord.getRawData();

            /*
             * UPDATED 테스트
             *
             * アイロン의 데이터를 변경한다.
             */
            if (rawData != null
                    && rawData.contains("\"品名\": \"アイロン\"")) {

                rawData = """
                    {
                        "分類": "粗大ごみ",
                        "品名": "アイロン",
                        "出し方・注意点": "統合テストによるUPDATED変更です"
                    }
                    """;

                updatedTargetCount++;

                System.out.println(
                    "UPDATED 테스트 대상 변경"
                    + " | item = アイロン"
                );
            }

            /*
             * DELETED 테스트
             *
             * ウレタン은 새 Batch에 복사하지 않는다.
             */
            if (rawData != null
                    && rawData.contains("\"品名\": \"ウレタン\"")) {

                deletedTargetCount++;

                System.out.println(
                    "DELETED 테스트 대상 제외"
                    + " | item = ウレタン"
                );

                continue;
            }

            /*
             * 기존 RawRecord 저장
             */
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

        /*
         * INSERTED 테스트
         *
         * 기존 Batch에는 존재하지 않는 새로운 품목을 추가한다.
         */
        String insertedRawData = """
            {
                "分類": "不燃ごみ",
                "品名": "統合テスト新規品目",
                "出し方・注意点": "統合テストによるINSERTEDデータです"
            }
            """;

        rawRecordSaveService.save(
            newBatch.getBatchId(),
            oldBatch.getDocumentId(),
            null,
            "DATA",
            9999,
            insertedRawData,
            insertedRawData,
            null
        );

        System.out.println(
            "INSERTED 테스트 대상 추가"
            + " | item = 統合テスト新規品目"
        );

        /*
         * Import 완료
         *
         * 여기서 실제 운영 흐름이 시작된다.
         *
         * completeImport()
         *  ↓
         * 이전 SUCCESS Batch 탐색
         *  ↓
         * ChangeDetection
         *  ↓
         * ChangeApply
         */
        int totalCount = copiedCount + 1;

        rawImportService.completeImport(
            newBatch.getBatchId(),
            1L,
            totalCount,
            totalCount,
            0,
            0,
            0,
            0
        );

        System.out.println(
            "===== RawImport 자동 통합 테스트 종료 ====="
            + " | copied = " + copiedCount
            + " | updatedTarget = " + updatedTargetCount
            + " | deletedTarget = " + deletedTargetCount
            + " | insertedTarget = 1"
        );
    }
}