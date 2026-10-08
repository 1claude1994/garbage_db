package com.garbage.repository.normalization;

import com.garbage.domain.normalization.FieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldMappingRepository
        extends JpaRepository<FieldMapping, Long> {

    List<FieldMapping> findBySourceIdAndIsActiveTrue(
            Long sourceId
    );
}