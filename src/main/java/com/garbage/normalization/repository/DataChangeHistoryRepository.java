package com.garbage.normalization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garbage.normalization.entity.DataChangeHistory;

public interface DataChangeHistoryRepository
        extends JpaRepository<DataChangeHistory, Long> {

    List<DataChangeHistory> findByChangeIdOrderByHistoryIdAsc(
            Long changeId);

    List<DataChangeHistory> findByEntityTypeAndEntityIdOrderByChangedAtDesc(
            String entityType,
            Long entityId);
}