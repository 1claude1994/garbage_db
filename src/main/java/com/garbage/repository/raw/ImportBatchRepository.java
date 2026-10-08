package com.garbage.repository.raw;

import com.garbage.domain.raw.ImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, Long> {

    List<ImportBatch> findBySourceId(Long sourceId);

    List<ImportBatch> findByApiSourceId(Long apiSourceId);

    List<ImportBatch> findByDocumentId(Long documentId);

    List<ImportBatch> findByStatus(String status);

    ImportBatch findTopBySourceIdAndStatusAndBatchIdLessThanOrderByBatchIdDesc(
            Long sourceId,
            String status,
            Long batchId);
}