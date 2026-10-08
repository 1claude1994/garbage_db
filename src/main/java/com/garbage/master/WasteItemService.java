package com.garbage.master;

import com.garbage.domain.master.WasteItem;
import com.garbage.repository.master.WasteItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WasteItemService {

    private final WasteItemRepository wasteItemRepository;

    public WasteItemService(WasteItemRepository wasteItemRepository) {
        this.wasteItemRepository = wasteItemRepository;
    }

    public WasteItem findOrCreate(
            String itemName) {

        return wasteItemRepository
                .findByItemName(itemName)
                .orElseGet(() -> {

                    WasteItem wasteItem = new WasteItem();

                    wasteItem.setItemName(itemName);
                    wasteItem.setCreatedAt(
                            java.time.LocalDateTime.now()
                    );
                    wasteItem.setUpdatedAt(
                            java.time.LocalDateTime.now()
                    );

                    return wasteItemRepository.save(wasteItem);
                });
    }
    
    public WasteItem findByItemName(String itemName) {

        return wasteItemRepository
                .findByItemName(itemName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "WasteItem을 찾을 수 없습니다. itemName="
                                + itemName
                ));
    }
}