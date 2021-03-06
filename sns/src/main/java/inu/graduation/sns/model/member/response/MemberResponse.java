package inu.graduation.sns.model.member.response;

import inu.graduation.sns.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String profileThumbnailImageUrl;
    private String backGroundImageUrl;
    private String role;

    public MemberResponse(Member member){
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImage().getProfileImageUrl();
        this.profileThumbnailImageUrl = member.getProfileImage().getProfileThumbnailImageUrl();
        this.backGroundImageUrl = member.getBackGroundImage();
        this.role = member.getRole().toString();
    }
}
