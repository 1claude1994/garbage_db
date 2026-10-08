package com.garbage.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.garbage.dto.AreaDto;
import com.garbage.parser.dto.ScheduleSourceDto;

/**
 * 여러 개의 ScheduleSourceDto를
 * 여러 개의 AreaDto로 변환하는 클래스
 *
 * 주요 역할:
 * 1. HTML에서 가져온 전체 지역 데이터를 변환
 * 2. 같은 지역이 중복되는 경우 제거
 *
 * 중복 판단 기준:
 * municipalityId + areaName
 */
public class ScheduleAreaListConverter {

    /**
     * ScheduleSourceDto 목록을 AreaDto 목록으로 변환한다.
     *
     * @param sources HTML에서 파싱한 전체 지역 목록
     * @param municipalityId 자치단체 ID
     * @return 중복 제거된 AreaDto 목록
     */
    public static List<AreaDto> convert(
            List<ScheduleSourceDto> sources,
            Long municipalityId) {

        List<AreaDto> result = new ArrayList<>();

        // 이미 추가한 지역명을 저장
        Set<String> areaNames = new HashSet<>();

        if (sources == null || sources.isEmpty()) {
            return result;
        }

        for (ScheduleSourceDto source : sources) {

            if (source == null) {
                continue;
            }

            AreaDto areaDto =
                    ScheduleAreaConverter.convert(
                            source,
                            municipalityId
                    );

            String areaName = areaDto.getAreaName();

            /*
             * 같은 municipality 안에서
             * 같은 areaName이 이미 존재하면 추가하지 않는다.
             */
            if (areaNames.contains(areaName)) {
                continue;
            }

            areaNames.add(areaName);
            result.add(areaDto);
        }

        return result;
    }
    
    public static void main(String[] args) {

        List<ScheduleSourceDto> sources = new ArrayList<>();

        ScheduleSourceDto source1 = new ScheduleSourceDto();
        source1.setTownName("上原");
        source1.setDistrictName("1丁目");

        ScheduleSourceDto source2 = new ScheduleSourceDto();
        source2.setTownName("上原");
        source2.setDistrictName("2・3丁目");

        // 중복 테스트
        ScheduleSourceDto source3 = new ScheduleSourceDto();
        source3.setTownName("上原");
        source3.setDistrictName("1丁目");

        sources.add(source1);
        sources.add(source2);
        sources.add(source3);

        List<AreaDto> result =
                ScheduleAreaListConverter.convert(
                        sources,
                        1L
                );

        System.out.println("지역 개수 = " + result.size());

        for (AreaDto dto : result) {
            System.out.println(
                    dto.getMunicipalityId()
                    + " | "
                    + dto.getAreaName()
            );
        }
    }
}