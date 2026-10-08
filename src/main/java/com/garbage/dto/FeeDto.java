package com.garbage.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================
 * FeeDto
 * ============================================================
 *
 * [역할]
 * Oracle DB의 FEE 테이블에서 조회한
 * 쓰레기 처리 비용 정보를 전달하기 위한 DTO이다.
 *
 * FEE 테이블은 특정 자치단체에서 쓰레기 처리에
 * 비용이 발생하는 경우 그 비용 정보를 관리한다.
 *
 * 주요 관계:
 *
 * MUNICIPALITY
 *      │
 *      └── FEE.MUNICIPALITY_ID
 *
 * WASTE_ITEM
 *      │
 *      └── FEE.ITEM_ID
 *
 * 이 DTO에서는 MunicipalityDto나 WasteItemDto를
 * 직접 포함하지 않는다.
 *
 * 각각의 ID를 이용하여 다른 테이블과의 관계만 표현한다.
 *
 * ============================================================
 */
public class FeeDto {

    /**
     * FEE.FEE_ID
     *
     * 비용 정보의 고유 ID이다.
     *
     * Oracle DB에서 PRIMARY KEY로 사용된다.
     *
     * 하나의 비용 정보를 식별하기 위한 값이다.
     */
    private Long feeId;


    /**
     * FEE.MUNICIPALITY_ID
     *
     * 해당 비용 정보가 어느 자치단체에 적용되는지를
     * 나타내는 ID이다.
     *
     * MUNICIPALITY.MUNICIPALITY_ID를 참조한다.
     *
     * 현재 프로젝트에서는:
     *
     * 1 → 渋谷区
     *
     * 와 같이 사용한다.
     *
     * 같은 쓰레기라도 자치단체에 따라
     * 처리 비용이 다를 수 있기 때문에
     * 자치단체를 구분하여 관리한다.
     */
    private Long municipalityId;


    /**
     * FEE.ITEM_ID
     *
     * 어떤 쓰레기 품목에 대한 비용인지를
     * 나타내는 ID이다.
     *
     * WASTE_ITEM.ITEM_ID를 참조한다.
     *
     * 예:
     *
     * ITEM_ID
     *    ↓
     * 粗大ごみ 품목
     *    ↓
     * FEE
     *    ↓
     * 처리 비용
     *
     * 모든 비용이 특정 품목에만 연결되는 것은 아니므로
     * Oracle DB에서 NULL이 허용된다.
     */
    private Long itemId;


    /**
     * FEE.FEE_TYPE
     *
     * 비용의 종류를 나타낸다.
     *
     * 비용이 어떤 기준으로 부과되는지를
     * 구분하기 위한 필드이다.
     *
     * 예:
     *
     * DISPOSAL
     * COLLECTION
     * OVERSIZED
     *
     * 실제 사용하는 값은 수집한 공식 데이터의
     * 비용 정책에 맞춰 결정한다.
     */
    private String feeType;


    /**
     * FEE.AMOUNT
     *
     * 실제 비용 금액이다.
     *
     * 현재 프로젝트에서는 일본 엔화를 기준으로
     * 금액을 관리한다.
     *
     * 예:
     *
     * 400
     * 800
     * 1200
     *
     * Oracle DB에서는 NUMBER(10)으로 관리하고
     * Java에서는 Integer로 받는다.
     *
     * 금액은 0 이상이어야 한다.
     */
    private Integer amount;


    /**
     * FEE.CURRENCY
     *
     * 비용에 사용되는 통화 코드이다.
     *
     * ISO 4217 형식의 통화 코드를 사용한다.
     *
     * 현재 프로젝트의 대상 지역이 일본이므로
     * 기본값은 "JPY"이다.
     *
     * 예:
     *
     * JPY
     */
    private String currency;


    /**
     * FEE.UNIT
     *
     * 비용이 어떤 단위로 부과되는지를 나타낸다.
     *
     * 예:
     *
     * 1건
     * 1개
     * 1회
     * 1kg
     *
     * 비용 금액만으로는 정확한 부과 기준을
     * 알 수 없는 경우가 있기 때문에 별도로 관리한다.
     */
    private String unit;


    /**
     * FEE.CONDITION_DESCRIPTION
     *
     * 해당 비용이 적용되는 조건에 대한 설명이다.
     *
     * 단순히 금액만 저장하는 것이 아니라
     * "어떤 조건에서 이 비용이 적용되는가"를
     * 설명하기 위한 필드이다.
     *
     * 예:
     *
     * 크기에 따라 비용이 달라지는 경우
     * 특정 품목에만 비용이 발생하는 경우
     * 특정 처리 방법을 이용하는 경우
     *
     * 등의 상세 조건을 저장할 수 있다.
     */
    private String conditionDescription;


    /**
     * FEE.EFFECTIVE_FROM
     *
     * 해당 비용 정책이 적용되기 시작하는 날짜이다.
     *
     * 행정 정책이나 수수료가 변경될 수 있기 때문에
     * 비용 정책의 적용 시작일을 관리한다.
     *
     * NULL이면 별도의 시작일이 지정되지 않은 것이다.
     */
    private LocalDate effectiveFrom;


    /**
     * FEE.EFFECTIVE_TO
     *
     * 해당 비용 정책의 적용 종료일이다.
     *
     * 새로운 비용 정책으로 변경되는 경우
     * 기존 비용 정보의 종료일을 기록할 수 있다.
     *
     * NULL이면 종료일이 지정되지 않은 것이다.
     */
    private LocalDate effectiveTo;


    /**
     * FEE.SOURCE_ID
     *
     * 해당 비용 정보의 원본 출처를 나타내는 ID이다.
     *
     * 현재 프로젝트에서는 원본 데이터를 추적하기 위한
     * 용도로 사용한다.
     *
     * 예:
     *
     * 1 → 渋谷区公式サイト
     *
     * 어떤 공식 자료를 기반으로 만들어진 비용 정보인지
     * 추적할 수 있도록 하기 위한 필드이다.
     */
    private Long sourceId;


    /**
     * FEE.CREATED_AT
     *
     * 해당 비용 데이터가 Oracle DB에
     * 처음 생성된 시간이다.
     *
     * 데이터 생성 시점이나 관리 이력을
     * 확인할 때 사용할 수 있다.
     */
    private LocalDateTime createdAt;


    /**
     * FEE.UPDATED_AT
     *
     * 해당 비용 데이터가 Oracle DB에서
     * 마지막으로 수정된 시간이다.
     *
     * 비용 정책 변경이나 데이터 수정 시점을
     * 확인할 때 사용할 수 있다.
     */
    private LocalDateTime updatedAt;


    // ============================================================
    // Getter / Setter
    // ============================================================

    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
    }


    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }


    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }


    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription;
    }


    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }


    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }


    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}