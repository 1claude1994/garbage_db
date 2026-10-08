package com.garbage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.garbage.normalization.entity.ChangeHistory;
import com.garbage.normalization.repository.ChangeHistoryRepository;
import com.garbage.normalization.service.ChangeApplyService;

//@Component
public class ChangeApplyTestRunner implements CommandLineRunner {

    private final ChangeHistoryRepository changeHistoryRepository;
    private final ChangeApplyService changeApplyService;

    public ChangeApplyTestRunner(
            ChangeHistoryRepository changeHistoryRepository,
            ChangeApplyService changeApplyService) {

        this.changeHistoryRepository = changeHistoryRepository;
        this.changeApplyService = changeApplyService;
    }

    @Override
    public void run(String... args) {

        System.out.println(
                "===== ChangeApply INSERTED 테스트 시작 ====="
        );

        Long changeId = 20L;

        ChangeHistory history =
                changeHistoryRepository
                        .findById(changeId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "ChangeHistory를 찾을 수 없습니다."
                                                + " | changeId = "
                                                + changeId
                                )
                        );

        System.out.println(
                "ChangeHistory 조회 완료"
                        + " | changeId = " + history.getChangeId()
                        + " | item = " + history.getItemName()
                        + " | changeType = " + history.getChangeType()
        );

        changeApplyService.apply(history);

        System.out.println(
                "===== ChangeApply INSERTED 테스트 완료 ====="
        );
    }
}