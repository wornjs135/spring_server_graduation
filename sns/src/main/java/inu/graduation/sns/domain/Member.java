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

//    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private ProfileImage profileImage;

    private String refreshToken;

    public static Member createMember(Integer kakaoId) {
        Member member = new Member();
        member.kakaoId = Long.valueOf(kakaoId);
//        member.email = email;
        member.nickname = randomNickname();
        member.role = Role.ROLE_MEMBER;
        member.profileImage = ProfileImage.createDefaultProfileImage();
        member.refreshToken = null;
        return member;
    }

    public static Member createAdminMember(Integer kakaoId) {
        Member member = new Member();
        member.kakaoId = Long.valueOf(kakaoId);
//        member.email = email;
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
