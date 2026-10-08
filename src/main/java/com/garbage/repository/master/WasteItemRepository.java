package com.garbage.repository.master;

import com.garbage.domain.master.WasteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WasteItemRepository
        extends JpaRepository<WasteItem, Long> {

    Optional<WasteItem> findByItemName(String itemName);
}