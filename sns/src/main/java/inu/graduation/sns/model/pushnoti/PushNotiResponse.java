package inu.graduation.sns.model.pushnoti;

import inu.graduation.sns.domain.PushNoti;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushNotiResponse {

    private Long id;
    private String title;
    private String content;
    private Long postId;
    private LocalDateTime createdAt;

    public PushNotiResponse(PushNoti pushNoti) {
        this.id = pushNoti.getId();
        this.title = pushNoti.getTitle();
        this.content = pushNoti.getContent();
        this.postId = pushNoti.getPostId();
        this.createdAt = pushNoti.getCreatedAt();
    }
}
