package com.garbage.domain.normalization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "field_mapping",
    schema = "normalization"
)
@Getter
@Setter
@NoArgsConstructor
public class FieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_mapping_id")
    private Long fieldMappingId;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "source_field_name", nullable = false, length = 200)
    private String sourceFieldName;

    @Column(name = "normalized_field_name", nullable = false, length = 100)
    private String normalizedFieldName;

    @Column(name = "data_type", length = 30)
    private String dataType;

    @Column(name = "transformation_rule")
    private String transformationRule;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}