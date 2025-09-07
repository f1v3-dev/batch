package com.f1v3.batch.dummy;

import com.f1v3.batch.domain.Member;
import com.f1v3.batch.domain.enums.MemberRole;

import java.util.List;

public class MemberDummy {


    public static List<Member> createDummyUsers() {
        // 관리자 생성
        Member admin = Member.builder()
                .username("admin")
                .password("admin123")
                .email("admin@example.com")
                .role(MemberRole.ADMIN)
                .build();

        // 판매자들 생성
        Member seller1 = Member.builder()
                .username("seller1")
                .password("seller123")
                .email("seller1@example.com")
                .role(MemberRole.SELLER)
                .build();

        Member seller2 = Member.builder()
                .username("seller2")
                .password("seller123")
                .email("seller2@example.com")
                .role(MemberRole.SELLER)
                .build();

        // 고객들 생성
        Member customer1 = Member.builder()
                .username("customer1")
                .password("customer123")
                .email("customer1@example.com")
                .role(MemberRole.CUSTOMER)
                .build();

        Member customer2 = Member.builder()
                .username("customer2")
                .password("customer123")
                .email("customer2@example.com")
                .role(MemberRole.CUSTOMER)
                .build();

        return List.of(admin, seller1, seller2, customer1, customer2);
    }
}
