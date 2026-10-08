package com.garbage.parser.dto;

/**
 * 渋谷区 공식 수거요일 HTML의 한 행을 담기 위한
 * 파싱 전용 DTO
 *
 * ※ Oracle 테이블과 직접 대응하는 DTO가 아니다.
 * ※ HTML 원본의 지역명과 수거요일 정보를 임시로 보관하는 용도이다.
 */
public class ScheduleSourceDto {

    private String townName;
    private String districtName;

    private String burnableSchedule;
    private String nonBurnableSchedule;
    private String resourceSchedule;

    // 일반 지역: 08:00
    // 일부 번화가 지역: 07:30
    private String collectionTimeLimit;

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getBurnableSchedule() {
        return burnableSchedule;
    }

    public void setBurnableSchedule(String burnableSchedule) {
        this.burnableSchedule = burnableSchedule;
    }

    public String getNonBurnableSchedule() {
        return nonBurnableSchedule;
    }

    public void setNonBurnableSchedule(String nonBurnableSchedule) {
        this.nonBurnableSchedule = nonBurnableSchedule;
    }

    public String getResourceSchedule() {
        return resourceSchedule;
    }

    public void setResourceSchedule(String resourceSchedule) {
        this.resourceSchedule = resourceSchedule;
    }

    public String getCollectionTimeLimit() {
        return collectionTimeLimit;
    }

    public void setCollectionTimeLimit(String collectionTimeLimit) {
        this.collectionTimeLimit = collectionTimeLimit;
    }
}