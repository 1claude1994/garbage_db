package com.garbage.domain.normalization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "mapping_rule",
    schema = "normalization"
)
@Getter
@Setter
@NoArgsConstructor
public class MappingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_rule_id")
    private Long mappingRuleId;

    @Column(name = "municipality_id")
    private Long municipalityId;

    @Column(name = "mapping_type", nullable = false, length = 50)
    private String mappingType;

    @Column(name = "source_value", nullable = false, length = 500)
    private String sourceValue;

    @Column(name = "target_value", nullable = false, length = 500)
    private String targetValue;

    @Column(name = "description")
    private String description;

    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}