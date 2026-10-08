package com.garbage.raw;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.raw.RawRecord;
import com.garbage.normalization.entity.ChangeHistory;
import com.garbage.normalization.repository.ChangeHistoryRepository;
import com.garbage.normalization.service.ChangeApplyService;
import com.garbage.normalization.service.ChangeDetectionService;

@Service
@Transactional
public class RawImportService {

    private final ChangeDetectionService changeDetectionService;
    private final ChangeApplyService changeApplyService;
    private final ChangeHistoryRepository changeHistoryRepository;
    private final ImportBatchService importBatchService;
    private final RawRecordSaveService rawRecordSaveService;

    public RawImportService(
            ImportBatchService importBatchService,
            RawRecordSaveService rawRecordSaveService,
            ChangeDetectionService changeDetectionService,
            ChangeApplyService changeApplyService,
            ChangeHistoryRepository changeHistoryRepository) {

        this.importBatchService = importBatchService;
        this.rawRecordSaveService = rawRecordSaveService;
        this.changeDetectionService = changeDetectionService;
        this.changeApplyService = changeApplyService;
        this.changeHistoryRepository = changeHistoryRepository;
    }

    public ImportBatch startImport(
            Long sourceId,
            Long apiSourceId,
            Long documentId) {

        return importBatchService.startBatch(
                sourceId,
                apiSourceId,
                documentId
        );
    }

    public RawRecord saveRecord(
            Long batchId,
            Long documentId,
            Long responseId,
            String recordType,
            Integer sourceRowNumber,
            String rawData,
            String rawText,
            String recordHash) {

        return rawRecordSaveService.save(
                batchId,
                documentId,
                responseId,
                recordType,
                sourceRowNumber,
                rawData,
                rawText,
                recordHash
        );
    }

    public ImportBatch completeImport(
            Long batchId,
            Long municipalityId,
            int recordsFound,
            int recordsProcessed,
            int recordsInserted,
            int recordsUpdated,
            int recordsSkipped,
            int errorCount) {

        ImportBatch batch =
                importBatchService.completeBatch(
                        batchId,
                        recordsFound,
                        recordsProcessed,
                        recordsInserted,
                        recordsUpdated,
                        recordsSkipped,
                        errorCount
                );

        // 정상적으로 수집이 완료된 경우에만 변경 감지
        if ("SUCCESS".equals(batch.getStatus())) {

            ImportBatch previousBatch =
                    importBatchService.findPreviousSuccessfulBatch(
                            batch.getSourceId(),
                            batch.getBatchId()
                    );

            // 이전 성공 batch가 있을 때
            if (previousBatch != null) {

                int detectedCount =
                        changeDetectionService.detectChanges(
                                municipalityId,
                                previousBatch.getBatchId(),
                                batch.getBatchId()
                        );

                System.out.println(
                        "변경 감지 완료"
                        + " | 저장 건수 = " + detectedCount
                );

                /*
                 * 방금 생성된 Batch의 변경사항만 조회하여 적용
                 */
                List<ChangeHistory> histories =
                        changeHistoryRepository
                                .findByNewBatchIdOrderByChangedAtDesc(
                                        batch.getBatchId()
                                );

                System.out.println(
                        "변경 반영 대상 조회"
                        + " | count = " + histories.size()
                );

                for (ChangeHistory history : histories) {

                    System.out.println(
                            "변경 반영 시작"
                            + " | changeId = " + history.getChangeId()
                            + " | item = " + history.getItemName()
                            + " | type = " + history.getChangeType()
                    );

                    /*
                     * 이미 처리된 이력은 다시 적용하지 않는다.
                     */
                    if (history.getProcessed()) {
                        System.out.println(
                                "이미 처리된 변경사항 건너뜀"
                                        + " | changeId = " + history.getChangeId()
                        );
                        continue;
                    }

                    changeApplyService.apply(history);
                }

            } else {

                /*
                 * 최초 성공 Batch
                 *
                 * 이전 Batch가 없으므로
                 * 현재 Batch의 모든 품목을 INSERTED로 처리한다.
                 */
                int initialDetectedCount =
                        changeDetectionService.detectInitialChanges(
                                municipalityId,
                                batch.getBatchId()
                        );

                System.out.println(
                        "최초 데이터 변경 감지 완료"
                        + " | 저장 건수 = " + initialDetectedCount
                );

                /*
                 * 방금 생성된 Batch의 변경사항만 조회하여 적용
                 */
                List<ChangeHistory> initialHistories =
                        changeHistoryRepository
                                .findByNewBatchIdOrderByChangedAtDesc(
                                        batch.getBatchId()
                                );

                System.out.println(
                        "최초 데이터 반영 대상 조회"
                        + " | count = " + initialHistories.size()
                );

                for (ChangeHistory history : initialHistories) {

                    System.out.println(
                            "최초 데이터 반영 시작"
                            + " | changeId = " + history.getChangeId()
                            + " | item = " + history.getItemName()
                            + " | type = " + history.getChangeType()
                    );

                    /*
                     * 이미 처리된 이력은 다시 적용하지 않는다.
                     */
                    if (history.getProcessed()) {
                        System.out.println(
                                "이미 처리된 변경사항 건너뜀"
                                        + " | changeId = " + history.getChangeId()
                        );
                        continue;
                    }

                    changeApplyService.apply(history);
                }
            }
        }

        return batch;
    }

    public ImportBatch failImport(
            Long batchId,
            String errorMessage) {

        return importBatchService.failBatch(
                batchId,
                errorMessage
        );
    }
}