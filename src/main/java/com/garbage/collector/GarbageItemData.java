package com.garbage.collector;

import com.garbage.collector.GarbageItemData;

public class GarbageItemData {

    private String itemName;
    private String category;
    private String instruction;

    public GarbageItemData(
            String itemName,
            String category,
            String instruction) {

        this.itemName = itemName;
        this.category = category;
        this.instruction = instruction;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getInstruction() {
        return instruction;
    }
}