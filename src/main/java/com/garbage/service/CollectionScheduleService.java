package com.garbage.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.domain.master.Area;
import com.garbage.domain.service.CollectionSchedule;
import com.garbage.dto.CollectionScheduleDto;
import com.garbage.parser.ScheduleDtoConverter;
import com.garbage.parser.ShibuyaScheduleParser;
import com.garbage.parser.dto.ScheduleSourceDto;
import com.garbage.repository.master.AreaRepository;
import com.garbage.repository.service.CollectionScheduleRepository;

@Service
@Transactional
public class CollectionScheduleService {

    private final CollectionScheduleRepository scheduleRepository;
    private final AreaRepository areaRepository;

    public CollectionScheduleService(
            CollectionScheduleRepository scheduleRepository,
            AreaRepository areaRepository) {

        this.scheduleRepository = scheduleRepository;
        this.areaRepository = areaRepository;
    }

    public List<CollectionSchedule> importSchedules(
            String url,
            Long municipalityId,
            Long burnableCategoryId,
            Long nonBurnableCategoryId,
            Long resourceCategoryId,
            Long sourceId) throws Exception {

        /*
         * 1. 공식 HTML 파싱
         */
        List<ScheduleSourceDto> sources =
                ShibuyaScheduleParser.parse(url);

        System.out.println(
                "파싱된 지역 수 = " + sources.size()
        );

        List<CollectionSchedule> result =
                new ArrayList<>();

        /*
         * 2. 지역별 실제 area_id 조회
         */
        for (ScheduleSourceDto source : sources) {

            String townName =
                    source.getTownName();

            String districtName =
                    source.getDistrictName();

            String areaName;

            if (districtName == null
                    || districtName.isBlank()
                    || districtName.equals("―")) {

                areaName = townName;

            } else {

                areaName =
                        townName + " " + districtName;
            }

            Area area =
                    findArea(
                            municipalityId,
                            areaName
                    );

            Long areaId =
                    area.getAreaId();

            /*
             * 3. 해당 지역의 스케줄 생성
             */
            List<CollectionScheduleDto> dtos =
                    ScheduleDtoConverter.convert(
                            source,
                            municipalityId,
                            areaId,
                            burnableCategoryId,
                            nonBurnableCategoryId,
                            resourceCategoryId,
                            sourceId
                    );

            /*
             * 4. DTO → Entity
             */
            for (CollectionScheduleDto dto : dtos) {

                CollectionSchedule entity =
                        new CollectionSchedule();

                entity.setMunicipalityId(
                        dto.getMunicipalityId()
                );

                entity.setAreaId(
                        dto.getAreaId()
                );

                entity.setCategoryId(
                        dto.getCategoryId()
                );

                entity.setDayOfWeek(
                        dto.getDayOfWeek().shortValue()
                );

                entity.setWeekPattern(
                        dto.getWeekPattern()
                );

                entity.setCollectionTimeLimit(
                        dto.getCollectionTimeLimit()
                );

                entity.setEffectiveFrom(
                        dto.getEffectiveFrom()
                );

                entity.setEffectiveTo(
                        dto.getEffectiveTo()
                );

                entity.setNotes(
                        dto.getNotes()
                );

                entity.setSourceId(
                        dto.getSourceId()
                );

                LocalDateTime now =
                        LocalDateTime.now();

                entity.setCreatedAt(now);
                entity.setUpdatedAt(now);

                result.add(entity);
            }
        }

        /*
         * 5. 기존 일정 삭제
         *
         * 공식 사이트에서 최신 데이터를 다시 가져온 경우
         * 기존 일정과 중복되지 않도록 해당 지자체의
         * 기존 수거일정을 먼저 삭제한다.
         */
        scheduleRepository.deleteByMunicipalityId(
                municipalityId
        );

        System.out.println(
                "기존 수거일정 삭제 완료"
                + " | municipalityId = "
                + municipalityId
        );

        /*
         * 6. 최신 일정 저장
         */
        List<CollectionSchedule> saved =
                scheduleRepository.saveAll(result);

        System.out.println(
                "저장된 스케줄 수 = "
                + saved.size()
        );

        return saved;
    }

    private Area findArea(
            Long municipalityId,
            String areaName) {

        return areaRepository
                .findByMunicipalityIdAndAreaName(
                        municipalityId,
                        areaName
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "수거지역을 찾을 수 없습니다. "
                                + "municipalityId="
                                + municipalityId
                                + ", areaName="
                                + areaName
                        )
                );
    }
}