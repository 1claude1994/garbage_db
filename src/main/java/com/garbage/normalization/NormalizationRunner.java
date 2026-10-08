package com.garbage.normalization;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class NormalizationRunner implements CommandLineRunner {

    private final RawNormalizationService rawNormalizationService;

    public NormalizationRunner(
            RawNormalizationService rawNormalizationService) {

        this.rawNormalizationService = rawNormalizationService;
    }

    @Override
    public void run(String... args) {

        Long batchId = 7L;
        Long sourceId = 1L;
        Long municipalityId = 1L;

        System.out.println("===== 정규화 시작 =====");
        System.out.println("batchId = " + batchId);

        var result =
                rawNormalizationService.normalizeAndSaveByBatchId(
                        batchId,
                        sourceId,
                        municipalityId
                );

        System.out.println("===== 정규화 완료 =====");
        System.out.println("저장된 DisposalRule 수 = " + result.size());
    }
}