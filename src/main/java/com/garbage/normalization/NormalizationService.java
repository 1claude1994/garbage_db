package com.garbage.normalization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.domain.normalization.FieldMapping;

@Service
@Transactional(readOnly = true)
public class NormalizationService {

    private final FieldMappingService fieldMappingService;
    private final MappingService mappingService;

    public NormalizationService(
            FieldMappingService fieldMappingService,
            MappingService mappingService) {

        this.fieldMappingService = fieldMappingService;
        this.mappingService = mappingService;
    }

    public Map<String, String> normalize(
            Long sourceId,
            Long municipalityId,
            Map<String, String> rawData) {

        Map<String, String> normalizedData =
                new HashMap<>();

        List<FieldMapping> fieldMappings =
                fieldMappingService.findBySourceId(sourceId);

        for (FieldMapping fieldMapping : fieldMappings) {

            String sourceFieldName =
                    fieldMapping.getSourceFieldName();

            String normalizedFieldName =
                    fieldMapping.getNormalizedFieldName();

            String rawValue =
                    rawData.get(sourceFieldName);

            if (rawValue == null || rawValue.isBlank()) {
                continue;
            }

            String normalizedValue =
                    rawValue.trim();

            /*
             * 1. 품목명
             */
            if ("item_name".equals(normalizedFieldName)) {

                normalizedData.put(
                        "item_name",
                        normalizedValue
                );
            }

            /*
             * 2. 분류
             */
            else if ("category_source".equals(normalizedFieldName)) {

                normalizedData.put(
                        "category_source",
                        normalizedValue
                );

                /*
                 * 페이지 정보는 쓰레기 데이터가 아니므로 제외
                 */
                if (isPageInfo(normalizedValue)) {
                    continue;
                }

                /*
                 * 일반 분류
                 */

                if (isBasicCategory(normalizedValue)) {

                    String categoryCode =
                            mappingService.map(
                                    municipalityId,
                                    "WASTE_CATEGORY",
                                    normalizedValue
                            );

                    normalizedData.put(
                            "category_code",
                            categoryCode
                    );

                    normalizedData.put(
                            "disposal_method",
                            normalizedValue
                    );

                    normalizedData.put(
                            "is_collectable",
                            "true"
                    );
                }

                /*
                 * 특수 분류
                 */
                else {

                    normalizedData.put(
                            "category_code",
                            resolveSpecialCategory(
                                    normalizedValue
                            )
                    );

                    normalizedData.put(
                            "disposal_method",
                            normalizedValue
                    );

                    normalizedData.put(
                            "is_collectable",
                            resolveCollectable(
                                    normalizedValue
                            )
                    );
                }
            }

            /*
             * 3. 출し方・注意点
             */
            else if ("instruction".equals(normalizedFieldName)) {

                normalizedData.put(
                        "instruction",
                        normalizedValue
                );
            }

            /*
             * 4. 그 외 필드
             */
            else {

                normalizedData.put(
                        normalizedFieldName,
                        normalizedValue
                );
            }
        }

        return normalizedData;
    }

    /*
     * 일반적인 4개 분류인지 확인
     */
    private boolean isBasicCategory(
            String category) {

        return "可燃ごみ".equals(category)
                || "不燃ごみ".equals(category)
                || "資源".equals(category)
                || "粗大ごみ".equals(category);
    }

    /*
     * 특수 분류의 기본 category 결정
     */
    private String resolveSpecialCategory(
            String category) {

        /*
         * 불연 쓰레기로 처리할 수 있는 경우
         */
        if ("拠点回収または不燃ごみ".equals(category)) {
            return "NON_BURNABLE";
        }

        /*
         * 가연 쓰레기로 처리할 수 있는 경우
         */
        if ("拠点回収または可燃ごみ".equals(category)) {
            return "BURNABLE";
        }

        /*
         * 자원으로 처리할 수 있는 경우
         */
        if ("拠点回収または資源".equals(category)) {
            return "RESOURCE";
        }

        /*
         * 그 외 특수 처리는
         * OTHER로 분류
         */
        return "OTHER";
    }

    /*
     * 수거 가능 여부 결정
     */
    private String resolveCollectable(
            String category) {

        if ("区では収集しません。".equals(category)) {
            return "false";
        }

        if ("区で収集できないもの".equals(category)) {
            return "false";
        }

        return "true";
    }

    /*
     * 페이지 정보인지 확인
     */
    private boolean isPageInfo(String category) {

        return "お問い合わせフォーム（外部サイト）".equals(category)
                || "03-5467-4300".equals(category)
                || "03-5467-4301".equals(category);
    }
    
    
}
