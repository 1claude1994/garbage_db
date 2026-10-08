package com.garbage.parser;

import com.garbage.dto.AreaDto;
import com.garbage.parser.dto.ScheduleSourceDto;

/**
 * ScheduleSourceDto를 AreaDto로 변환하는 클래스
 *
 * ScheduleSourceDto:
 *   渋谷区 공식 수거요일 HTML에서 가져온 원본 지역 정보
 *
 * AreaDto:
 *   Oracle AREA 테이블에 넣기 위한 DTO
 *
 * 예:
 *
 * townName    = 上原
 * districtName = 1丁目
 *
 * ↓
 *
 * areaName = 上原 1丁目
 *
 * 주의:
 * - 공식 사이트의 지역 표현을 임의로 숫자 분해하지 않는다.
 * - 1丁目, 2・3丁目, 1～3丁目 등의 표현을 그대로 보존한다.
 * - 「―」도 원본 데이터이므로 그대로 유지한다.
 */
public class ScheduleAreaConverter {

    /**
     * ScheduleSourceDto 한 건을 AreaDto 한 건으로 변환한다.
     *
     * @param source HTML에서 파싱한 지역 정보
     * @param municipalityId 해당 지역의 자치단체 ID
     * @return 변환된 AreaDto
     */
    public static AreaDto convert(
            ScheduleSourceDto source,
            Long municipalityId) {

        AreaDto dto = new AreaDto();

        // 渋谷区의 municipality_id
        dto.setMunicipalityId(municipalityId);

        /*
         * 공식 사이트의
         *
         * 町名 + 丁目
         *
         * 을 하나의 지역명으로 보존한다.
         */
        String townName = source.getTownName();
        String districtName = source.getDistrictName();

        String areaName;

        if (districtName == null
                || districtName.isBlank()
                || districtName.equals("―")) {

            areaName = townName;

        } else {

            areaName = townName + " " + districtName;
        }

        dto.setAreaName(areaName);

        /*
         * 현재 공식 HTML에서는 별도의 area_code를 제공하지 않는다.
         *
         * 따라서 여기서는 null로 둔다.
         *
         * 나중에 Oracle INSERT 단계에서
         * 별도의 규칙으로 코드가 필요하면 생성한다.
         */
        dto.setAreaCode(null);

        /*
         * 현재 공식 수거요일 데이터만으로는
         * 계층 구조를 확정할 수 없으므로 null.
         */
        dto.setParentAreaId(null);

        /*
         * 현재는 수거지역이라는 의미로 저장한다.
         */
        dto.setAreaType("COLLECTION_AREA");

        /*
         * 공식 HTML에서 우편번호는 제공하지 않는다.
         */
        dto.setPostalCode(null);

        return dto;
    }
    
    public static void main(String[] args) {

        ScheduleSourceDto source = new ScheduleSourceDto();

        source.setTownName("上原");
        source.setDistrictName("1丁目");

        AreaDto dto = ScheduleAreaConverter.convert(source, 1L);

        System.out.println("municipalityId = "
                + dto.getMunicipalityId());

        System.out.println("areaName = "
                + dto.getAreaName());

        System.out.println("areaCode = "
                + dto.getAreaCode());

        System.out.println("parentAreaId = "
                + dto.getParentAreaId());

        System.out.println("areaType = "
                + dto.getAreaType());

        System.out.println("postalCode = "
                + dto.getPostalCode());
    }
}