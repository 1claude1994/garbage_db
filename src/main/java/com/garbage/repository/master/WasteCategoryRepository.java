package com.garbage.repository.master;

import com.garbage.domain.master.WasteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WasteCategoryRepository
        extends JpaRepository<WasteCategory, Long> {

    Optional<WasteCategory> findByCategoryCode(String categoryCode);
}