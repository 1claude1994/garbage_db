package com.garbage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.garbage.normalization.service.ChangeDetectionService;

//@Component
public class ChangeDetectionTestRunner implements CommandLineRunner {

    private final ChangeDetectionService changeDetectionService;

    public ChangeDetectionTestRunner(
            ChangeDetectionService changeDetectionService) {

        this.changeDetectionService = changeDetectionService;
    }

    @Override
    public void run(String... args) {

        System.out.println("===== 변경 감지 테스트 시작 =====");

        int count =
                changeDetectionService.detectChanges(
                        1L,
                        17L,
                        19L
                );

        System.out.println(
                "변경 감지 완료"
                + " | 저장 건수 = " + count
        );

        System.out.println("===== 변경 감지 테스트 종료 =====");
    }
}