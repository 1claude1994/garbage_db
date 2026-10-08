package com.garbage.domain.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "external_disposal_route", schema = "service")
public class ExternalDisposalRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "route_type", nullable = false, length = 50)
    private String routeType;

    @Column(name = "instruction")
    private String instruction;

    @Column(name = "reservation_required", nullable = false)
    private Boolean reservationRequired = false;

    @Column(name = "schedule_type", length = 50)
    private String scheduleType;

    @Column(name = "reception_start_time")
    private LocalTime receptionStartTime;

    @Column(name = "reception_end_time")
    private LocalTime receptionEndTime;

    @Column(name = "fee_description")
    private String feeDescription;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ExternalDisposalRoute() {
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Boolean getReservationRequired() {
        return reservationRequired;
    }

    public void setReservationRequired(Boolean reservationRequired) {
        this.reservationRequired = reservationRequired;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public LocalTime getReceptionStartTime() {
        return receptionStartTime;
    }

    public void setReceptionStartTime(LocalTime receptionStartTime) {
        this.receptionStartTime = receptionStartTime;
    }

    public LocalTime getReceptionEndTime() {
        return receptionEndTime;
    }

    public void setReceptionEndTime(LocalTime receptionEndTime) {
        this.receptionEndTime = receptionEndTime;
    }

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
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