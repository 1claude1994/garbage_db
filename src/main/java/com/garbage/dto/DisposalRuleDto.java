package com.garbage.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================
 * DisposalRuleDto
 * ============================================================
 *
 * [역할]
 * Oracle DB의 DISPOSAL_RULE 테이블에서 조회한
 * 쓰레기 배출 규칙 정보를 전달하기 위한 DTO이다.
 *
 * DISPOSAL_RULE은 이 프로젝트에서 실제로
 * "이 쓰레기를 어떻게 버려야 하는가?"를 관리하는 핵심 테이블이다.
 *
 * 주요 관계:
 *
 * MUNICIPALITY
 *      │
 *      └── DISPOSAL_RULE.MUNICIPALITY_ID
 *
 * AREA
 *      │
 *      └── DISPOSAL_RULE.AREA_ID
 *
 * WASTE_ITEM
 *      │
 *      └── DISPOSAL_RULE.ITEM_ID
 *
 * WASTE_CATEGORY
 *      │
 *      └── DISPOSAL_RULE.CATEGORY_ID
 *
 * 따라서 이 DTO에서는 다른 DTO 객체를 직접 넣지 않고
 * 각 테이블의 ID를 이용하여 관계를 표현한다.
 *
 * 예:
 *
 * "エアコン"
 *     ↓
 * ITEM_ID
 *     ↓
 * DISPOSAL_RULE
 *     ↓
 * CATEGORY_ID = OTHER
 *     ↓
 * DISPOSAL_METHOD = 区では収集しません。
 *     ↓
 * IS_COLLECTABLE = false
 *
 * ============================================================
 */
public class DisposalRuleDto {

    /**
     * DISPOSAL_RULE.RULE_ID
     *
     * 배출 규칙의 고유 ID이다.
     *
     * Oracle DB에서 PRIMARY KEY로 사용된다.
     *
     * 하나의 배출 규칙을 식별하기 위한 값이다.
     */
    private Long ruleId;


    /**
     * DISPOSAL_RULE.MUNICIPALITY_ID
     *
     * 해당 배출 규칙이 어느 자치단체에 적용되는지를
     * 나타내는 ID이다.
     *
     * MUNICIPALITY.MUNICIPALITY_ID를 참조한다.
     *
     * 현재 프로젝트에서는:
     *
     * 1 → 渋谷区
     *
     * 이다.
     *
     * 같은 쓰레기라도 자치단체에 따라
     * 배출 방법이 달라질 수 있기 때문에
     * DISPOSAL_RULE에서 자치단체를 구분한다.
     */
    private Long municipalityId;


    /**
     * DISPOSAL_RULE.AREA_ID
     *
     * 해당 배출 규칙이 적용되는 세부 지역의 ID이다.
     *
     * AREA.AREA_ID를 참조한다.
     *
     * 예:
     *
     * 渋谷区
     *   └── 渋谷1丁目
     *
     * 특정 지역에만 적용되는 배출 규칙이라면
     * 해당 지역의 AREA_ID를 저장한다.
     *
     * 현재 수집한 221개 품목 데이터에는
     * 세부 지역 정보가 없기 때문에 NULL일 수 있다.
     */
    private Long areaId;


    /**
     * DISPOSAL_RULE.ITEM_ID
     *
     * 어떤 쓰레기 품목에 대한 배출 규칙인지를
     * 나타내는 ID이다.
     *
     * WASTE_ITEM.ITEM_ID를 참조한다.
     *
     * 예:
     *
     * ITEM_ID
     *    ↓
     * エアコン
     *
     * 즉 이 값을 통해
     * "어떤 품목의 배출 규칙인가?"를 알 수 있다.
     */
    private Long itemId;


    /**
     * DISPOSAL_RULE.CATEGORY_ID
     *
     * 해당 쓰레기의 기본 분류를 나타내는 ID이다.
     *
     * WASTE_CATEGORY.CATEGORY_ID를 참조한다.
     *
     * 현재 프로젝트의 기본 분류:
     *
     * BURNABLE
     * NON_BURNABLE
     * RESOURCE
     * OVERSIZED
     * OTHER
     *
     * 주의:
     *
     * 원본 사이트의 "分類" 값을 그대로 저장하는 것이 아니다.
     *
     * 예:
     *
     * "拠点回収または不燃ごみ"
     *
     * 같은 원본 값은
     * category = NON_BURNABLE
     * disposalMethod = 拠点回収または不燃ごみ
     *
     * 와 같이 분리해서 관리한다.
     */
    private Long categoryId;


    /**
     * DISPOSAL_RULE.DISPOSAL_METHOD
     *
     * 실제 쓰레기를 어떤 방법으로 처리해야 하는지를
     * 나타내는 값이다.
     *
     * 원본 사이트의 "分類" 정보 중
     * 단순한 기본 분류 이상의 처리 방법을 표현하기 위해 사용한다.
     *
     * 예:
     *
     * 可燃ごみ
     * 不燃ごみ
     * 粗大ごみ
     * 資源
     * 拠点回収
     * 拠点回収または不燃ごみ
     * 拠点回収または可燃ごみ
     * 区では収集しません。
     * 個別
     *
     * 즉 CATEGORY_ID가 "기본적인 분류"라면
     * DISPOSAL_METHOD는 "실제 처리 방법/조건"에 가깝다.
     */
    private String disposalMethod;


    /**
     * DISPOSAL_RULE.DISPOSAL_LOCATION
     *
     * 쓰레기를 실제로 배출하거나
     * 가져가야 하는 장소를 나타낸다.
     *
     * 예:
     *
     * 지정된 수거 장소
     * 특정 회수 거점
     *
     * 현재 수집한 데이터에는 별도의 장소 정보가
     * 없는 경우 NULL일 수 있다.
     */
    private String disposalLocation;


    /**
     * DISPOSAL_RULE.INSTRUCTION
     *
     * 쓰레기를 버릴 때 필요한
     * 구체적인 배출 방법 및 주의사항이다.
     *
     * PostgreSQL에서 공식 웹사이트의
     * "出し方・注意点" 값을 수집하여
     * 최종적으로 이 컬럼에 저장했다.
     *
     * 예:
     *
     * 30センチメートル角以上のものは粗大ごみへ
     *
     * 사용자가 실제 배출 방법을 확인할 때
     * 중요한 데이터이다.
     */
    private String instruction;


    /**
     * DISPOSAL_RULE.CAUTION
     *
     * 배출 시 별도로 표시해야 하는
     * 주의사항을 저장하는 필드이다.
     *
     * INSTRUCTION이 전체적인 배출 방법이라면
     * CAUTION은 별도의 주의사항을 분리해서
     * 관리하기 위한 필드이다.
     *
     * 현재 공식 웹페이지 데이터에서는
     * 별도의 CAUTION 필드가 없기 때문에
     * NULL일 수 있다.
     */
    private String caution;


    /**
     * DISPOSAL_RULE.IS_COLLECTABLE
     *
     * 해당 쓰레기를 渋谷区에서 수거하는지 여부를 나타낸다.
     *
     * true
     * → 자치단체에서 수거 가능
     *
     * false
     * → 자치단체에서 수거하지 않음
     *
     * 현재 데이터 예:
     *
     * 일반 可燃ごみ
     * → true
     *
     * エアコン
     * → false
     *
     * 区では収集しません。
     * 같은 원본 분류를 가진 품목은
     * false로 정규화된다.
     */
    private Boolean isCollectable;


    /**
     * DISPOSAL_RULE.EFFECTIVE_FROM
     *
     * 해당 배출 규칙이 적용되기 시작하는 날짜이다.
     *
     * 행정 규칙이 변경될 가능성을 고려하여
     * 시작일을 관리할 수 있도록 만들어 둔 필드이다.
     *
     * NULL이면 별도의 시작일이 지정되지 않은 것으로 볼 수 있다.
     */
    private LocalDate effectiveFrom;


    /**
     * DISPOSAL_RULE.EFFECTIVE_TO
     *
     * 해당 배출 규칙의 적용이 종료되는 날짜이다.
     *
     * 향후 새로운 배출 규칙으로 변경될 경우
     * 이전 규칙의 종료일을 기록하는 데 사용할 수 있다.
     *
     * NULL이면 현재 종료일이 지정되지 않은 상태이다.
     */
    private LocalDate effectiveTo;


    /**
     * DISPOSAL_RULE.SOURCE_ID
     *
     * 해당 배출 규칙의 원본 데이터가
     * 어디에서 수집되었는지를 나타내는 ID이다.
     *
     * 현재 프로젝트에서는 RAW.SOURCE와 연결된다.
     *
     * 예:
     *
     * 1 → 渋谷区公式サイト
     *
     * 데이터의 출처를 추적할 수 있도록 하기 위한 필드이다.
     */
    private Long sourceId;


    /**
     * DISPOSAL_RULE.CREATED_AT
     *
     * 해당 배출 규칙 데이터가
     * Oracle DB에 처음 생성된 시간이다.
     */
    private LocalDateTime createdAt;


    /**
     * DISPOSAL_RULE.UPDATED_AT
     *
     * 해당 배출 규칙 데이터가
     * 마지막으로 수정된 시간이다.
     */
    private LocalDateTime updatedAt;


    // ============================================================
    // Getter / Setter
    // ============================================================

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }


    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }


    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }


    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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