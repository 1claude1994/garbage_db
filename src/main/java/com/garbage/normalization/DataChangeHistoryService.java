package com.garbage.normalization;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.normalization.entity.DataChangeHistory;
import com.garbage.normalization.repository.DataChangeHistoryRepository;

@Service
@Transactional
public class DataChangeHistoryService {

    private final DataChangeHistoryRepository repository;

    public DataChangeHistoryService(
            DataChangeHistoryRepository repository) {
        this.repository = repository;
    }

    public void record(
            Long changeId,
            Long municipalityId,
            String entityType,
            Long entityId,
            String fieldName,
            Object oldValue,
            Object newValue) {

        DataChangeHistory history = new DataChangeHistory();

        history.setChangeId(changeId);
        history.setMunicipalityId(municipalityId);
        history.setEntityType(entityType);
        history.setEntityId(entityId);
        history.setFieldName(fieldName);

        history.setOldValue(
                oldValue == null ? null : String.valueOf(oldValue)
        );

        history.setNewValue(
                newValue == null ? null : String.valueOf(newValue)
        );

        history.setChangedAt(LocalDateTime.now());

        repository.save(history);
    }
}