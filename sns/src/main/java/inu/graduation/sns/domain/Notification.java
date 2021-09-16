package inu.graduation.sns.domain;

import inu.graduation.sns.model.notification.request.CreateNotificationRequest;
import inu.graduation.sns.model.notification.request.UpdateNotificationRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


// 공지사항
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String title;

    private String content;

    public static Notification createNotification(CreateNotificationRequest createNotificationRequest) {
        Notification notification = new Notification();
        notification.title = createNotificationRequest.getTitle();
        notification.content = createNotificationRequest.getContent();
        return notification;
    }

    public void updateNotification(UpdateNotificationRequest updateNotificationRequest) {
        this.title = updateNotificationRequest.getTitle();
        this.content = updateNotificationRequest.getContent();
    }
}
