package inu.graduation.sns.repository;

import inu.graduation.sns.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByKakaoId(Long kakaoId);
}
