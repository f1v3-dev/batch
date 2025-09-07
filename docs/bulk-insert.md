# Bulk Insert

100만 건의 데이터를 한 번에 밀어넣어야 하는 상황이 있다고 가정을 해보자.

- e.g. 쇼핑몰에서 판매자가 올린 상품들에 대해서 검수 후 새벽 시간에 일괄적으로 실제 상품 테이블에 반영하는 작업

## JPA - saveAll

JPA를 사용하는 상황에서 데이터를 MySQL에 저장하기 위해서는 `saveAll` 메서드를 사용할 수 있다.

하지만, JPA를 사용하는 상황에 `@Id` 생성 전략이 `Auto Increment(IDENTITY)`인 경우에 내부적으로 ID 채번을 위해 INSERT 쿼리를 1건씩 실행하는 문제가 있다.

## JdbcTemplate - batchUpdate
