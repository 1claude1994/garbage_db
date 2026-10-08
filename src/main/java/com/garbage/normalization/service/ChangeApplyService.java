package com.garbage.normalization.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garbage.domain.master.WasteCategory;
import com.garbage.domain.master.WasteItem;
import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.service.DisposalRule;
import com.garbage.master.WasteCategoryService;
import com.garbage.master.WasteItemService;
import com.garbage.normalization.DataChangeHistoryService;
import com.garbage.normalization.MappingService;
import com.garbage.normalization.entity.ChangeHistory;
import com.garbage.normalization.repository.ChangeHistoryRepository;
import com.garbage.raw.ImportBatchService;
import com.garbage.service.DisposalRuleSaveService;

@Service
@Transactional
public class ChangeApplyService {

    private final ChangeHistoryRepository changeHistoryRepository;

    private final ObjectMapper objectMapper;

    private final WasteItemService wasteItemService;

    private final WasteCategoryService wasteCategoryService;

    private final MappingService mappingService;

    private final DisposalRuleSaveService disposalRuleSaveService;

    private final ImportBatchService importBatchService;
    
    private final DataChangeHistoryService dataChangeHistoryService;

    public ChangeApplyService(
            ChangeHistoryRepository changeHistoryRepository,
            ObjectMapper objectMapper,
            WasteItemService wasteItemService,
            WasteCategoryService wasteCategoryService,
            MappingService mappingService,
            DisposalRuleSaveService disposalRuleSaveService,
            ImportBatchService importBatchService,
            DataChangeHistoryService dataChangeHistoryService) {

        this.changeHistoryRepository = changeHistoryRepository;
        this.objectMapper = objectMapper;
        this.wasteItemService = wasteItemService;
        this.wasteCategoryService = wasteCategoryService;
        this.mappingService = mappingService;
        this.disposalRuleSaveService = disposalRuleSaveService;
        this.importBatchService = importBatchService;
        this.dataChangeHistoryService = dataChangeHistoryService;
    }

    public void apply(ChangeHistory history) {

        String changeType = history.getChangeType();

        switch (changeType) {

            case "INSERTED":
                applyInserted(history);
                break;

            case "UPDATED":
                applyUpdated(history);
                break;

            case "DELETED":
                applyDeleted(history);
                break;

            default:
                throw new IllegalArgumentException(
                        "알 수 없는 변경 유형입니다. changeType="
                                + changeType
                );
        }

        markProcessed(history);
    }

    private void markProcessed(ChangeHistory history) {

        history.setProcessed(true);

        history.setProcessedAt(
                java.time.LocalDateTime.now()
        );

        changeHistoryRepository.save(history);
    }

    private void applyInserted(ChangeHistory history) {

        if (history.getNewData() == null) {

            throw new IllegalArgumentException(
                    "INSERTED 변경 이력에 newData가 없습니다."
                            + " | item = " + history.getItemName()
            );
        }
        
        ImportBatch newBatch =
                importBatchService.findById(
                        history.getNewBatchId()
                );

        Long sourceId = newBatch.getSourceId();

        System.out.println(
                "INSERTED sourceId 조회 완료"
                        + " | batchId = " + history.getNewBatchId()
                        + " | sourceId = " + sourceId
        );

        JsonNode newData = history.getNewData();

        String itemName =
                newData.path("品名").asText(null);

        String categorySource =
                newData.path("分類").asText(null);

        if (categorySource == null || categorySource.isBlank()) {

            throw new IllegalArgumentException(
                    "INSERTED 데이터에 분류가 없습니다."
                            + " | item = " + itemName
            );
        }

        /*
         * 실제 카테고리 결정
         *
         * 직접 카테고리:
         * 可燃ごみ
         * 不燃ごみ
         * 資源
         * 粗大ごみ
         *
         * 복합 분류:
         * 拠点回収または可燃ごみ
         * 拠点回収または不燃ごみ
         * 拠点回収または資源
         *
         * 카테고리가 없는 분류:
         * 拠点回収
         * 個別
         * 区では収集しません。
         * 区で収集できないもの
         */
        String categorySourceForMapping =
                extractCategorySource(categorySource);

        WasteCategory wasteCategory = null;

        if (categorySourceForMapping != null) {

            String categoryCode =
                    mappingService.map(
                            history.getMunicipalityId(),
                            "WASTE_CATEGORY",
                            categorySourceForMapping
                    );

            System.out.println(
                    "INSERTED Category 매핑 완료"
                            + " | source = "
                            + categorySourceForMapping
                            + " | categoryCode = "
                            + categoryCode
            );

            wasteCategory =
                    wasteCategoryService.findByCategoryCode(
                            categoryCode
                    );

            System.out.println(
                    "INSERTED WasteCategory 조회 완료"
                            + " | categoryCode = "
                            + categoryCode
                            + " | categoryId = "
                            + wasteCategory.getCategoryId()
            );

        } else {

            System.out.println(
                    "INSERTED Category 없음"
                            + " | 원본分類 = "
                            + categorySource
                            + " | categoryId = null"
            );
        }

        boolean isCollectable = true;

        if ("区では収集しません。".equals(categorySource)
                || "区で収集できないもの".equals(categorySource)) {

            isCollectable = false;
        }

        System.out.println(
                "INSERTED 수거 여부 결정"
                        + " | item = " + itemName
                        + " | isCollectable = " + isCollectable
        );

        String instruction =
                newData.path("出し方・注意点").asText(null);

        if (itemName == null || itemName.isBlank()) {

            throw new IllegalArgumentException(
                    "INSERTED 데이터에 품목명이 없습니다."
                            + " | item = " + history.getItemName()
            );
        }

        WasteItem wasteItem =
                wasteItemService.findOrCreate(itemName);

        System.out.println(
                "INSERTED WasteItem 처리 완료"
                        + " | item = " + wasteItem.getItemName()
                        + " | itemId = " + wasteItem.getItemId()
        );

        System.out.println(
                "INSERTED 반영 대상"
                        + " | item = " + itemName
                        + " | category = " + categorySource
                        + " | instruction = " + instruction
        );

        DisposalRule disposalRule =
                disposalRuleSaveService.save(
                        history.getMunicipalityId(),
                        null,
                        wasteItem.getItemId(),
                        wasteCategory != null
                                ? wasteCategory.getCategoryId()
                                : null,
                        categorySource,
                        null,
                        instruction,
                        null,
                        isCollectable,
                        sourceId
                );

        System.out.println(
                "INSERTED DisposalRule 저장 완료"
                        + " | ruleId = " + disposalRule.getRuleId()
                        + " | itemId = " + disposalRule.getItemId()
                        + " | categoryId = "
                        + disposalRule.getCategoryId()
        );
        
        // INSERTED 변경 이력 기록
        dataChangeHistoryService.record(
                history.getChangeId(),
                history.getMunicipalityId(),
                "WASTE_ITEM",
                wasteItem.getItemId(),
                "item_name",
                null,
                wasteItem.getItemName()
        );

        dataChangeHistoryService.record(
                history.getChangeId(),
                history.getMunicipalityId(),
                "DISPOSAL_RULE",
                disposalRule.getRuleId(),
                "category_id",
                null,
                disposalRule.getCategoryId()
        );

        dataChangeHistoryService.record(
                history.getChangeId(),
                history.getMunicipalityId(),
                "DISPOSAL_RULE",
                disposalRule.getRuleId(),
                "disposal_method",
                null,
                disposalRule.getDisposalMethod()
        );

        dataChangeHistoryService.record(
                history.getChangeId(),
                history.getMunicipalityId(),
                "DISPOSAL_RULE",
                disposalRule.getRuleId(),
                "instruction",
                null,
                disposalRule.getInstruction()
        );

        dataChangeHistoryService.record(
                history.getChangeId(),
                history.getMunicipalityId(),
                "DISPOSAL_RULE",
                disposalRule.getRuleId(),
                "is_collectable",
                null,
                disposalRule.getIsCollectable()
        );
    }

    private void applyUpdated(ChangeHistory history) {

        JsonNode newData = history.getNewData();

        if (newData == null || newData.isNull()) {
            throw new IllegalStateException(
                    "UPDATED newData가 없습니다. changeId="
                            + history.getChangeId());
        }

        ImportBatch newBatch =
                importBatchService.findById(history.getNewBatchId());

        Long sourceId = newBatch.getSourceId();

        String itemName =
                newData.path("品名").asText(null);

        String categorySource =
                newData.path("分類").asText(null);

        String instruction =
                newData.path("出し方・注意点").asText(null);

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalStateException(
                    "UPDATED 품목명이 없습니다. changeId="
                            + history.getChangeId());
        }

        /*
         * UPDATED도 INSERTED와 동일한 방식으로
         * 실제 카테고리가 있는 경우에만 매핑한다.
         */
        String categorySourceForMapping =
                extractCategorySource(categorySource);

        WasteCategory wasteCategory = null;

        if (categorySourceForMapping != null) {

            String categoryCode =
                    mappingService.map(
                            history.getMunicipalityId(),
                            "WASTE_CATEGORY",
                            categorySourceForMapping
                    );

            wasteCategory =
                    wasteCategoryService.findByCategoryCode(
                            categoryCode
                    );

            System.out.println(
                    "UPDATED Category 매핑 완료"
                            + " | source = "
                            + categorySourceForMapping
                            + " | categoryCode = "
                            + categoryCode
            );

        } else {

            System.out.println(
                    "UPDATED Category 없음"
                            + " | 원본分類 = "
                            + categorySource
                            + " | categoryId = null"
            );
        }

        boolean isCollectable =
                !"区では収集しません。".equals(categorySource)
                && !"区で収集できないもの".equals(categorySource);

        WasteItem wasteItem =
                wasteItemService.findByItemName(itemName);

        Long itemId = wasteItem.getItemId();

        DisposalRule oldRule =
                disposalRuleSaveService.findCurrentRule(
                        history.getMunicipalityId(),
                        null,
                        itemId);

        Long newCategoryId =
                wasteCategory != null
                        ? wasteCategory.getCategoryId()
                        : null;

        if (!java.util.Objects.equals(
                oldRule.getCategoryId(),
                newCategoryId)) {

            dataChangeHistoryService.record(
                    history.getChangeId(),
                    history.getMunicipalityId(),
                    "DISPOSAL_RULE",
                    oldRule.getRuleId(),
                    "category_id",
                    oldRule.getCategoryId(),
                    newCategoryId);
        }

        if (!java.util.Objects.equals(
                oldRule.getDisposalMethod(),
                categorySource)) {

            dataChangeHistoryService.record(
                    history.getChangeId(),
                    history.getMunicipalityId(),
                    "DISPOSAL_RULE",
                    oldRule.getRuleId(),
                    "disposal_method",
                    oldRule.getDisposalMethod(),
                    categorySource);
        }
        
        if (!java.util.Objects.equals(
                oldRule.getInstruction(),
                instruction)) {

            dataChangeHistoryService.record(
                    history.getChangeId(),
                    history.getMunicipalityId(),
                    "DISPOSAL_RULE",
                    oldRule.getRuleId(),
                    "instruction",
                    oldRule.getInstruction(),
                    instruction);
        }

        if (!java.util.Objects.equals(
                oldRule.getIsCollectable(),
                isCollectable)) {

            dataChangeHistoryService.record(
                    history.getChangeId(),
                    history.getMunicipalityId(),
                    "DISPOSAL_RULE",
                    oldRule.getRuleId(),
                    "is_collectable",
                    oldRule.getIsCollectable(),
                    isCollectable);
        }

        /*
         * 실제 현재 데이터 반영
         */
        disposalRuleSaveService.save(
                history.getMunicipalityId(),
                null,
                itemId,
                newCategoryId,
                categorySource,
                null,
                instruction,
                null,
                isCollectable,
                sourceId
        );

        System.out.println(
                "UPDATED 반영 완료 | item = "
                + itemName
                + " | itemId = "
                + itemId);
    }

    private void applyDeleted(ChangeHistory history) {

        String itemName = history.getItemName();

        System.out.println(
            "DELETED 반영 시작"
            + " | item = " + itemName
        );

        // 1. 기존 WasteItem 조회
        WasteItem wasteItem =
                wasteItemService.findByItemName(itemName);

        Long itemId = wasteItem.getItemId();

        System.out.println(
            "기존 WasteItem 조회 완료"
            + " | itemId = " + itemId
            + " | itemName = " + itemName
        );

        // 2. 기존 DisposalRule 종료
        DisposalRule endedRule =
                disposalRuleSaveService.endRule(
                        history.getMunicipalityId(),
                        null,
                        itemId
                );

        System.out.println(
            "DisposalRule 종료 완료"
            + " | ruleId = " + endedRule.getRuleId()
            + " | itemId = " + itemId
            + " | effectiveTo = " + endedRule.getEffectiveTo()
        );
        
        // DELETED 변경 이력 기록
        dataChangeHistoryService.record(
                history.getChangeId(),
                history.getMunicipalityId(),
                "DISPOSAL_RULE",
                endedRule.getRuleId(),
                "effective_to",
                null,
                endedRule.getEffectiveTo()
        );

        System.out.println(
                "DELETED 변경 이력 기록 완료"
                + " | changeId = " + history.getChangeId()
                + " | ruleId = " + endedRule.getRuleId()
        );
    }

    /*
     * 공식 사이트의 "分類" 값을 실제 카테고리 값으로 변환한다.
     *
     * 직접 카테고리:
     * 可燃ごみ
     * 不燃ごみ
     * 資源
     * 粗大ごみ
     *
     * 복합 카테고리:
     * 拠点回収または可燃ごみ
     * 拠点回収または不燃ごみ
     * 拠点回収または資源
     *
     * 그 외:
     * 拠点回収
     * 個別
     * 区では収集しません。
     * 区で収集できないもの
     *
     * → category_id = null
     */
    private String extractCategorySource(String categorySource) {

        if (categorySource == null
                || categorySource.isBlank()) {

            return null;
        }

        if ("可燃ごみ".equals(categorySource)
                || "不燃ごみ".equals(categorySource)
                || "資源".equals(categorySource)
                || "粗大ごみ".equals(categorySource)) {

            return categorySource;
        }

        if ("拠点回収または可燃ごみ".equals(categorySource)) {
            return "可燃ごみ";
        }

        if ("拠点回収または不燃ごみ".equals(categorySource)) {
            return "不燃ごみ";
        }

        if ("拠点回収または資源".equals(categorySource)) {
            return "資源";
        }

        return null;
    }
}