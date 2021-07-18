package inu.graduation.sns.controller;

import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMember;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.model.common.CreateToken;
import inu.graduation.sns.model.member.request.MemberUpdateRequest;
import inu.graduation.sns.model.member.response.MemberResponse;
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
    public ResponseEntity kakaoLogin(@RequestHeader String kakaoToken){
        CreateToken createToken = memberService.kakaoLoginMember(kakaoToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", createToken.getAccessToken())
                .header("refreshToken", createToken.getRefreshToken())
                .build();
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
        Member updatedMember = memberService.updateMember(memberId, memberUpdateRequest);

        return ResponseEntity.ok(new MemberResponse(updatedMember));
    }

    // 프로필사진 수정
    @PatchMapping("/members/profileimg")
    public ResponseEntity updateProfileImage(@LoginMember Long memberId,
                                             @RequestPart MultipartFile image){
        memberService.updateProfileImg(image, memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
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
        MemberResponse findMemberInfo = memberService.findMemberInfo(memberId);

        return ResponseEntity.ok(findMemberInfo);
    }

    // 로그아웃
    @GetMapping("/members/logout")
    public ResponseEntity logout(@LoginMember Long memberId){
        memberService.logout(memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
