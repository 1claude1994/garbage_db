package com.garbage.domain.service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
    name = "collection_schedule",
    schema = "service"
)
@Getter
@Setter
@NoArgsConstructor
public class CollectionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "municipality_id", nullable = false)
    private Long municipalityId;

    @Column(name = "area_id", nullable = false)
    private Long areaId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

    @Column(name = "week_pattern", length = 30)
    private String weekPattern;

    @Column(name = "collection_time_limit")
    private LocalTime collectionTimeLimit;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "notes")
    private String notes;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}