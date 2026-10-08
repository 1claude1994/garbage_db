package com.garbage.normalization.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garbage.domain.normalization.SchedulerExecution;
import com.garbage.domain.normalization.SchedulerExecutionRepository;

@Service
public class SchedulerExecutionService {

    private static final String SCHEDULER_NAME =
            "SHIBUYA_GARBAGE_ITEM";

    private final SchedulerExecutionRepository repository;

    public SchedulerExecutionService(
            SchedulerExecutionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SchedulerExecution start() {

        SchedulerExecution execution =
                new SchedulerExecution();

        execution.setSchedulerName(SCHEDULER_NAME);
        execution.setExecutionNo(getNextExecutionNo());
        execution.setStartedAt(LocalDateTime.now());
        execution.setStatus("RUNNING");

        return repository.save(execution);
    }

    @Transactional
    public void success(
            Long executionId,
            Long batchId,
            Integer recordsFound) {

        SchedulerExecution execution =
                repository.findById(executionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "스케줄 실행 이력을 찾을 수 없습니다. "
                                        + "executionId=" + executionId));

        execution.setStatus("SUCCESS");
        execution.setBatchId(batchId);
        execution.setRecordsFound(recordsFound);
        execution.setCompletedAt(LocalDateTime.now());

        repository.save(execution);
    }

    @Transactional
    public void fail(
            Long executionId,
            String errorMessage) {

        SchedulerExecution execution =
                repository.findById(executionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "스케줄 실행 이력을 찾을 수 없습니다. "
                                        + "executionId=" + executionId));

        execution.setStatus("FAILED");
        execution.setErrorMessage(errorMessage);
        execution.setCompletedAt(LocalDateTime.now());

        repository.save(execution);
    }

    private Long getNextExecutionNo() {
        return repository.getNextExecutionNo();
    }
}