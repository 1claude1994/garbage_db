package com.garbage.dto;

/**
 * 쓰레기 배출 정보 조회용 DTO
 *
 * 여러 Oracle 테이블을 JOIN한 조회 결과를 하나의 객체로 전달하기 위한 DTO이다.
 *
 * 주요 JOIN 관계
 * WASTE_ITEM
 *      ↓ item_id
 * DISPOSAL_RULE
 *      ↓ category_id
 * WASTE_CATEGORY
 *
 * DISPOSAL_RULE
 *      ↓ municipality_id
 * MUNICIPALITY
 *
 * 이 클래스는 특정 테이블과 1:1로 대응하지 않는다.
 * DAO에서 JOIN한 결과를 Service / Controller / View로 전달할 때 사용한다.
 */
public class WasteDisposalDto {

    // =========================
    // WASTE_ITEM 정보
    // =========================

    /**
     * WASTE_ITEM.ITEM_ID
     *
     * 쓰레기 품목의 PK
     *
     * 예: 101
     */
    private Long itemId;

    /**
     * WASTE_ITEM.ITEM_NAME
     *
     * 쓰레기 품목명
     *
     * 예: 페ットボトル
     */
    private String itemName;

    /**
     * WASTE_ITEM.DESCRIPTION
     *
     * 쓰레기 품목에 대한 추가 설명
     *
     * NULL 가능
     */
    private String itemDescription;


    // =========================
    // WASTE_CATEGORY 정보
    // =========================

    /**
     * WASTE_CATEGORY.CATEGORY_ID
     *
     * 쓰레기 분류의 PK
     *
     * 예: 1
     */
    private Long categoryId;

    /**
     * WASTE_CATEGORY.CATEGORY_CODE
     *
     * 시스템에서 사용하는 분류 코드
     *
     * 예:
     * BURNABLE
     * NON_BURNABLE
     * RESOURCE
     * OVERSIZED
     * OTHER
     */
    private String categoryCode;

    /**
     * WASTE_CATEGORY.CATEGORY_NAME
     *
     * 사용자에게 표시할 쓰레기 분류명
     *
     * 예: 資源
     */
    private String categoryName;


    // =========================
    // DISPOSAL_RULE 정보
    // =========================

    /**
     * DISPOSAL_RULE.RULE_ID
     *
     * 배출 규칙의 PK
     *
     * 예: 1001
     */
    private Long ruleId;

    /**
     * DISPOSAL_RULE.DISPOSAL_METHOD
     *
     * 원본 데이터의 분류/배출 방법
     *
     * 예:
     * 可燃ごみ
     * 不燃ごみ
     * 資源
     * 拠点回収または不燃ごみ
     */
    private String disposalMethod;

    /**
     * DISPOSAL_RULE.DISPOSAL_LOCATION
     *
     * 쓰레기를 배출해야 하는 장소
     *
     * NULL 가능
     */
    private String disposalLocation;

    /**
     * DISPOSAL_RULE.INSTRUCTION
     *
     * 실제 배출 방법 및 처리 방법
     *
     * NULL 가능
     */
    private String instruction;

    /**
     * DISPOSAL_RULE.CAUTION
     *
     * 배출 시 주의사항
     *
     * NULL 가능
     */
    private String caution;

    /**
     * DISPOSAL_RULE.IS_COLLECTABLE
     *
     * 해당 품목을 지자체에서 수거하는지 여부
     *
     * true  = 수거 가능
     * false = 수거하지 않음
     */
    private Boolean isCollectable;


    // =========================
    // MUNICIPALITY 정보
    // =========================

    /**
     * MUNICIPALITY.MUNICIPALITY_ID
     *
     * 지자체의 PK
     *
     * 예: 1
     */
    private Long municipalityId;

    /**
     * MUNICIPALITY.MUNICIPALITY_NAME
     *
     * 지자체명
     *
     * 예: 渋谷区
     */
    private String municipalityName;


    // =========================
    // Getter / Setter
    // =========================

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getDisposalMethod() {
        return disposalMethod;
    }

    public void setDisposalMethod(String disposalMethod) {
        this.disposalMethod = disposalMethod;
    }

    public String getDisposalLocation() {
        return disposalLocation;
    }

    public void setDisposalLocation(String disposalLocation) {
        this.disposalLocation = disposalLocation;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public Boolean getIsCollectable() {
        return isCollectable;
    }

    public void setIsCollectable(Boolean isCollectable) {
        this.isCollectable = isCollectable;
    }

    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }
}
