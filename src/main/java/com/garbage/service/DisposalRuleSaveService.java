package com.garbage.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.domain.service.DisposalRule;
import com.garbage.repository.service.DisposalRuleRepository;

@Service
@Transactional
public class DisposalRuleSaveService {

    private final DisposalRuleRepository disposalRuleRepository;

    public DisposalRuleSaveService(
            DisposalRuleRepository disposalRuleRepository) {

        this.disposalRuleRepository = disposalRuleRepository;
    }

    public DisposalRule save(
            Long municipalityId,
            Long areaId,
            Long itemId,
            Long categoryId,
            String disposalMethod,
            String disposalLocation,
            String instruction,
            String caution,
            Boolean isCollectable,
            Long sourceId) {

        DisposalRule rule =
                disposalRuleRepository
                        .findFirstByMunicipalityIdAndAreaIdAndItemId(
                                municipalityId,
                                areaId,
                                itemId
                        )
                        .orElseGet(DisposalRule::new);

        rule.setMunicipalityId(municipalityId);
        rule.setAreaId(areaId);
        rule.setItemId(itemId);
        rule.setCategoryId(categoryId);

        rule.setDisposalMethod(disposalMethod);
        rule.setDisposalLocation(disposalLocation);
        rule.setInstruction(instruction);
        rule.setCaution(caution);

        rule.setIsCollectable(
                isCollectable != null ? isCollectable : true
        );

        rule.setSourceId(sourceId);

        LocalDateTime now = LocalDateTime.now();

        if (rule.getCreatedAt() == null) {
            rule.setCreatedAt(now);
        }

        rule.setUpdatedAt(now);

        return disposalRuleRepository.save(rule);
    }
    
    public DisposalRule findCurrentRule(
            Long municipalityId,
            Long areaId,
            Long itemId) {

        return disposalRuleRepository
                .findFirstByMunicipalityIdAndAreaIdAndItemId(
                        municipalityId,
                        areaId,
                        itemId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "현재 DisposalRule을 찾을 수 없습니다."
                                + " municipalityId=" + municipalityId
                                + ", areaId=" + areaId
                                + ", itemId=" + itemId));
    }


    public DisposalRule endRule(
            Long municipalityId,
            Long areaId,
            Long itemId) {

        DisposalRule rule =
                disposalRuleRepository
                        .findFirstByMunicipalityIdAndAreaIdAndItemId(
                                municipalityId,
                                areaId,
                                itemId
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "종료할 DisposalRule을 찾을 수 없습니다."
                                        + " | municipalityId = " + municipalityId
                                        + " | areaId = " + areaId
                                        + " | itemId = " + itemId
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        LocalDate today = LocalDate.now();

        rule.setEffectiveTo(today);
        rule.setUpdatedAt(LocalDateTime.now());

        return disposalRuleRepository.save(rule);
    }
}