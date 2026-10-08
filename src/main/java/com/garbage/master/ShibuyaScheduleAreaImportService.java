package com.garbage.master;

import com.garbage.domain.master.Area;
import com.garbage.dto.AreaDto;
import com.garbage.parser.ScheduleAreaListConverter;
import com.garbage.parser.ShibuyaScheduleParser;
import com.garbage.parser.dto.ScheduleSourceDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShibuyaScheduleAreaImportService {

    private final AreaService areaService;

    public ShibuyaScheduleAreaImportService(
            AreaService areaService) {

        this.areaService = areaService;
    }

    @Transactional
    public List<Area> importAreas(
            String url,
            Long municipalityId) throws Exception {

        // 1. 공식 HTML 파싱
        List<ScheduleSourceDto> sources =
                ShibuyaScheduleParser.parse(url);

        System.out.println(
                "파싱된 지역 수 = " + sources.size()
        );

        // 2. AreaDto로 변환 + 중복 제거
        List<AreaDto> areaDtos =
                ScheduleAreaListConverter.convert(
                        sources,
                        municipalityId
                );

        System.out.println(
                "변환된 지역 수 = " + areaDtos.size()
        );

        // 3. PostgreSQL master.area에 저장
        List<Area> savedAreas = areaDtos.stream()
                .map(dto ->
                        areaService.findOrCreate(
                                dto.getMunicipalityId(),
                                dto.getAreaName(),
                                dto.getAreaCode(),
                                dto.getAreaType(),
                                dto.getPostalCode()
                        )
                )
                .toList();

        System.out.println(
                "저장/조회된 지역 수 = "
                + savedAreas.size()
        );

        // 4. 실제 생성된 area_id 확인
        for (Area area : savedAreas) {

            System.out.println(
                    "area_id = "
                    + area.getAreaId()
                    + " | area_name = "
                    + area.getAreaName()
            );
        }

        return savedAreas;
    }
}