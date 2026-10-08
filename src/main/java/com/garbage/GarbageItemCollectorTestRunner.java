package com.garbage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.garbage.collector.GarbageItemCollector;

//@Component
public class GarbageItemCollectorTestRunner implements CommandLineRunner {

    private final GarbageItemCollector garbageItemCollector;

    public GarbageItemCollectorTestRunner(
            GarbageItemCollector garbageItemCollector) {
        this.garbageItemCollector = garbageItemCollector;
    }

    @Override
    public void run(String... args) {

        System.out.println("===== GarbageItemCollector 수동 테스트 시작 =====");

        garbageItemCollector.collect();

        System.out.println("===== GarbageItemCollector 수동 테스트 종료 =====");
    }
}