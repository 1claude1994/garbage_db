package com.garbage.normalization;

import com.garbage.domain.master.WasteCategory;
import com.garbage.domain.master.WasteItem;
import com.garbage.domain.service.DisposalRule;

import com.garbage.master.WasteCategoryService;
import com.garbage.master.WasteItemService;

import com.garbage.raw.RawRecordJsonService;

import com.garbage.service.DisposalRuleSaveService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RawNormalizationService {

    private final RawRecordJsonService rawRecordJsonService;
    private final NormalizationService normalizationService;
    private final WasteItemService wasteItemService;
    private final WasteCategoryService wasteCategoryService;
    private final DisposalRuleSaveService disposalRuleSaveService;

    public RawNormalizationService(
            RawRecordJsonService rawRecordJsonService,
            NormalizationService normalizationService,
            WasteItemService wasteItemService,
            WasteCategoryService wasteCategoryService,
            DisposalRuleSaveService disposalRuleSaveService) {

        this.rawRecordJsonService = rawRecordJsonService;
        this.normalizationService = normalizationService;
        this.wasteItemService = wasteItemService;
        this.wasteCategoryService = wasteCategoryService;
        this.disposalRuleSaveService = disposalRuleSaveService;
    }

    public List<DisposalRule> normalizeAndSaveByBatchId(
            Long batchId,
            Long sourceId,
            Long municipalityId) {

        List<Map<String, String>> rawRecords =
                rawRecordJsonService.findStringJsonByBatchId(batchId);

        return rawRecords.stream()

                /*
                 * 1. Raw 데이터를 정규화
                 */
                .map(rawData ->
                        normalizationService.normalize(
                                sourceId,
                                municipalityId,
                                rawData
                        )
                )

                /*
                 * 2. 실제 쓰레기 품목만 통과
                 *
                 * 전화 / FAX / 문의하기 같은
                 * 페이지 정보는 제외한다.
                 */
                .filter(this::isWasteItem)

                /*
                 * 3. Master + Service 데이터 저장
                 */
                .map(normalizedData -> {

                    /*
                     * WASTE_ITEM 생성 또는 조회
                     */
                    WasteItem wasteItem =
                            wasteItemService.findOrCreate(
                                    normalizedData.get("item_name")
                            );

                    /*
                     * WASTE_CATEGORY 조회
                     */
                    WasteCategory wasteCategory =
                            wasteCategoryService.findByCategoryCode(
                                    normalizedData.get("category_code")
                            );

                    /*
                     * 수거 가능 여부
                     */
                    Boolean isCollectable =
                            Boolean.valueOf(
                                    normalizedData.get(
                                            "is_collectable"
                                    )
                            );

                    /*
                     * DISPOSAL_RULE 저장
                     */
                    return disposalRuleSaveService.save(

                            municipalityId,

                            /*
                             * 현재 웹 데이터에는
                             * 지역 정보가 없으므로 null
                             */
                            null,

                            wasteItem.getItemId(),

                            wasteCategory.getCategoryId(),

                            /*
                             * 분류에 따른 처리 방법
                             */
                            normalizedData.get(
                                    "disposal_method"
                            ),

                            /*
                             * 현재 데이터에는
                             * 별도의 처리 장소가 없으므로 null
                             */
                            null,

                            /*
                             * 출し方・注意点
                             */
                            normalizedData.get(
                                    "instruction"
                            ),

                            /*
                             * 주의사항은 현재
                             * instruction과 별도 데이터가 없으므로 null
                             */
                            null,

                            isCollectable,

                            sourceId
                    );
                })

                .toList();
    }

    /*
     * 실제 쓰레기 품목인지 확인
     */
    private boolean isWasteItem(
            Map<String, String> normalizedData) {

        /*
         * item_name이 없으면 제외
         */
        String itemName =
                normalizedData.get("item_name");

        if (itemName == null
                || itemName.isBlank()) {

            return false;
        }

        /*
         * category_code가 없으면 제외
         *
         * 정상적인 쓰레기 데이터라면
         * 반드시 category_code가 만들어진다.
         */
        String categoryCode =
                normalizedData.get("category_code");

        if (categoryCode == null
                || categoryCode.isBlank()) {

            return false;
        }

        return true;
    }
}
