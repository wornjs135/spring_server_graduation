package inu.graduation.sns.domain;

import inu.graduation.sns.domain.state.MemberState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private Long kakaoId;

    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private ProfileImage profileImage;

    private String refreshToken;

    public static Member createMember(Integer kakaoId, String email) {
        Member member = new Member();
        member.kakaoId = Long.valueOf(kakaoId);
        member.email = email;
        member.nickname = "기본 닉네임";
        member.role = Role.ROLE_MEMBER;
        member.profileImage = ProfileImage.createDefaultProfileImage();
        member.refreshToken = null;
        return member;
    }

    public static Member createAdminMember(Integer kakaoId, String email) {
        Member member = new Member();
        member.kakaoId = Long.valueOf(kakaoId);
        member.email = email;
        member.nickname = "관리자";
        member.role = Role.ROLE_ADMIN;
        member.profileImage = ProfileImage.createDefaultProfileImage();
        member.refreshToken = null;
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

    public void logout(){
        this.refreshToken = null;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
