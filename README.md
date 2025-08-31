# 배치 작업 시나리오

상점에서 아이템을 구매할 때 '포인트'를 사용하는 시스템에서, 포인트 사용 내역을 기록하는 테이블이 필요하다.  
이 테이블의 내용을 기반으로 포인트 적립/사용 통계를 만들 수 있으며, 월별 회원 등급 산정에도 활용할 수 있다.

특히, 100만 건 이상의 대량 히스토리 데이터를 효율적으로 처리해야 하는 요구사항이 있다고 가정하고, 단순한 CRUD를 넘어서 성능, 확장성, 안정성을 모두 고려한 배치 처리 시스템을 설계해보자.

## 테이블 구조

_회원 테이블_
```sql
CREATE TABLE member
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100)                                  NOT NULL,
    grade     ENUM ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM') NOT NULL DEFAULT 'BRONZE',
    is_active BOOLEAN                                       NOT NULL DEFAULT TRUE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
```

_포인트 히스토리 테이블_

```sql
-- 1. 포인트 히스토리 테이블 생성
CREATE TABLE point_history
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id        BIGINT                 NOT NULL,
    transaction_type ENUM ('EARN', 'SPEND') NOT NULL,
    earn_reason      ENUM ('DAILY_LOGIN', 'MISSION', 'BOOK_REGISTRATION', 'BOOK_REVIEW', 'ADMIN_ADJUSTMENT'),
    spend_reason     ENUM ('GACHA', 'ADMIN_ADJUSTMENT'),
    points           INT                    NOT NULL,
    balance_after    INT                    NOT NULL,
    descript      VARCHAR(255),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    batch_processed  BOOLEAN   DEFAULT FALSE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


-- 2. 인덱스 추가
# 거래 유형별 조회를 위한 복합 인덱스 (포인트 적립/사용 통계)
CREATE INDEX idx_earn_type_reason ON point_history (transaction_type, earn_reason);
CREATE INDEX idx_spend_type_reason ON point_history (transaction_type, spend_reason);

# 회원별 포인트 내역 조회를 위한 복합 인덱스 (회원 등급 산정)
CREATE INDEX idx_member_created ON point_history (member_id, created_at);

# 배치 처리 대상 조회를 위한 인덱스 (배치 처리 확인)
CREATE INDEX idx_batch_processed ON point_history (batch_processed);

-- 3. 제약조건 추가 (MySQL 8.0.16 이상)
# 거래 유형에 따른 사유 필드 검증 
ALTER TABLE point_history
    ADD CONSTRAINT check_earn_reason
        CHECK (
            (transaction_type = 'EARN' AND earn_reason IS NOT NULL AND spend_reason IS NULL) OR
            (transaction_type = 'SPEND' AND spend_reason IS NOT NULL AND earn_reason IS NULL)
            );

```

물론 위와 같이 ENUM 타입은 유연성이 떨어지므로, 실제 운영 환경에서는 VARCHAR 타입을 사용하든, 별도의 테이블을 만들어 관리하든 더 나은 방식을 선택하는 것이 좋다.

# 고민한 내용

## 1. ID 생성 전략

```java
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //...
}
```

ID 생성 전략을 단순히 Auto Increment로 설정을 했지만 문제가 있다. JPA를 사용하는 상황에서 IDENTITY(Auto Increment)를 사용하면 ID 값 채번 과정이 필수가 된다.

즉, `session.persist()` 시점에 DB에 INSERT 쿼리가 바로 실행되고, 그 이후에야 ID 값을 가져올 수 있다.

다른 문제도 존재함.

- Auto Increment Lock 문제: `innodb_autoinc_lock_mode` 설정에 따라 배치 성능 저하가 될 문제가 있음.
- 분산 환경 고려: Auto Increment가 아닌 다른 ID 생성 전략의 경우 ID 충돌이 발생할 수 있음.
- 인덱스 성능: 클러스터링 인덱스 특성상, 순차적이지 않은 ID는 성능상 이점을 갖지 못함.

**검토한 대안**
- UUID: 무작위 값으로 인해 인덱스 페이지 분할 문제
- Snowflake: 분산 환경에서 충돌 없는 ID 생성 가능, 시간 기반으로 어느 정도 순차적인 ID 생성 가능, But, 복잡성 증가

## 2. Bulk Insert 

**JPA vs JDBC**

```java
// JPA의 한계 인식
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    // saveAll()은 내부적으로 개별 save() 호출
    // EntityManager의 persist(), isNew() 체크, ID 생성 등으로 DB 왕복 발생
}

// 해결책: JdbcTemplate 활용
@Repository
public class PointHistoryBulkRepository {
    // rewriteBatchedStatements=true를 통해 bulk insert 구현
}
```

100만 건의 데이터 삽입 테스트 결과

- JPA: 309초(약 5분)의 시간이 걸림, 단일 INSERT로 1건씩 처리됨. 
- JDBC: 16초의 시간이 걸림, Multi-value INSERT 지원

JDBC의 batchUpdate() 메서드를 통해 약 20배의 성능 향상을 확인할 수 있음.

## 구체적인 구현 내용

### 1. 배치 처리

```java
public void saveAllInBatches(List<PointHistory> list, int batchSize) {
    String sql = """
            INSERT INTO point_history 
                (member_id, points, balance_after, transaction_type, earn_reason, spend_reason, description, batch_processed, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    for (int i = 0; i < list.size(); i += batchSize) {
        int endIndex = Math.min(i + batchSize, list.size());
        List<PointHistory> batch = list.subList(i, endIndex);
        
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            // 배치 단위로 처리하여 OutOfMemoryError 방지
        });
    }
}

```

> 배치 단위를 잘 설정하는 것이 중요함. 너무 작으면 오버헤드가 커지고, 너무 크면 메모리 부족 문제가 발생할 수 있음. <br/>
> `java.lang.OutOfMemoryError: Java heap space` 발생..

**생각해볼 문제**

- 배치의 타임아웃 설정은 어떻게 해야할까?
- 트랜잭션 관련 설정은 어떻게 해야할까? (Isolation Level, Rollback 등)
- 에러 발생시 재시도는 어느 시점부터 해야할까? - 아예 처음부터? 끊긴 부분을 알 수 있을까?
- 배치 사이즈는 동적으로 조절하는게 좋은걸까? -> JVM Heap 메모리 상황에 영향을 미치진 않을까?

### 2. MySQL 설정 최적화

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/your_database?rewriteBatchedStatements=true
```

- `rewriteBatchedStatements=true`: MySQL JDBC 드라이버가 배치 INSERT를 단일 Multi-value INSERT로 변환하여 전송하도록 함. 네트워크 오버헤드 감소.

위의 옵션을 통해 아래와 같은 효과를 볼 수 있음.

```sql
-- 원래 의도한 배치
INSERT INTO table VALUES (?);
INSERT INTO table VALUES (?);
INSERT INTO table VALUES (?);

-- rewriteBatchedStatements=true로 변환된 결과
INSERT INTO table VALUES (1),(2),(3);
```


추가적으로 InnoDB 설정을 조정하여 대량 INSERT 작업의 성능을 올릴 수 있다고 한다.

```sql
SET innodb_buffer_pool_size = '8G';
SET innodb_change_buffering = 'all';
SET innodb_max_dirty_pages_pct = 90;
```

- 메모리 활용 극대화: 8GB 버퍼 풀 + 90% dirty pages = 대용량 데이터를 메모리에서 처리
- I/O 최적화: Change buffering + dirty pages 지연 = 디스크 접근 최소화

---

# 고민되는 것들

## 1. 트랜잭션의 전파 옵션

배치 처리 작업 자체를 하나의 트랜잭션으로 보는게 맞을까? 아니면, 청크 단위로 트랜잭션을 나누는게 좋을까?

나눴을 때의 장점으로는 롤백, 재시도 시점이 명확해지지만 단점으로는 각 작업이 독립적인 트랜잭션/커넥션을 사용하게 된다.

## 2. 배치를 실패했을 때의 동작

배치 작업이 실패했을 때 단순 `Retry` 로 해결할 수 있을까? 계속해서 실패한다면 어떻게 대응해야할까?

적절한 재시도 횟수 및 간격을 설정하고 개발자가 직접 재시도를 수행할 수 있는 별도의 API를 제공하는 것도 방법이 될 수 있다.

## 3. 적절한 배치 사이즈 

너무 과도한 크기로 설정한다면 OOM(OutOfMemoryError)이 발생할 수 있으며, 데이터베이스 측에서도 너무 큰 트랜잭션을 처리하는데 부담이 될 수 있다.

반면 너무 작은 크기로 설정한다면 네트워크 왕복이 많아진다. 