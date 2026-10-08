package com.garbage.normalization.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garbage.normalization.entity.ChangeHistory;
import com.garbage.normalization.repository.ChangeHistoryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ChangeDetectionService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ChangeHistoryRepository changeHistoryRepository;
    private final ObjectMapper objectMapper;

    public ChangeDetectionService(
            ChangeHistoryRepository changeHistoryRepository,
            ObjectMapper objectMapper) {

        this.changeHistoryRepository = changeHistoryRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 이전 batch와 새로운 batch를 비교하여
     * INSERTED / UPDATED / DELETED 변경사항을 저장한다.
     */
    @Transactional
    public int detectChanges(
            Long municipalityId,
            Long oldBatchId,
            Long newBatchId) {

        String sql = """
            SELECT
                COALESCE(old_data.item_name, new_data.item_name) AS item_name,

                CASE
                    WHEN old_data.item_name IS NULL THEN 'INSERTED'
                    WHEN new_data.item_name IS NULL THEN 'DELETED'
                    WHEN old_data.record_hash <> new_data.record_hash
                        THEN 'UPDATED'
                END AS change_type,

                old_data.raw_data AS old_data,
                new_data.raw_data AS new_data,

                old_data.record_hash AS old_hash,
                new_data.record_hash AS new_hash

            FROM (
                SELECT
                    raw_data->>'品名' AS item_name,
                    raw_data,
                    record_hash
                FROM raw.raw_record
                WHERE batch_id = :oldBatchId
            ) old_data

            FULL OUTER JOIN (
                SELECT
                    raw_data->>'品名' AS item_name,
                    raw_data,
                    record_hash
                FROM raw.raw_record
                WHERE batch_id = :newBatchId
            ) new_data

            ON old_data.item_name = new_data.item_name

            WHERE
                old_data.item_name IS NULL
                OR new_data.item_name IS NULL
                OR old_data.record_hash <> new_data.record_hash

            ORDER BY item_name
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> changes = entityManager
                .createNativeQuery(sql)
                .setParameter("oldBatchId", oldBatchId)
                .setParameter("newBatchId", newBatchId)
                .getResultList();

        int savedCount = 0;

        for (Object[] row : changes) {

            String itemName = (String) row[0];
            String changeType = (String) row[1];

            System.out.println(
                    "변경 감지"
                    + " | item = " + itemName
                    + " | type = " + changeType
            );

//            // 이미 같은 batch 간의 동일 품목 변경사항이 저장되어 있으면 건너뛴다.
//            if (changeHistoryRepository
//                    .existsByOldBatchIdAndNewBatchIdAndItemName(
//                            oldBatchId,
//                            newBatchId,
//                            itemName)) {
//                continue;
//            }

            ChangeHistory history = new ChangeHistory();

            history.setMunicipalityId(municipalityId);
            history.setOldBatchId(oldBatchId);
            history.setNewBatchId(newBatchId);
            history.setItemName(itemName);
            history.setChangeType(changeType);

            try {

                if (row[2] != null) {
                    history.setOldData(
                        objectMapper.readTree(row[2].toString())
                    );
                }

                if (row[3] != null) {
                    history.setNewData(
                        objectMapper.readTree(row[3].toString())
                    );
                }

            } catch (Exception e) {
                throw new IllegalStateException(
                    "변경 이력 JSON 변환 실패"
                    + " | item = " + itemName,
                    e
                );
            }

            if (row[4] != null) {
                history.setOldHash((String) row[4]);
            }

            if (row[5] != null) {
                history.setNewHash((String) row[5]);
            } 
            
            

            history.setProcessed(false);
            
            System.out.println(
                    "ChangeHistory 저장 시도"
                    + " | item = " + history.getItemName()
                    + " | type = " + history.getChangeType()
            );
            
            changeHistoryRepository.save(history);
            
            System.out.println(
                    "ChangeHistory 저장 완료"
                    + " | item = " + history.getItemName()
                    + " | type = " + history.getChangeType()
            );

            savedCount++;
        }

        return savedCount;
    }
    
    /**
     * 최초 batch를 기준 데이터로 등록한다.
     *
     * 이전 batch가 없기 때문에
     * 현재 batch의 모든 품목을 INSERTED로 처리한다.
     */
    @Transactional
    public int detectInitialChanges(
            Long municipalityId,
            Long newBatchId) {

        String sql = """
            SELECT
                raw_data->>'品名' AS item_name,
                raw_data,
                record_hash
            FROM raw.raw_record
            WHERE batch_id = :newBatchId
            ORDER BY raw_data->>'品名'
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> records = entityManager
                .createNativeQuery(sql)
                .setParameter("newBatchId", newBatchId)
                .getResultList();

        int savedCount = 0;

        for (Object[] row : records) {

            String itemName = (String) row[0];

            ChangeHistory history = new ChangeHistory();

            history.setMunicipalityId(municipalityId);

            /*
             * 최초 batch이므로 이전 batch는 없다.
             */
            history.setOldBatchId(null);

            history.setNewBatchId(newBatchId);
            history.setItemName(itemName);
            history.setChangeType("INSERTED");

            try {

                if (row[1] != null) {
                    history.setNewData(
                        objectMapper.readTree(row[1].toString())
                    );
                }

            } catch (Exception e) {
                throw new IllegalStateException(
                    "최초 변경 이력 JSON 변환 실패"
                    + " | item = " + itemName,
                    e
                );
            }

            if (row[2] != null) {
                history.setNewHash((String) row[2]);
            }

            history.setProcessed(false);

            System.out.println(
                    "최초 데이터 등록"
                    + " | item = " + history.getItemName()
                    + " | type = " + history.getChangeType()
            );

            changeHistoryRepository.save(history);

            savedCount++;
        }

        return savedCount;
    }
}