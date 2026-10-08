package com.garbage.service;

import com.garbage.domain.service.DisposalRule;
import com.garbage.repository.service.DisposalRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DisposalRuleService {

    private final DisposalRuleRepository disposalRuleRepository;

    public DisposalRuleService(
            DisposalRuleRepository disposalRuleRepository) {

        this.disposalRuleRepository = disposalRuleRepository;
    }

    public List<DisposalRule> findByMunicipalityIdAndItemId(
            Long municipalityId,
            Long itemId) {

        return disposalRuleRepository
                .findByMunicipalityIdAndItemId(
                        municipalityId,
                        itemId
                );
    }
}