package com.f1v3.batch.service;

import com.f1v3.batch.domain.PointHistory;
import com.f1v3.batch.dummy.PointHistoryDummy;
import com.f1v3.batch.repository.PointHistoryBulkRepositoryV1;
import com.f1v3.batch.repository.PointHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.StopWatch;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PointHistoryServiceTest {

    @Autowired
    PointHistoryService service;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    PointHistoryBulkRepositoryV1 pointHistoryBulkRepository;

    @Test
    @Rollback(false)
    @DisplayName("포인트 내역 100만개 생성 - JPA")
    void bulkInsertInJpa() {

        // 309초 (5분 9초)
        StopWatch stopWatch = new StopWatch();

        List<PointHistory> dummies = PointHistoryDummy.createList(1_000_000);

        stopWatch.start();
        service.bulkInsertInJpa(dummies);
        stopWatch.stop();

        System.out.println("JPA bulkInsert = " + stopWatch.getTotalTimeSeconds() + "초");


        long count = pointHistoryRepository.count();
        assertEquals(1_000_000, count);
    }

    @Test
    @Rollback(false)
    @DisplayName("rewriteBatchedStatements=true 옵션 테스트 - JDBC")
    void rewriteBatchedStatements() {

        // 16초
        StopWatch stopWatch = new StopWatch();

        List<PointHistory> dummies = PointHistoryDummy.createList(1_000_000);

        stopWatch.start();
        pointHistoryBulkRepository.saveAllInBatches(dummies, 100_000);
        stopWatch.stop();

        System.out.println("JDBC rewriteBatchedStatements=true = " + stopWatch.getTotalTimeSeconds() + "초");

        long count = pointHistoryRepository.count();
        assertEquals(1_000_000, count);

        // Exception in thread "mysql-cj-abandoned-connection-cleanup" java.lang.OutOfMemoryError: Java heap space
        // 객체를 너무 많이 생성해서 Heap 영역이 부족해져버림.
    }
}