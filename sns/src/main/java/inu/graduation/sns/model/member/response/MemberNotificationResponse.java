package inu.graduation.sns.model.member.response;

import inu.graduation.sns.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberNotificationResponse {

    private Boolean adminNoti;
    private Boolean goodNoti;
    private Boolean commentNoti;

    public MemberNotificationResponse(Member member) {
        this.adminNoti = member.getAdminNoti();
        this.goodNoti = member.getGoodNoti();
        this.commentNoti = member.getCommentNoti();
    }
}
