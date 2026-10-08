package com.garbage.repository.master;

import com.garbage.domain.master.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository
        extends JpaRepository<Area, Long> {

    List<Area> findByMunicipalityId(Long municipalityId);

    Optional<Area> findByMunicipalityIdAndAreaName(
            Long municipalityId,
            String areaName
    );
}