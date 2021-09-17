package inu.graduation.sns.controller;

import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMember;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.domain.Role;
import inu.graduation.sns.model.common.CreateToken;
import inu.graduation.sns.model.member.request.MemberUpdateRequest;
import inu.graduation.sns.model.member.response.LoginResponse;
import inu.graduation.sns.model.member.response.MemberNotificationResponse;
import inu.graduation.sns.model.member.response.MemberResponse;
import inu.graduation.sns.model.role.dto.RoleDto;
import inu.graduation.sns.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 카카오 로그인
    @PostMapping("/members/login")
    public ResponseEntity<RoleDto> kakaoLogin(@RequestHeader(name = "kakaoToken") String kakaoToken,
                                              @RequestHeader(required = false, name = "fcmToken") String fcmToken){
        LoginResponse loginResponse = memberService.kakaoLoginMember(kakaoToken, fcmToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", loginResponse.getCreateToken().getAccessToken())
                .header("refreshToken", loginResponse.getCreateToken().getRefreshToken())
                .body(new RoleDto(loginResponse.getRole()));
    }

    // accessToken 재발급
    @PostMapping("/members/refresh")
    public ResponseEntity refresh(@LoginMember Long memberId,
                                  @RequestHeader(name = HttpHeaders.AUTHORIZATION) String refreshToken){
        String refresh = memberService.refresh(memberId, refreshToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", refresh)
                .build();
    }

    // 닉네임 수정
    @PatchMapping("/members/nickname")
    public ResponseEntity<MemberResponse> updateMemberNickname(@LoginMember Long memberId,
                                       @RequestBody @Valid MemberUpdateRequest memberUpdateRequest){

        return ResponseEntity.ok(memberService.updateMember(memberId, memberUpdateRequest));
    }

    // 프로필사진 수정
    @PatchMapping("/members/profileimg")
    public ResponseEntity<MemberResponse> updateProfileImage(@LoginMember Long memberId,
                                             @RequestPart MultipartFile image){
        return ResponseEntity.ok(memberService.updateProfileImg(image, memberId));
    }

    // 기본 프로필사진으로
    @PatchMapping("/members/profileimg/default")
    public ResponseEntity<MemberResponse> defaultProfileImage(@LoginMember Long memberId){
        return ResponseEntity.ok(memberService.defaultProfileImage(memberId));
    }

    // 회원 탈퇴
    @DeleteMapping("/members/delete")
    public ResponseEntity deleteMember(@LoginMember Long memberId){
        memberService.deleteMember(memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회원 정보 조회
    @GetMapping("/members")
    public ResponseEntity<MemberResponse> findMemberInfo(@LoginMember Long memberId){
        return ResponseEntity.ok(memberService.findMemberInfo(memberId));
    }

    // 회원 알림여부 조회
    @GetMapping("/members/notification/info")
    public ResponseEntity<MemberNotificationResponse> findNotificationInfo(@LoginMember Long memberId) {
        return ResponseEntity.ok(memberService.findNotificationInfo(memberId));
    }

    // 로그아웃
    @GetMapping("/members/logout")
    public ResponseEntity logout(@LoginMember Long memberId){
        memberService.logout(memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 배경사진 수정
    @PatchMapping("/members/backgroundimg")
    public ResponseEntity<MemberResponse> updateBackGroundImage(@LoginMember Long memberId,
                                                                @RequestPart MultipartFile image) {
        return ResponseEntity.ok(memberService.updateBackGroundImage(memberId, image));
    }

    // 배경사진 기본이미지로
    @PatchMapping("/members/backgroundimg/default")
    public ResponseEntity<MemberResponse> defaultBackGroundImage(@LoginMember Long memberId) {
        return ResponseEntity.ok(memberService.defaultBackGroundImage(memberId));
    }
}
