package inu.graduation.sns.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


// 좋아요, 댓글 알림
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNoti extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pushNoti_id")
    private Long id;

    private String title;

    private String content;

    private Long memberId;
    private Long postId;

    public static PushNoti createPushNoti(String title, String content, Post post) {
        PushNoti pushNoti = new PushNoti();
        pushNoti.title = title;
        pushNoti.content = content;
        pushNoti.memberId = post.getMember().getId();
        pushNoti.postId = post.getId();
        return pushNoti;
    }
}
