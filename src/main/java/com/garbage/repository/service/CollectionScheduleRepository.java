package com.garbage.repository.service;

import com.garbage.domain.service.CollectionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionScheduleRepository
        extends JpaRepository<CollectionSchedule, Long> {

    List<CollectionSchedule> findByMunicipalityId(
            Long municipalityId
    );

    List<CollectionSchedule> findByAreaId(
            Long areaId
    );

    List<CollectionSchedule> findByMunicipalityIdAndAreaId(
            Long municipalityId,
            Long areaId
    );

    List<CollectionSchedule> findByMunicipalityIdAndCategoryId(
            Long municipalityId,
            Long categoryId
    );

    /*
     * 해당 지자체의 기존 수거일정을
     * 최신 공식 데이터로 교체하기 위해 사용
     */
    void deleteByMunicipalityId(
            Long municipalityId
    );
}