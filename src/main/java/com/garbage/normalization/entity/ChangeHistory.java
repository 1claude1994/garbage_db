package com.garbage.normalization.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "change_history", schema = "normalization")
public class ChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "change_id")
    private Long changeId;

    @Column(name = "municipality_id", nullable = false)
    private Long municipalityId;

    @Column(name = "old_batch_id")
    private Long oldBatchId;

    @Column(name = "new_batch_id", nullable = false)
    private Long newBatchId;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "change_type", nullable = false, length = 20)
    private String changeType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_data", columnDefinition = "jsonb")
    private JsonNode oldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private JsonNode newData;

    @Column(name = "old_hash", length = 64)
    private String oldHash;

    @Column(name = "new_hash", length = 64)
    private String newHash;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;


    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }


    public Long getChangeId() {
        return changeId;
    }

    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Long getOldBatchId() {
        return oldBatchId;
    }

    public void setOldBatchId(Long oldBatchId) {
        this.oldBatchId = oldBatchId;
    }

    public Long getNewBatchId() {
        return newBatchId;
    }

    public void setNewBatchId(Long newBatchId) {
        this.newBatchId = newBatchId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public JsonNode getOldData() {
        return oldData;
    }

    public void setOldData(JsonNode oldData) {
        this.oldData = oldData;
    }

    public JsonNode getNewData() {
        return newData;
    }

    public void setNewData(JsonNode newData) {
        this.newData = newData;
    }

    public String getOldHash() {
        return oldHash;
    }

    public void setOldHash(String oldHash) {
        this.oldHash = oldHash;
    }

    public String getNewHash() {
        return newHash;
    }

    public void setNewHash(String newHash) {
        this.newHash = newHash;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public boolean getProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}