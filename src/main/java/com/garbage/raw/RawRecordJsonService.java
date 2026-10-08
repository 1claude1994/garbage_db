package com.garbage.raw;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garbage.domain.raw.RawRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RawRecordJsonService {

    private final RawRecordService rawRecordService;
    private final ObjectMapper objectMapper;

    public RawRecordJsonService(
            RawRecordService rawRecordService,
            ObjectMapper objectMapper) {

        this.rawRecordService = rawRecordService;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> findJsonByBatchId(Long batchId) {

        List<RawRecord> records =
                rawRecordService.findByBatchId(batchId);

        return records.stream()
                .map(this::parseJson)
                .toList();
    }

    private Map<String, Object> parseJson(RawRecord record) {

        try {

            return objectMapper.readValue(
                    record.getRawData(),
                    new TypeReference<Map<String, Object>>() {}
            );

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "RawRecord JSON 파싱 실패. recordId="
                    + record.getRecordId(),
                    e
            );
        }
    }
    
    public List<Map<String, String>> findStringJsonByBatchId(Long batchId) {

        List<RawRecord> records =
                rawRecordService.findByBatchId(batchId);

        return records.stream()
                .map(this::parseStringJson)
                .toList();
    }

    private Map<String, String> parseStringJson(RawRecord record) {

        try {

            return objectMapper.readValue(
                    record.getRawData(),
                    new TypeReference<Map<String, String>>() {}
            );

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "RawRecord JSON 파싱 실패. recordId="
                    + record.getRecordId(),
                    e
            );
        }
    }
}