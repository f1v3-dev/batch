package com.f1v3.batch.service;

import com.f1v3.batch.domain.PointHistory;
import com.f1v3.batch.repository.PointHistoryBulkRepositoryV1;
import com.f1v3.batch.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryRepository repository;
    private final PointHistoryBulkRepositoryV1 bulkRepository;

    @Transactional
    public void bulkInsertInJpa(List<PointHistory> dummies) {
;        // JPA를 사용하는 메서드
        repository.saveAll(dummies);
    }


}
