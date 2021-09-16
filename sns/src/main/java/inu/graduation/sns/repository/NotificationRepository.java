package inu.graduation.sns.repository;

import inu.graduation.sns.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n order by n.createdAt desc")
    Slice<Notification> findAllNotification(Pageable pageable);
}
