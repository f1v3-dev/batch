package com.f1v3.batch.repository.member;

import com.f1v3.batch.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
