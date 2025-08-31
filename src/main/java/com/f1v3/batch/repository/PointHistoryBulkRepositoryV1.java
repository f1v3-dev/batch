package com.f1v3.batch.repository;

import com.f1v3.batch.domain.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class PointHistoryBulkRepositoryV1 {

    private final JdbcTemplate jdbcTemplate;

    /*
    TODO: 생각해볼만한 점
        1. 배치의 타임아웃 설정은 어떻게 해야할까?
        2. 트랜잭션 관련 설정은 어떻게 해야할까? (Isolation Level, Rollback 등)
        3. 에러 발생시 재시도는 어느 시점부터 해야할까? - 아예 처음부터? 끊긴 부분을 알 수 있을까?
        4. 배치 사이즈는 동적으로 조절하는게 좋은걸까? -> JVM Heap 메모리 상황에 영향을 미치진 않을까?
        5. 재시도 로직은 어떻게 구성해야 할까? (실패 처리에 대해서 고려해봐야 함.) -> 트랜잭션 범위랑 같이 고민
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAllInBatches(List<PointHistory> list, int batchSize) {

        String sql = """
                INSERT INTO point_history 
                    (member_id, points, balance_after, transaction_type, earn_reason, spend_reason, description, batch_processed, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;


        for (int i = 0; i < list.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, list.size());
            List<PointHistory> batch = list.subList(i, endIndex);

            jdbcTemplate.batchUpdate(sql,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int index) throws SQLException {


                            PointHistory ph = batch.get(index);
                            ps.setLong(1, ph.getMemberId());
                            ps.setInt(2, ph.getPoints());
                            ps.setInt(3, ph.getBalanceAfter());
                            ps.setString(4, ph.getTransactionType().name());

                            if (ph.getEarnReason() != null) {
                                ps.setString(5, ph.getEarnReason().name());
                            } else {
                                ps.setNull(5, Types.VARCHAR);
                            }

                            if (ph.getSpendReason() != null) {
                                ps.setString(6, ph.getSpendReason().name());
                            } else {
                                ps.setNull(6, Types.VARCHAR);
                            }

                            if (ph.getDescription() != null) {
                                ps.setString(7, ph.getDescription());
                            } else {
                                ps.setNull(7, Types.VARCHAR);
                            }

                            ps.setBoolean(8, ph.getBatchProcessed());
                            ps.setObject(9, ph.getCreatedAt()); // LocalDateTime 매핑
                        }

                        @Override
                        public int getBatchSize() {
                            return batch.size();
                        }
                    });
        }
    }
}
