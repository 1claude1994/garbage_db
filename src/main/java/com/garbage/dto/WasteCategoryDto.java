package com.garbage.dto;

import java.time.LocalDateTime;

/**
 * WASTE_CATEGORY 테이블과 대응하는 DTO
 *
 * Oracle 테이블:
 * WASTE_CATEGORY
 *
 * 쓰레기 분류 정보를 전달하기 위한 DTO이다.
 *
 * 예:
 * BURNABLE      → 可燃ごみ
 * NON_BURNABLE  → 不燃ごみ
 * RESOURCE      → 資源
 * OVERSIZED     → 粗大ごみ
 * OTHER         → その他
 */
public class WasteCategoryDto {

    // WASTE_CATEGORY.CATEGORY_ID
    // 쓰레기 분류의 고유 ID
    private Long categoryId;

    // WASTE_CATEGORY.CATEGORY_CODE
    // 시스템에서 사용하는 분류 코드
    // 예: BURNABLE, NON_BURNABLE, RESOURCE
    private String categoryCode;

    // WASTE_CATEGORY.CATEGORY_NAME
    // 실제 분류명
    // 예: 可燃ごみ, 不燃ごみ, 資源
    private String categoryName;

    // WASTE_CATEGORY.DESCRIPTION
    // 해당 분류에 대한 설명
    private String description;

    // WASTE_CATEGORY.CREATED_AT
    // 데이터 생성 일시
    private LocalDateTime createdAt;

    // WASTE_CATEGORY.UPDATED_AT
    // 데이터 수정 일시
    private LocalDateTime updatedAt;


    // ==============================
    // Getter / Setter
    // ==============================

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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