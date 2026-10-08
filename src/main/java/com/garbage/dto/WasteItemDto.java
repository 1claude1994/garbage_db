package com.garbage.dto;

/**
 * ============================================================
 * WasteItemDto
 * ============================================================
 *
 * Oracle DB의 WASTE_ITEM 테이블에 대응하는 DTO이다.
 *
 * 하나의 쓰레기 품목 자체에 대한 기본 정보를 관리한다.
 *
 * 중요:
 * - 지역 정보는 포함하지 않는다.
 * - 쓰레기 분류 정보도 직접 포함하지 않는다.
 * - 자치단체 정보도 포함하지 않는다.
 *
 * 지역/분류 등의 정보는 각각 다른 테이블에서 관리하고,
 * DAO에서 JOIN하여 조회용 DTO인 WasteDisposalDto 등으로
 * 필요한 정보를 조합한다.
 */
public class WasteItemDto {

    /**
     * WASTE_ITEM.ITEM_ID
     *
     * 쓰레기 품목의 고유 ID.
     *
     * Oracle DB에서 PRIMARY KEY로 사용된다.
     */
    private Long itemId;


    /**
     * WASTE_ITEM.ITEM_CODE
     *
     * 쓰레기 품목을 식별하기 위한 코드.
     *
     * 예:
     * ITEM001
     * ITEM002
     *
     * DB 내부에서 품목을 코드로 관리하거나
     * 다른 시스템과 연계할 때 사용할 수 있다.
     */
    private String itemCode;


    /**
     * WASTE_ITEM.ITEM_NAME
     *
     * 쓰레기 품목명.
     *
     * 실제 시부야구 공식 자료에서 가져온 품목명이 저장된다.
     *
     * 예:
     * アイロン
     * エアコン
     * モバイルバッテリー
     */
    private String itemName;


    /**
     * WASTE_ITEM.DESCRIPTION
     *
     * 쓰레기 품목에 대한 추가 설명.
     *
     * 품목 자체에 대한 설명을 저장하기 위한 필드이다.
     *
     * 배출 방법이나 분류 등의 정보는
     * 각각 DISPOSAL_RULE, WASTE_CATEGORY 등에서 관리한다.
     */
    private String description;


    // ============================================================
    // Getter / Setter
    // ============================================================

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }


    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}