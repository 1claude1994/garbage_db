package com.garbage.domain.raw;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_record", schema = "raw")
@Getter
@Setter
@NoArgsConstructor
public class RawRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "response_id")
    private Long responseId;

    @Column(name = "record_type", length = 50)
    private String recordType;

    @Column(name = "source_row_number")
    private Integer sourceRowNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_data", nullable = false, columnDefinition = "jsonb")
    private String rawData;

    @Column(name = "raw_text")
    private String rawText;

    @Column(name = "record_hash", length = 128)
    private String recordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}