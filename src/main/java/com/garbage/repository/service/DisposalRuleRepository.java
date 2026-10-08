package com.garbage.repository.service;

import com.garbage.domain.service.DisposalRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisposalRuleRepository
        extends JpaRepository<DisposalRule, Long> {

    List<DisposalRule> findByMunicipalityIdAndItemId(
            Long municipalityId,
            Long itemId
    );

    Optional<DisposalRule> findFirstByMunicipalityIdAndAreaIdAndItemId(
            Long municipalityId,
            Long areaId,
            Long itemId
    );

    Optional<DisposalRule> findFirstByMunicipalityIdAndAreaIdAndItemIdAndCategoryId(
            Long municipalityId,
            Long areaId,
            Long itemId,
            Long categoryId
    );
}