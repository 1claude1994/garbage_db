package com.garbage.normalization;

import com.garbage.domain.normalization.FieldMapping;
import com.garbage.repository.normalization.FieldMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FieldMappingService {

    private final FieldMappingRepository fieldMappingRepository;

    public FieldMappingService(FieldMappingRepository fieldMappingRepository) {
        this.fieldMappingRepository = fieldMappingRepository;
    }

    public List<FieldMapping> findBySourceId(Long sourceId) {

        return fieldMappingRepository
                .findBySourceIdAndIsActiveTrue(sourceId);
    }
}