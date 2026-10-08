package com.garbage.master;

import com.garbage.domain.master.Area;
import com.garbage.repository.master.AreaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public List<Area> findByMunicipalityId(
            Long municipalityId) {

        return areaRepository
                .findByMunicipalityId(municipalityId);
    }

    @Transactional(readOnly = true)
    public Area findByMunicipalityIdAndAreaName(
            Long municipalityId,
            String areaName) {

        return areaRepository
                .findByMunicipalityIdAndAreaName(
                        municipalityId,
                        areaName
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "지역을 찾을 수 없습니다. municipalityId="
                        + municipalityId
                        + ", areaName="
                        + areaName
                ));
    }

    public Area findOrCreate(
            Long municipalityId,
            String areaName,
            String areaCode,
            String areaType,
            String postalCode) {

        return areaRepository
                .findByMunicipalityIdAndAreaName(
                        municipalityId,
                        areaName
                )
                .orElseGet(() -> {

                    Area area = new Area();

                    area.setMunicipalityId(municipalityId);
                    area.setAreaName(areaName);
                    area.setAreaCode(areaCode);
                    area.setAreaType(areaType);
                    area.setPostalCode(postalCode);

                    LocalDateTime now =
                            LocalDateTime.now();

                    area.setCreatedAt(now);
                    area.setUpdatedAt(now);

                    return areaRepository.save(area);
                });
    }
}