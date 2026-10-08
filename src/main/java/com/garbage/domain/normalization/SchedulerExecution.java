package com.garbage.domain.normalization;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "scheduler_execution",
    schema = "normalization"
)
public class SchedulerExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long executionId;

    @Column(name = "scheduler_name", nullable = false)
    private String schedulerName;

    @Column(name = "execution_no", nullable = false)
    private Long executionNo;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "records_found")
    private Integer recordsFound;

    @Column(name = "error_message")
    private String errorMessage;

    public SchedulerExecution() {
    }

    public Long getExecutionId() {
        return executionId;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public Long getExecutionNo() {
        return executionNo;
    }

    public void setExecutionNo(Long executionNo) {
        this.executionNo = executionNo;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Integer getRecordsFound() {
        return recordsFound;
    }

    public void setRecordsFound(Integer recordsFound) {
        this.recordsFound = recordsFound;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}