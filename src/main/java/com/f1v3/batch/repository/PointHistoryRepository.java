package com.f1v3.batch.repository;

import com.f1v3.batch.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // todo: JPA Repository로 대량 insert 할 때 성능 이슈가 있음 (1건씩 insert 하는 문제)
    //      -> 해결 방안: Spring Data JDBC, native query ...
}
