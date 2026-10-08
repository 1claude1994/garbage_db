package com.garbage.domain.master;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "area",
    schema = "master"
)
@Getter
@Setter
@NoArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "municipality_id", nullable = false)
    private Long municipalityId;

    @Column(name = "parent_area_id")
    private Long parentAreaId;

    @Column(name = "area_code", length = 50)
    private String areaCode;

    @Column(name = "area_name", nullable = false, length = 100)
    private String areaName;

    @Column(name = "area_type", length = 30)
    private String areaType;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}