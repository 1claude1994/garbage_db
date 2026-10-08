package com.garbage.normalization;

import com.garbage.domain.normalization.MappingRule;
import com.garbage.repository.normalization.MappingRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MappingService {

    private final MappingRuleRepository mappingRuleRepository;

    public MappingService(MappingRuleRepository mappingRuleRepository) {
        this.mappingRuleRepository = mappingRuleRepository;
    }

    public String map(
            Long municipalityId,
            String mappingType,
            String sourceValue) {

        MappingRule rule = mappingRuleRepository
                .findFirstByMunicipalityIdAndMappingTypeAndSourceValueAndIsActiveTrueOrderByPriorityDesc(
                        municipalityId,
                        mappingType,
                        sourceValue
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "매핑 규칙을 찾을 수 없습니다. municipalityId="
                        + municipalityId
                        + ", mappingType="
                        + mappingType
                        + ", sourceValue="
                        + sourceValue
                ));

        return rule.getTargetValue();
    }
}