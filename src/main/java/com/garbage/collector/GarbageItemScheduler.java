package com.garbage.collector;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.garbage.domain.normalization.SchedulerExecution;
import com.garbage.domain.raw.ImportBatch;
import com.garbage.domain.service.CollectionSchedule;
import com.garbage.domain.service.ExternalDisposalRoute;
import com.garbage.domain.service.Fee;
import com.garbage.normalization.service.SchedulerExecutionService;

@Component
public class GarbageItemScheduler {

    private final GarbageItemCollector garbageItemCollector;
    private final CollectionScheduleCollector collectionScheduleCollector;
    private final FeeCollector feeCollector;
    private final ExternalDisposalRouteCollector externalDisposalRouteCollector;
    private final SchedulerExecutionService schedulerExecutionService;

    public GarbageItemScheduler(
            GarbageItemCollector garbageItemCollector,
            CollectionScheduleCollector collectionScheduleCollector,
            FeeCollector feeCollector,
            ExternalDisposalRouteCollector externalDisposalRouteCollector,
            SchedulerExecutionService schedulerExecutionService) {

        this.garbageItemCollector = garbageItemCollector;
        this.collectionScheduleCollector = collectionScheduleCollector;
        this.feeCollector = feeCollector;
        this.externalDisposalRouteCollector =
                externalDisposalRouteCollector;
        this.schedulerExecutionService =
                schedulerExecutionService;
    }

    @Scheduled(fixedDelay = 60000)
    public void collectGarbageItems() {

        SchedulerExecution execution =
                schedulerExecutionService.start();

        System.out.println(
                "===== 渋谷区 Garbage Scheduler 시작 ====="
                + " | executionNo = "
                + execution.getExecutionNo()
        );

        try {

            ImportBatch batch =
                    garbageItemCollector.collect();

            List<CollectionSchedule> schedules =
                    collectionScheduleCollector.collect();

            List<Fee> fees =
                    feeCollector.collect();

            List<ExternalDisposalRoute> routes =
                    externalDisposalRouteCollector.collect();

            schedulerExecutionService.success(
                    execution.getExecutionId(),
                    batch.getBatchId(),
                    batch.getRecordsFound()
            );

            System.out.println(
                    "===== 渋谷区 Garbage Scheduler 성공 ====="
            );

            System.out.println(
                    "executionNo = "
                    + execution.getExecutionNo()
                    + " | batchId = "
                    + batch.getBatchId()
                    + " | items = "
                    + batch.getRecordsFound()
                    + " | schedules = "
                    + schedules.size()
                    + " | fees = "
                    + fees.size()
                    + " | routes = "
                    + routes.size()
            );

        } catch (Exception e) {

            schedulerExecutionService.fail(
                    execution.getExecutionId(),
                    e.getMessage()
            );

            System.out.println(
                    "===== 渋谷区 Garbage Scheduler 실패 ====="
            );

            e.printStackTrace();
        }
    }
}