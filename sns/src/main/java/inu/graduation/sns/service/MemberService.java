package inu.graduation.sns.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.domain.Post;
import inu.graduation.sns.domain.ProfileImage;
import inu.graduation.sns.exception.MemberException;
import inu.graduation.sns.model.common.CreateToken;
import inu.graduation.sns.model.kakao.KaKaoUserResponse;
import inu.graduation.sns.model.member.request.MemberUpdateRequest;
import inu.graduation.sns.model.member.response.FindAllMemberResponse;
import inu.graduation.sns.model.member.response.LoginResponse;
import inu.graduation.sns.model.member.response.MemberNotificationResponse;
import inu.graduation.sns.model.member.response.MemberResponse;
import inu.graduation.sns.model.notification.response.IsAdminNotiResponse;
import inu.graduation.sns.model.notification.response.IsCommentNotiResponse;
import inu.graduation.sns.model.notification.response.IsGoodNotiResponse;
import inu.graduation.sns.repository.MemberRepository;
import inu.graduation.sns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.thumbnailBucket}")
    private String thumbnailBucket;

    @Value("${spring.adminKakaoId1}")
    private Integer adminKakaoId1;

    @Value("${spring.adminKakaoId2}")
    private Integer adminKakaoId2;

    @Value("${spring.adminKakaoId3}")
    private Integer adminKakaoId3;

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final AmazonS3Client amazonS3Client;

    // 로그인
    @Transactional
    public LoginResponse kakaoLoginMember(String kakaoToken, String fcmToken) {
        KaKaoUserResponse kakaoUser = getKakaoUserInfo(kakaoToken);

        Optional<Member> findMember = memberRepository.findByKakaoId(Long.valueOf(kakaoUser.getId()));
        // 서버에 회원 정보가 없으면
        if (!findMember.isPresent()){
            // 일반계정 생성
            if (!kakaoUser.getId().equals(adminKakaoId1) && !kakaoUser.getId().equals(adminKakaoId2) && !kakaoUser.getId().equals(adminKakaoId3)) {
                Member member = Member.createMember(kakaoUser.getId(), fcmToken);
                Member savedMember = memberRepository.save(member);
                CreateToken createToken = jwtTokenProvider.createToken(String.valueOf(savedMember.getId()));
                savedMember.changeRefreshToken(createToken.getRefreshToken());

                return new LoginResponse(createToken, member.getRole());
            } else { // 관리자 계정 생성
                Member savedAdmin = saveAdmin(kakaoUser.getId(), fcmToken);
                CreateToken createToken = jwtTokenProvider.createToken(String.valueOf(savedAdmin.getId()));
                savedAdmin.changeRefreshToken(createToken.getRefreshToken());

                return new LoginResponse(createToken, savedAdmin.getRole());
            }

        } else { // 서버에 회원 정보 있으면 바로 로그인
            Member member = findMember.get();
            CreateToken createToken = jwtTokenProvider.createToken(String.valueOf(member.getId()));
            member.changeRefreshToken(createToken.getRefreshToken());

            if (fcmToken != null) {
                member.changeFcmToken(fcmToken);
            }

            return new LoginResponse(createToken, member.getRole());
        }
    }

    public String refresh(Long memberId, String refreshToken) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));

        String refresh = jwtTokenProvider.refresh(refreshToken.substring(7), findMember);
        return refresh;
    }

    // 닉네임 수정
    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest memberUpdateRequest) {
        // 존재하는 닉네임이면
        if (memberRepository.findByNickname(memberUpdateRequest.getNickname()).isPresent()){
            throw new MemberException("이미 존재하는 닉네임입니다.");
        }
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        findMember.updateNickname(memberUpdateRequest.getNickname());

        return new MemberResponse(findMember);
    }

    // 프로필사진 수정
    @Transactional
    public MemberResponse updateProfileImg(MultipartFile image, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        ProfileImage updateProfileImage = uploadImageS3(image);
        findMember.updateProfileImage(updateProfileImage);

        return new MemberResponse(findMember);
    }

    // 기본 프로필사진으로
    @Transactional
    public MemberResponse defaultProfileImage(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        findMember.defaultProfileImage();

        return new MemberResponse(findMember);
    }

    // 회원 탈퇴
    @Transactional
    public boolean deleteMember(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        List<Post> findPost = postRepository.findByMemberId(findMember.getId());
        if (!findPost.isEmpty()) {
            postRepository.deleteAll(findPost);
        }
        memberRepository.delete(findMember);

        return true;
    }

    // 관리자가 회원 삭제
    @Transactional
    public boolean adminDeleteMember(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        List<Post> findPost = postRepository.findByMemberId(findMember.getId());
        if (!findPost.isEmpty()) {
            postRepository.deleteAll(findPost);
        }
        memberRepository.delete(findMember);

        return true;
    }

    // 관리자가 회원 조회(닉네임으로)
    public MemberResponse adminFindMember(String nickname) {
        Member findMember = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        return new MemberResponse(findMember);
    }

    // (웹) 관리자가 회원 전체 조회
    public Page<FindAllMemberResponse> findAllMember(Pageable pageable) {
        return memberRepository.findAll(pageable).map(member -> new FindAllMemberResponse(member));
    }

    // 회원 정보 조회
    public MemberResponse findMemberInfo(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        return new MemberResponse(findMember);
    }

    // 회원 알림 여부 조회
    public MemberNotificationResponse findNotificationInfo(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        return new MemberNotificationResponse(findMember);
    }

    // 공지사항 알림 여부 설정
    @Transactional
    public IsAdminNotiResponse updateNotification(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        findMember.changeAdminNoti();

        return new IsAdminNotiResponse(findMember.getAdminNoti());
    }

    // 좋아요 알림 여부 설정
    @Transactional
    public IsGoodNotiResponse updateGoodNotification(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        findMember.changeGoodNoti();

        return new IsGoodNotiResponse(findMember.getGoodNoti());
    }

    // 댓글 알림 여부 설정
    @Transactional
    public IsCommentNotiResponse updateCommentNotification(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        findMember.changeCommentNoti();

        return new IsCommentNotiResponse(findMember.getCommentNoti());
    }

    // 로그아웃
    @Transactional
    public void logout(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        findMember.logout();
    }

    // 배경사진 수정
    @Transactional
    public MemberResponse updateBackGroundImage(Long memberId, MultipartFile image) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        ProfileImage updateProfileImage = uploadImageS3(image);
        findMember.updateBackGroundImage(updateProfileImage.getProfileImageUrl());

        return new MemberResponse(findMember);
    }

    // 배경사진 기본이미지로
    @Transactional
    public MemberResponse defaultBackGroundImage(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        findMember.defaultBackGroundImage();

        return new MemberResponse(findMember);
    }

    //카카오api로 카카오 유저정보 받아오기
    public KaKaoUserResponse getKakaoUserInfo(String kakaoAccessToken){
        //post방식으로 key=value 데이터를 요청 (카카오쪽으로)
        RestTemplate rt = new RestTemplate();

        //http 헤더 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //헤더와 바디를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        //http 요청하기/ response에 응답을 받아옴
        ResponseEntity<String> response;
        try {
            response = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoProfileRequest,
                    String.class
            );
        } catch (Exception e) {
            throw new MemberException("kakao 토큰이 잘못되었습니다.");
        }

        ObjectMapper objectMapper2 = new ObjectMapper();
        KaKaoUserResponse kaKaoUserResponse = null;

        try {
            kaKaoUserResponse = objectMapper2.readValue(response.getBody(), KaKaoUserResponse.class);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new MemberException("kakao 사용자 정보를 불러오지 못했습니다.");
        }

        return kaKaoUserResponse;
    }

    // 관리자 계정 저장
    private Member saveAdmin(Integer kakaoId, String fcmToken) {
        Member adminMember = Member.createAdminMember(kakaoId, fcmToken);
        return memberRepository.save(adminMember);
    }

    // 프로필사진 s3에 업로드
    private ProfileImage uploadImageS3(MultipartFile image) {
        ProfileImage imageObject = null;

        if(!image.isEmpty()){
            if(!image.getContentType().startsWith("image")){
                throw new IllegalStateException();
            }
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(image.getSize());
            objectMetadata.setContentType(image.getContentType());
            String imageStoreName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

            try {
                amazonS3Client.putObject(new PutObjectRequest(bucket,imageStoreName, image.getInputStream(), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

                //이미지 url 가져오기
                String profileImageUrl = amazonS3Client.getUrl(bucket, imageStoreName).toString();
                String thumbnailImageUrl = amazonS3Client.getUrl(thumbnailBucket, imageStoreName).toString();

                imageObject = ProfileImage.updateProfileImage(profileImageUrl, thumbnailImageUrl, imageStoreName);
            } catch (Exception e){
                throw new MemberException(e.getMessage());
            }
        }

        return imageObject;
    }
}
