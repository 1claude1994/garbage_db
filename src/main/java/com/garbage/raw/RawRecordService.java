package com.garbage.raw;

import com.garbage.domain.raw.RawRecord;
import com.garbage.repository.raw.RawRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RawRecordService {

    private final RawRecordRepository rawRecordRepository;

    public RawRecordService(RawRecordRepository rawRecordRepository) {
        this.rawRecordRepository = rawRecordRepository;
    }

    public List<RawRecord> findByBatchId(Long batchId) {

        return rawRecordRepository.findByBatchId(batchId);
    }

    public List<RawRecord> findByResponseId(Long responseId) {

        return rawRecordRepository.findByResponseId(responseId);
    }

    public List<RawRecord> findByDocumentId(Long documentId) {

        return rawRecordRepository.findByDocumentId(documentId);
    }
}