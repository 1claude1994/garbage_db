package com.garbage.repository.normalization;

import com.garbage.domain.normalization.MappingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MappingRuleRepository
        extends JpaRepository<MappingRule, Long> {

    Optional<MappingRule>
    findFirstByMunicipalityIdAndMappingTypeAndSourceValueAndIsActiveTrueOrderByPriorityDesc(
            Long municipalityId,
            String mappingType,
            String sourceValue
    );
}