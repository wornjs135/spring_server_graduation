package inu.graduation.sns.repository;

import inu.graduation.sns.domain.Good;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodRepository extends JpaRepository<Good, Long> {
    Optional<Good> findByMemberAndPost(Member member, Post post);
}
