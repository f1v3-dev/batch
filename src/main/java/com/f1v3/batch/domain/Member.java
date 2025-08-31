package com.f1v3.batch.domain;

import com.f1v3.batch.domain.enums.MemberGrade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberGrade grade;

    @Column(nullable = false)
    private boolean isActive;

    public Member(Long id, String name, MemberGrade grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.isActive = true;
    }

    // todo: 등급 계산?
    //   - 배치를 통해 한 번에 모든 회원의 등급을 처리
}

/*
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    grade VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);
 */