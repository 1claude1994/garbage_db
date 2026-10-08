package com.garbage.domain.raw;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_batch", schema = "raw")
@Getter
@Setter
@NoArgsConstructor
public class ImportBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "api_source_id")
    private Long apiSourceId;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "records_found")
    private Integer recordsFound = 0;

    @Column(name = "records_processed")
    private Integer recordsProcessed = 0;

    @Column(name = "records_inserted")
    private Integer recordsInserted = 0;

    @Column(name = "records_updated")
    private Integer recordsUpdated = 0;

    @Column(name = "records_skipped")
    private Integer recordsSkipped = 0;

    @Column(name = "error_count")
    private Integer errorCount = 0;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}