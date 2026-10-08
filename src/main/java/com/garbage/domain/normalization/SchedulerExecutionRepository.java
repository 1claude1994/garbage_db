package com.garbage.domain.normalization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SchedulerExecutionRepository
        extends JpaRepository<SchedulerExecution, Long> {

    @Query(
        value = "SELECT nextval("
              + "'normalization.scheduler_execution_no_seq')",
        nativeQuery = true
    )
    Long getNextExecutionNo();
}