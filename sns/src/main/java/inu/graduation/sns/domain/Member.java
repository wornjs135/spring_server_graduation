package inu.graduation.sns.domain;

import inu.graduation.sns.domain.state.MemberState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private Long kakaoId;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private ProfileImage profileImage;

    private String refreshToken;

    private String fcmToken;

    private Boolean goodNoti;

    private Boolean commentNoti;

    private Boolean adminNoti;

    private String backGroundImage;

    public static Member createMember(Integer kakaoId, String fcmToken) {
        Member member = new Member();
        member.kakaoId = Long.valueOf(kakaoId);
        member.nickname = randomNickname();
        member.role = Role.ROLE_MEMBER;
        member.profileImage = ProfileImage.createDefaultProfileImage();
        member.refreshToken = null;
        member.fcmToken = null;
        member.goodNoti = true;
        member.commentNoti = true;
        member.adminNoti = true;
        if (fcmToken != null) {
            member.fcmToken = fcmToken;
        }
        member.backGroundImage = null;
        return member;
    }

    public static Member createAdminMember(Integer kakaoId, String fcmToken) {
        Member member = new Member();
        member.kakaoId = Long.valueOf(kakaoId);
        member.nickname = "관리자";
        member.role = Role.ROLE_ADMIN;
        member.profileImage = ProfileImage.createDefaultProfileImage();
        member.refreshToken = null;
        member.fcmToken = null;
        member.goodNoti = true;
        member.commentNoti = true;
        member.adminNoti = true;
        if (fcmToken != null) {
            member.fcmToken = fcmToken;
        }
        member.backGroundImage = null;
        return member;
    }

    public boolean updateNickname(String nickname) {
        this.nickname = nickname;
        return true;
    }

    public boolean updateProfileImage(ProfileImage updateProfileImage) {
        this.profileImage = updateProfileImage;
        return true;
    }

    public void defaultProfileImage() {
        this.profileImage = ProfileImage.createDefaultProfileImage();
    }

    public void logout(){
        this.refreshToken = null;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void changeFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public void changeAdminNoti() {
        if (this.getAdminNoti().equals(true)) {
            this.adminNoti = false;
        } else {
            this.adminNoti = true;
        }
    }

    public void changeGoodNoti() {
        if (this.getGoodNoti().equals(true)) {
            this.goodNoti = false;
        } else {
            this.goodNoti = true;
        }
    }

    public void changeCommentNoti() {
        if (this.getCommentNoti().equals(true)) {
            this.commentNoti = false;
        } else {
            this.commentNoti = true;
        }
    }

    // 기본 랜덤닉네임 생성
    public static String randomNickname(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit,rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}