package com.garbage.master;

import com.garbage.domain.master.WasteCategory;
import com.garbage.repository.master.WasteCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WasteCategoryService {

    private final WasteCategoryRepository wasteCategoryRepository;

    public WasteCategoryService(
            WasteCategoryRepository wasteCategoryRepository) {

        this.wasteCategoryRepository = wasteCategoryRepository;
    }

    public WasteCategory findByCategoryCode(
            String categoryCode) {

        return wasteCategoryRepository
                .findByCategoryCode(categoryCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "분류 코드를 찾을 수 없습니다. categoryCode="
                        + categoryCode
                ));
    }
}