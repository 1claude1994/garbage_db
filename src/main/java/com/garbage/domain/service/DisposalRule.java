package com.garbage.domain.service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "disposal_rule",
    schema = "service"
)
@Getter
@Setter
@NoArgsConstructor
public class DisposalRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "municipality_id", nullable = false)
    private Long municipalityId;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "category_id", nullable = true)
    private Long categoryId;

    @Column(name = "disposal_method", length = 100)
    private String disposalMethod;

    @Column(name = "disposal_location", length = 200)
    private String disposalLocation;

    @Column(name = "instruction")
    private String instruction;

    @Column(name = "caution")
    private String caution;

    @Column(name = "is_collectable", nullable = false)
    private Boolean isCollectable = true;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}