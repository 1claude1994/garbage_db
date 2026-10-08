package com.garbage.raw;

import com.garbage.domain.raw.ImportBatch;
import com.garbage.repository.raw.ImportBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ImportBatchService {

    private final ImportBatchRepository importBatchRepository;

    public ImportBatchService(
            ImportBatchRepository importBatchRepository) {

        this.importBatchRepository = importBatchRepository;
    }

    public ImportBatch startBatch(
            Long sourceId,
            Long apiSourceId,
            Long documentId) {

        ImportBatch batch = new ImportBatch();

        batch.setSourceId(sourceId);
        batch.setApiSourceId(apiSourceId);
        batch.setDocumentId(documentId);

        batch.setStartedAt(LocalDateTime.now());
        batch.setStatus("RUNNING");

        batch.setRecordsFound(0);
        batch.setRecordsProcessed(0);
        batch.setRecordsInserted(0);
        batch.setRecordsUpdated(0);
        batch.setRecordsSkipped(0);
        batch.setErrorCount(0);

        batch.setCreatedAt(LocalDateTime.now());

        return importBatchRepository.save(batch);
    }

    public ImportBatch completeBatch(
            Long batchId,
            int recordsFound,
            int recordsProcessed,
            int recordsInserted,
            int recordsUpdated,
            int recordsSkipped,
            int errorCount) {

        ImportBatch batch =
                importBatchRepository.findById(batchId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "ImportBatch를 찾을 수 없습니다. batchId="
                                        + batchId
                        ));

        batch.setStatus(
                errorCount > 0 ? "PARTIAL" : "SUCCESS"
        );

        batch.setCompletedAt(LocalDateTime.now());

        batch.setRecordsFound(recordsFound);
        batch.setRecordsProcessed(recordsProcessed);
        batch.setRecordsInserted(recordsInserted);
        batch.setRecordsUpdated(recordsUpdated);
        batch.setRecordsSkipped(recordsSkipped);
        batch.setErrorCount(errorCount);

        return importBatchRepository.save(batch);
    }

    public ImportBatch failBatch(
            Long batchId,
            String errorMessage) {

        ImportBatch batch =
                importBatchRepository.findById(batchId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "ImportBatch를 찾을 수 없습니다. batchId="
                                        + batchId
                        ));

        batch.setStatus("FAILED");
        batch.setCompletedAt(LocalDateTime.now());
        batch.setErrorMessage(errorMessage);

        return importBatchRepository.save(batch);
    }

    @Transactional(readOnly = true)
    public ImportBatch findById(Long batchId) {

        return importBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ImportBatch를 찾을 수 없습니다. batchId="
                                + batchId
                ));
    }

    @Transactional(readOnly = true)
    public List<ImportBatch> findBySourceId(Long sourceId) {

        return importBatchRepository.findBySourceId(sourceId);
    }

    @Transactional(readOnly = true)
    public List<ImportBatch> findByDocumentId(Long documentId) {

        return importBatchRepository.findByDocumentId(documentId);
    }

    @Transactional(readOnly = true)
    public List<ImportBatch> findByStatus(String status) {

        return importBatchRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public ImportBatch findPreviousSuccessfulBatch(
            Long sourceId,
            Long currentBatchId) {

        return importBatchRepository
                .findTopBySourceIdAndStatusAndBatchIdLessThanOrderByBatchIdDesc(
                        sourceId,
                        "SUCCESS",
                        currentBatchId
                );
    }
}