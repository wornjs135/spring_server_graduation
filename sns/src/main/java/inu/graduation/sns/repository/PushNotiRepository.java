package inu.graduation.sns.repository;

import inu.graduation.sns.domain.PushNoti;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PushNotiRepository extends JpaRepository<PushNoti, Long> {

    @Query("select pn from PushNoti pn where pn.memberId =:memberId order by pn.createdAt desc")
    Slice<PushNoti> findAllMyPushNotification(@Param("memberId") Long memberId, Pageable pageable);
}
