package com.garbage.collector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.domain.service.Fee;
import com.garbage.parser.FeeParser;
import com.garbage.parser.FeeParser.FeeSourceDto;
import com.garbage.repository.service.FeeRepository;

@Service
@Transactional
public class FeeCollector {

    private static final String SOURCE_URL =
            "https://www.city.shibuya.tokyo.jp/"
            + "kurashi/gomi/kateigomi/"
            + "sibuyaku_sodaitesuuryou.html";

    private static final Long MUNICIPALITY_ID = 1L;

    private static final Long SOURCE_ID = 1L;

    private final FeeRepository feeRepository;

    public FeeCollector(
            FeeRepository feeRepository) {

        this.feeRepository = feeRepository;
    }

    public List<Fee> collect()
            throws Exception {

        System.out.println(
                "===== 渋谷区 요금 수집 시작 ====="
        );

        /*
         * 1. 공식 요금 페이지 파싱
         */
        List<FeeSourceDto> sources =
                FeeParser.parse(SOURCE_URL);

        System.out.println(
                "파싱된 요금 품목 수 = "
                + sources.size()
        );

        /*
         * 2. 기존 요금 삭제
         *
         * 최신 공식 데이터를 기준으로
         * municipality_id = 1의 요금을
         * 전체 교체한다.
         */
        feeRepository.deleteByMunicipalityId(
                MUNICIPALITY_ID
        );

        System.out.println(
                "기존 요금 데이터 삭제 완료"
        );

        /*
         * 3. 최신 요금 데이터 생성
         */
        List<Fee> result =
                new ArrayList<>();

        LocalDateTime now =
                LocalDateTime.now();

        int matchedItemCount = 0;
        int unmatchedItemCount = 0;

        for (FeeSourceDto source : sources) {

            Fee fee =
                    new Fee();

            fee.setMunicipalityId(
                    MUNICIPALITY_ID
            );

            /*
             * 공식 요금표 품목명과
             * master.waste_item 품목명이
             * 정확히 일치하는 경우 item_id 연결
             */
            Long itemId =
                    feeRepository.findItemIdByItemName(
                            source.getItemName()
                    );

            if (itemId != null) {

                fee.setItemId(itemId);

                matchedItemCount++;

            } else {

                /*
                 * 요금표에는 존재하지만
                 * 일반 품목 테이블과 직접 연결되지 않는 경우
                 *
                 * service.fee.item_id는 NULL 허용이므로
                 * 요금 데이터 자체는 보존한다.
                 */
                fee.setItemId(null);

                unmatchedItemCount++;
            }

            /*
             * 비용 종류
             */
            fee.setFeeType(
                    "OVERSIZED"
            );

            /*
             * 공식 요금
             */
            fee.setAmount(
                    source.getAmount()
            );

            /*
             * 통화
             */
            fee.setCurrency(
                    "JPY"
            );

            /*
             * 요금 단위
             */
            fee.setUnit(
                    "1건"
            );

            /*
             * A권 / B권 정보를 조건 설명에 저장
             */
            StringBuilder condition =
                    new StringBuilder();

            condition.append("품목=");

            condition.append(
                    source.getItemName()
            );

            if (source.getATicket() != null
                    && !source.getATicket().isBlank()) {

                condition.append(", A券=");

                condition.append(
                        source.getATicket()
                );
            }

            if (source.getBTicket() != null
                    && !source.getBTicket().isBlank()) {

                condition.append(", B券=");

                condition.append(
                        source.getBTicket()
                );
            }

            fee.setConditionDescription(
                    condition.toString()
            );

            fee.setEffectiveFrom(null);
            fee.setEffectiveTo(null);

            fee.setSourceId(
                    SOURCE_ID
            );

            fee.setCreatedAt(now);
            fee.setUpdatedAt(now);

            result.add(fee);
        }

        /*
         * 4. 일괄 저장
         */
        List<Fee> saved =
                feeRepository.saveAll(result);

        System.out.println(
                "===== 渋谷区 요금 수집 완료 ====="
        );

        System.out.println(
                "저장된 요금 수 = "
                + saved.size()
        );

        System.out.println(
                "master.waste_item 연결 = "
                + matchedItemCount
        );

        System.out.println(
                "master.waste_item 미연결 = "
                + unmatchedItemCount
        );

        return saved;
    }
}