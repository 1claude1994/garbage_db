package com.garbage.dto;

/**
 * ============================================================
 * MunicipalityDto
 * ============================================================
 *
 * [역할]
 * Oracle DB의 MUNICIPALITY 테이블에서 조회한
 * 자치단체 정보를 전달하기 위한 DTO이다.
 *
 * 현재 프로젝트에서는 일본의 지방자치단체 정보를 관리한다.
 *
 * 예:
 *
 * MUNICIPALITY
 * ┌──────────────────────────────┐
 * │ 1                            │
 * │ JP                           │
 * │ 13                           │
 * │ 13113                        │
 * │ 渋谷区                       │
 * │ 東京都渋谷区                 │
 * └──────────────────────────────┘
 *
 * DAO에서 JDBC ResultSet으로 조회한 데이터를
 * MunicipalityDto 객체에 담아서 Service / Controller로 전달한다.
 *
 * ============================================================
 */
public class MunicipalityDto {

    /**
     * MUNICIPALITY.MUNICIPALITY_ID
     *
     * 자치단체의 고유 ID이다.
     *
     * Oracle DB에서 PRIMARY KEY로 사용된다.
     *
     * 다른 테이블에서 해당 자치단체를 참조할 때 사용한다.
     *
     * 예:
     * 1 → 渋谷区
     */
    private Long municipalityId;


    /**
     * MUNICIPALITY.COUNTRY_CODE
     *
     * 국가 코드이다.
     *
     * 현재 프로젝트의 대상 국가는 일본이므로
     * 일반적으로 "JP"가 저장된다.
     *
     * 예:
     * JP
     */
    private String countryCode;


    /**
     * MUNICIPALITY.PREFECTURE_CODE
     *
     * 도도부현 코드이다.
     *
     * 일본의 행정구역에서 도쿄도는 13번으로 관리된다.
     *
     * 예:
     * 13 → 東京都
     *
     * 지역 정보를 확장할 경우 상위 행정구역을
     * 구분하는 데 사용할 수 있다.
     */
    private String prefectureCode;


    /**
     * MUNICIPALITY.MUNICIPALITY_CODE
     *
     * 자치단체 코드이다.
     *
     * 일본의 지방자치단체를 식별하기 위한 코드이다.
     *
     * 현재 프로젝트의 渋谷区는:
     *
     * 13113
     *
     * 으로 관리한다.
     *
     * COUNTRY_CODE와 함께 UNIQUE 제약조건을 가진다.
     */
    private String municipalityCode;


    /**
     * MUNICIPALITY.MUNICIPALITY_NAME
     *
     * 자치단체의 일반적인 이름이다.
     *
     * 현재 프로젝트에서는:
     *
     * 渋谷区
     *
     * 를 저장한다.
     *
     * 화면에서 지역명을 표시할 때 사용할 수 있다.
     */
    private String municipalityName;


    /**
     * MUNICIPALITY.OFFICIAL_NAME
     *
     * 자치단체의 공식 명칭이다.
     *
     * 현재 프로젝트에서는:
     *
     * 東京都渋谷区
     *
     * 를 저장할 수 있다.
     *
     * MUNICIPALITY_NAME보다 상위 행정구역을 포함한
     * 공식적인 명칭이 필요할 경우 사용한다.
     */
    private String officialName;


    /**
     * MUNICIPALITY.CREATED_AT
     *
     * 해당 자치단체 데이터가 DB에 처음 생성된 시간이다.
     *
     * 현재 DTO에서는 우선 기본적인 조회 데이터에 집중하기 위해
     * 생략할 수도 있지만, Oracle 테이블 구조와 정확하게
     * 대응시키기 위해 포함한다.
     *
     * JDBC에서 TIMESTAMP를 조회할 경우
     * java.sql.Timestamp 또는 java.time.LocalDateTime으로
     * 처리할 수 있다.
     */
    private java.time.LocalDateTime createdAt;


    /**
     * MUNICIPALITY.UPDATED_AT
     *
     * 해당 자치단체 데이터가 마지막으로 수정된 시간이다.
     *
     * 데이터 변경 이력이나 최신 데이터 여부를 확인할 때 사용할 수 있다.
     */
    private java.time.LocalDateTime updatedAt;


    // ============================================================
    // Getter / Setter
    // ============================================================

    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }


    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    public String getPrefectureCode() {
        return prefectureCode;
    }

    public void setPrefectureCode(String prefectureCode) {
        this.prefectureCode = prefectureCode;
    }


    public String getMunicipalityCode() {
        return municipalityCode;
    }

    public void setMunicipalityCode(String municipalityCode) {
        this.municipalityCode = municipalityCode;
    }


    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }


    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
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