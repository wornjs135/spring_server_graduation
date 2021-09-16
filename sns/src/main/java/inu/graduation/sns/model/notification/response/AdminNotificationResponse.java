package inu.graduation.sns.model.notification.response;

import inu.graduation.sns.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminNotificationResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminNotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.createdAt = notification.getCreatedAt();
        this.updatedAt = notification.getUpdatedAt();
    }
}
