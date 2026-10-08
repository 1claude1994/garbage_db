package com.garbage.dto;

/**
 * ============================================================
 * AreaDto
 * ============================================================
 *
 * [역할]
 * Oracle DB의 AREA 테이블에서 조회한
 * 지역 정보를 전달하기 위한 DTO이다.
 *
 * AREA는 MUNICIPALITY(자치단체) 내부의
 * 세부 지역을 관리하기 위한 테이블이다.
 *
 * 예를 들어 현재 프로젝트에서는:
 *
 * 東京都
 *   ↓
 * 渋谷区
 *   ↓
 * 渋谷
 *   ↓
 * 渋谷1丁目
 *
 * 와 같은 지역 계층을 표현할 수 있다.
 *
 * AreaDto에서는 다른 DTO를 직접 포함하지 않고,
 * municipalityId / parentAreaId를 이용하여
 * 다른 지역과의 관계만 표현한다.
 *
 * ============================================================
 */
public class AreaDto {

    /**
     * AREA.AREA_ID
     *
     * 지역의 고유 ID이다.
     *
     * Oracle DB에서 AREA 테이블의 PRIMARY KEY이다.
     *
     * 다른 테이블에서 특정 지역을 참조할 때 사용한다.
     *
     * 예:
     * 1 → 渋谷1丁目
     */
    private Long areaId;


    /**
     * AREA.MUNICIPALITY_ID
     *
     * 해당 지역이 어느 자치단체에 속해 있는지를 나타내는 ID이다.
     *
     * MUNICIPALITY.MUNICIPALITY_ID를 참조한다.
     *
     * 예:
     * 1 → 渋谷区
     *
     * 즉,
     *
     * 渋谷1丁目
     *      ↓
     * municipalityId = 1
     *      ↓
     * 渋谷区
     *
     * 와 같은 관계를 표현한다.
     */
    private Long municipalityId;


    /**
     * AREA.PARENT_AREA_ID
     *
     * 현재 지역의 상위 지역 ID이다.
     *
     * AREA 테이블이 자기 자신을 참조하는
     * Self Reference 구조를 가지고 있기 때문에 존재한다.
     *
     * 예:
     *
     * 渋谷
     *   ↓
     * 渋谷1丁目
     *
     * 渋谷1丁目의 parentAreaId가
     * 渋谷의 areaId를 가리키는 방식이다.
     *
     * 최상위 지역인 경우에는 상위 지역이 없으므로
     * NULL이 될 수 있다.
     */
    private Long parentAreaId;


    /**
     * AREA.AREA_CODE
     *
     * 지역을 식별하기 위한 업무용 코드이다.
     *
     * 지역 데이터를 외부 데이터와 연결하거나
     * 관리할 때 사용할 수 있다.
     *
     * 예:
     * SHIBUYA-1
     *
     * 현재 프로젝트에서는 실제 행정 데이터의
     * 지역 코드가 확보되면 그 값을 저장할 수 있다.
     */
    private String areaCode;


    /**
     * AREA.AREA_NAME
     *
     * 지역의 이름이다.
     *
     * 사용자에게 지역명을 표시할 때 사용하는 값이다.
     *
     * 예:
     * 渋谷
     * 渋谷1丁目
     * 恵比寿
     *
     * AREA 테이블에서는 NOT NULL 컬럼이다.
     */
    private String areaName;


    /**
     * AREA.AREA_TYPE
     *
     * 해당 지역이 어떤 종류의 지역 단위인지를 나타낸다.
     *
     * 예:
     * CHO
     *
     * 지역 데이터를 여러 단계로 관리할 경우
     * 지역의 단위를 구분하는 데 사용할 수 있다.
     */
    private String areaType;


    /**
     * AREA.POSTAL_CODE
     *
     * 해당 지역의 우편번호이다.
     *
     * 주소 검색이나 지역 식별에 활용할 수 있다.
     *
     * 우편번호가 없는 경우 NULL일 수 있다.
     */
    private String postalCode;


    /**
     * AREA.CREATED_AT
     *
     * 해당 지역 데이터가 Oracle DB에
     * 처음 생성된 시간이다.
     */
    private java.time.LocalDateTime createdAt;


    /**
     * AREA.UPDATED_AT
     *
     * 해당 지역 데이터가 마지막으로 수정된 시간이다.
     */
    private java.time.LocalDateTime updatedAt;


    // ============================================================
    // Getter / Setter
    // ============================================================

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }


    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }


    public Long getParentAreaId() {
        return parentAreaId;
    }

    public void setParentAreaId(Long parentAreaId) {
        this.parentAreaId = parentAreaId;
    }


    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }


    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }


    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }


    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}