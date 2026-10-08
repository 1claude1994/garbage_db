package com.garbage.collector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.domain.service.ExternalDisposalRoute;
import com.garbage.repository.service.ExternalDisposalRouteRepository;

@Service
@Transactional
public class ExternalDisposalRouteCollector {

    private static final Long SOURCE_ID = 1L;

    private final ExternalDisposalRouteRepository routeRepository;

    public ExternalDisposalRouteCollector(
            ExternalDisposalRouteRepository routeRepository) {

        this.routeRepository = routeRepository;
    }

    public List<ExternalDisposalRoute> collect() {

        System.out.println(
                "===== 渋谷区 外部処理ルート 수집 시작 ====="
        );

        List<Object[]> sources =
                routeRepository.findSpecialDisposalItems();

        System.out.println(
                "특수 처리 대상 품목 수 = " + sources.size()
        );

        routeRepository.deleteAll();

        List<ExternalDisposalRoute> result =
                new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        int nonCollectableCount = 0;
        int collectionPointCount = 0;
        int individualCount = 0;

        int provider1Count = 0;
        int provider2Count = 0;
        int provider3Count = 0;
        int provider4Count = 0;

        for (Object[] row : sources) {

            Long itemId =
                    ((Number) row[0]).longValue();

            String itemName =
                    row[1] != null
                            ? row[1].toString()
                            : "";

            String disposalMethod =
                    row[2] != null
                            ? row[2].toString()
                            : "";

            String instruction =
                    row[3] != null
                            ? row[3].toString()
                            : "";

            Boolean isCollectable =
                    row[4] != null
                            ? (Boolean) row[4]
                            : false;

            String routeType;

            if ("個別".equals(disposalMethod)) {

                routeType = "INDIVIDUAL";
                individualCount++;

            } else if ("拠点回収".equals(disposalMethod)) {

                routeType = "COLLECTION_POINT";
                collectionPointCount++;

            } else if (!isCollectable) {

                routeType = "EXTERNAL_DISPOSAL";
                nonCollectableCount++;

            } else {

                routeType = "EXTERNAL_DISPOSAL";
                nonCollectableCount++;
            }

            /*
             * provider 자동 연결
             *
             * 1 = 家電リサイクル受付センター
             * 2 = リネットジャパンリサイクル株式会社
             * 3 = 一般社団法人電池工業会
             * 4 = 一般社団法人JBRC
             */
            Long providerId = resolveProviderId(itemName);

            if (providerId != null) {

                if (providerId == 1L) {
                    provider1Count++;
                } else if (providerId == 2L) {
                    provider2Count++;
                } else if (providerId == 3L) {
                    provider3Count++;
                } else if (providerId == 4L) {
                    provider4Count++;
                }
            }

            ExternalDisposalRoute route =
                    new ExternalDisposalRoute();

            route.setItemId(itemId);
            route.setProviderId(providerId);
            route.setRouteType(routeType);

            String routeInstruction =
                    "품목=" + itemName;

            if (!instruction.isBlank()) {

                routeInstruction +=
                        " | " + instruction;
            }

            route.setInstruction(routeInstruction);

            route.setReservationRequired(false);
            route.setScheduleType(null);
            route.setReceptionStartTime(null);
            route.setReceptionEndTime(null);
            route.setFeeDescription(null);
            route.setSourceId(SOURCE_ID);
            route.setCreatedAt(now);
            route.setUpdatedAt(now);

            result.add(route);
        }

        List<ExternalDisposalRoute> saved =
                routeRepository.saveAll(result);

        System.out.println(
                "===== 渋谷区 外部処理ルート 수집 완료 ====="
        );

        System.out.println(
                "저장된 Route 수 = "
                + saved.size()
        );

        System.out.println(
                "EXTERNAL_DISPOSAL = "
                + nonCollectableCount
        );

        System.out.println(
                "COLLECTION_POINT = "
                + collectionPointCount
        );

        System.out.println(
                "INDIVIDUAL = "
                + individualCount
        );

        System.out.println(
                "provider 1 = "
                + provider1Count
        );

        System.out.println(
                "provider 2 = "
                + provider2Count
        );

        System.out.println(
                "provider 3 = "
                + provider3Count
        );

        System.out.println(
                "provider 4 = "
                + provider4Count
        );

        return saved;
    }

    private Long resolveProviderId(String itemName) {

        /*
         * 家電リサイクル受付センター
         *
         * エアコン
         * テレビ
         * 冷蔵庫
         * 洗濯機
         * 衣類乾燥機
         */
        if ("エアコン".equals(itemName)
                || "テレビ".equals(itemName)
                || "冷蔵庫".equals(itemName)
                || "洗濯機".equals(itemName)
                || "衣類乾燥機".equals(itemName)) {

            return 1L;
        }

        /*
         * リネットジャパン
         *
         * パソコン
         */
        if ("パソコン".equals(itemName)) {
            return 2L;
        }

        /*
         * 電池工業会
         *
         * ボタン電池
         */
        if ("ボタン電池".equals(itemName)) {
            return 3L;
        }

        /*
         * JBRC
         *
         * 小型充電式電池
         * モバイルバッテリー
         */
        if ("小型充電式電池 （リチウムイオン電池・ニカド電池・ニッケル水素電池）"
                .equals(itemName)
                || "モバイルバッテリー".equals(itemName)) {

            return 4L;
        }

        /*
         * 현재 등록된 provider로 확실하게 연결할 수 없는
         * 품목은 NULL을 유지한다.
         */
        return null;
    }
}