package com.garbage.repository.raw;

import com.garbage.domain.raw.RawRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawRecordRepository
        extends JpaRepository<RawRecord, Long> {

    List<RawRecord> findByBatchId(Long batchId);

    List<RawRecord> findByResponseId(Long responseId);

    List<RawRecord> findByDocumentId(Long documentId);
}