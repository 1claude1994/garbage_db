package com.garbage.normalization.repository;

import com.garbage.normalization.entity.ChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeHistoryRepository
        extends JpaRepository<ChangeHistory, Long> {

    List<ChangeHistory> findByNewBatchIdOrderByChangedAtDesc(Long newBatchId);

    List<ChangeHistory> findByProcessedFalseOrderByChangedAtDesc();
    
    boolean existsByOldBatchIdAndNewBatchIdAndItemName(
            Long oldBatchId,
            Long newBatchId,
            String itemName);
}