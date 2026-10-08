package com.garbage.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * COLLECTION_SCHEDULE 테이블과 대응하는 DTO
 *
 * Oracle 테이블:
 * COLLECTION_SCHEDULE
 *
 * 하나의 DTO는 하나의 수거요일 규칙을 의미한다.
 *
 * 예:
 * 상원 1丁目
 * 가연성 쓰레기 → 수요일
 *
 * dayOfWeek = 3
 * weekPattern = null
 *
 * 불연성 쓰레기 → 제2월요일
 *
 * dayOfWeek = 1
 * weekPattern = "第2"
 */
public class CollectionScheduleDto {

    // COLLECTION_SCHEDULE.SCHEDULE_ID
    private Long scheduleId;

    // COLLECTION_SCHEDULE.MUNICIPALITY_ID
    private Long municipalityId;

    // COLLECTION_SCHEDULE.AREA_ID
    private Long areaId;

    // COLLECTION_SCHEDULE.CATEGORY_ID
    private Long categoryId;

    /*
     * 요일
     *
     * 1 = 월요일
     * 2 = 화요일
     * 3 = 수요일
     * 4 = 목요일
     * 5 = 금요일
     * 6 = 토요일
     * 7 = 일요일
     *
     * Oracle:
     * COLLECTION_SCHEDULE.DAY_OF_WEEK
     */
    private Integer dayOfWeek;

    /*
     * 월간 반복 규칙
     *
     * 일반적인 주 2회 수거:
     * null
     *
     * 월 1회 수거:
     * "第1"
     * "第2"
     * "第3"
     * "第4"
     *
     * Oracle:
     * COLLECTION_SCHEDULE.WEEK_PATTERN
     */
    private String weekPattern;

    // Oracle: COLLECTION_SCHEDULE.COLLECTION_TIME_LIMIT
    private LocalTime collectionTimeLimit;

    // Oracle: COLLECTION_SCHEDULE.EFFECTIVE_FROM
    private LocalDate effectiveFrom;

    // Oracle: COLLECTION_SCHEDULE.EFFECTIVE_TO
    private LocalDate effectiveTo;

    // Oracle: COLLECTION_SCHEDULE.NOTES
    private String notes;

    // Oracle: COLLECTION_SCHEDULE.SOURCE_ID
    private Long sourceId;

    // Oracle: COLLECTION_SCHEDULE.CREATED_AT
    private LocalDateTime createdAt;

    // Oracle: COLLECTION_SCHEDULE.UPDATED_AT
    private LocalDateTime updatedAt;


    // ==============================
    // Getter / Setter
    // ==============================

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getWeekPattern() {
        return weekPattern;
    }

    public void setWeekPattern(String weekPattern) {
        this.weekPattern = weekPattern;
    }

    public LocalTime getCollectionTimeLimit() {
        return collectionTimeLimit;
    }

    public void setCollectionTimeLimit(LocalTime collectionTimeLimit) {
        this.collectionTimeLimit = collectionTimeLimit;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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