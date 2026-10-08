package com.garbage.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GarbageItemJsonConverter {

    private final ObjectMapper objectMapper;

    public GarbageItemJsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String convert(GarbageItemData item) {

        try {

            Map<String, String> data =
                    new LinkedHashMap<>();

            data.put("品名", item.getItemName());
            data.put("分類", item.getCategory());
            data.put("出し方・注意点", item.getInstruction());

            return objectMapper.writeValueAsString(data);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "GarbageItemData JSON 변환 실패",
                    e
            );
        }
    }
}