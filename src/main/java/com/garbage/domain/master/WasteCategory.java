package com.garbage.domain.master;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "waste_category",
    schema = "master"
)
@Getter
@Setter
@NoArgsConstructor
public class WasteCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(
        name = "category_code",
        nullable = false,
        unique = true,
        length = 50
    )
    private String categoryCode;

    @Column(
        name = "category_name",
        nullable = false,
        length = 100
    )
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
}