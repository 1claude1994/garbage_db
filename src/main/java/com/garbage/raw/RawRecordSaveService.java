package com.garbage.raw;

import com.garbage.domain.raw.RawRecord;
import com.garbage.repository.raw.RawRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

@Service
@Transactional
public class RawRecordSaveService {

    private final RawRecordRepository rawRecordRepository;

    public RawRecordSaveService(
            RawRecordRepository rawRecordRepository) {

        this.rawRecordRepository = rawRecordRepository;
    }

    public RawRecord save(
            Long batchId,
            Long documentId,
            Long responseId,
            String recordType,
            Integer sourceRowNumber,
            String rawData,
            String rawText,
            String recordHash) {

        if (documentId == null && responseId == null) {
            throw new IllegalArgumentException(
                    "documentId 또는 responseId 중 하나는 반드시 필요합니다."
            );
        }

        if (rawData == null || rawData.isBlank()) {
            throw new IllegalArgumentException(
                    "rawData는 비어 있을 수 없습니다."
            );
        }

        RawRecord record = new RawRecord();

        record.setBatchId(batchId);
        record.setDocumentId(documentId);
        record.setResponseId(responseId);
        record.setRecordType(recordType);
        record.setSourceRowNumber(sourceRowNumber);
        record.setRawData(rawData);
        record.setRawText(rawText);

        // rawData를 기준으로 SHA-256 hash 생성
        record.setRecordHash(generateHash(rawData));

        record.setCreatedAt(LocalDateTime.now());

        return rawRecordRepository.save(record);
    }

    private String generateHash(String rawData) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            rawData.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                hexString.append(
                        String.format("%02x", b)
                );
            }

            return hexString.toString();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "rawData SHA-256 hash 생성 실패",
                    e
            );
        }
    }
}