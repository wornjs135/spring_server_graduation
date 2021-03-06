package inu.graduation.sns.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.model.common.CreateToken;
import inu.graduation.sns.model.kakao.KakaoTokenRequest;
import inu.graduation.sns.model.member.request.MemberUpdateRequest;
import inu.graduation.sns.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static inu.graduation.sns.TestObject.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = MemberController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext wac, RestDocumentationContextProvider restDocumentationContextProvider){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .alwaysDo(print())
                .apply(documentationConfiguration(restDocumentationContextProvider)// mockMvc Rest Docs 설정
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint()) // 예쁘게 포맷팅
                        .withResponseDefaults(prettyPrint())) // 예쁘게 포맷팅
                .build();
    }

    @Test
    @DisplayName("카카오 로그인")
    void kakaoLogin() throws Exception {
        // given
        given(memberService.kakaoLoginMember(any(), any()))
                .willReturn(TEST_MEMBER_LOGIN_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/members/login")
                .header("kakaoToken","kakaoAccessToken").header("fcmToken", "fcmToken"))
                .andExpect(status().isOk())
                .andExpect(header().string("accessToken", TEST_MEMBER_LOGIN_RESPONSE.getCreateToken().getAccessToken()))
                .andExpect(header().string("refreshToken", TEST_MEMBER_LOGIN_RESPONSE.getCreateToken().getRefreshToken()))
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_ROLE_DTO)))
                .andDo(document("member/create",
                        requestHeaders(
                                headerWithName("kakaoToken").description("카카오 액세스 토큰"),
                                headerWithName("fcmToken").description("fcm 토큰").optional()
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("Access 토큰"),
                                headerWithName("refreshToken").description("Refresh 토큰")
                        ),
                        responseFields(
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(memberService).should(times(1)).kakaoLoginMember(any(), any());
    }

    @Test
    @DisplayName("access토큰 재발급")
    void refresh() throws Exception{
        given(memberService.refresh(any(), any()))
                .willReturn(JWT_ACCESSTOKEN_TEST);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/members/refresh")
                .header(HttpHeaders.AUTHORIZATION, JWT_REFRESHTOKEN_TEST))
                .andExpect(status().isOk())
                .andExpect(header().string("accessToken", JWT_ACCESSTOKEN_TEST))
                .andDo(document("member/refresh",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("기존 Refresh 토큰")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("새로운 Access 토큰")
                        )));

        then(memberService).should(times(1)).refresh(any(), any());
    }

    @Test
    @DisplayName("닉네임 수정")
    void updateMemberNickname() throws Exception{
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest();
        memberUpdateRequest.setNickname("황주환");

        String body = objectMapper.writeValueAsString(memberUpdateRequest);

        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(memberService.updateMember(any(), any()))
                .willReturn(TEST_MEMBER_RESPONSE);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/members/nickname")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE)))
                .andDo(document("member/updateNickname",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("수정할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("수정된 닉네임"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("프로필 썸네일 이미지 URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("배경 이미지 URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        then(memberService).should(times(1)).updateMember(any(),any());
    }

    @Test
    @DisplayName("프로필사진 수정")
    void updateProfileImage() throws Exception {
        // given
        given(memberService.updateProfileImg(any(), any()))
                .willReturn(TEST_MEMBER_RESPONSE_UPDATE_PROFILEIMG);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/members/profileimg");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });

        // when
        mockMvc.perform(builder.file(TEST_IMAGE_FILE1)
                .header(HttpHeaders.AUTHORIZATION,JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE_UPDATE_PROFILEIMG)))
                .andDo(document("member/updateProfileImg",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestParts(
                                partWithName("image").description("수정할 프로필사진 이미지")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("수정된 프로필 이미지 URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("수정된 프로필 썸네일 이미지 URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("배경 이미지 URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(memberService).should(times(1)).updateProfileImg(any(), any());
    }

    @Test
    @DisplayName("기본 프로필사진으로 변경")
    void defaultProfileimg() throws Exception{
        // given
        given(memberService.defaultProfileImage(any()))
                .willReturn(TEST_MEMBER_RESPONSE_DEFAULT_PROFILEIMG);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/members/profileimg/default")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE_DEFAULT_PROFILEIMG)))
                .andDo(document("member/defaultProfileImg",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("기본 프로필 이미지 URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("기본 프로필 썸네일 이미지 URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("배경 이미지 URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(memberService).should(times(1)).defaultProfileImage(any());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteMember() throws Exception{
        // given
        given(memberService.deleteMember(any()))
                .willReturn(true);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/members/delete")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("member/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        )));

        // then
        then(memberService).should(times(1)).deleteMember(any());
    }

    @Test
    @DisplayName("회원 정보 조회")
    void findMemberInfo() throws Exception{
        // given
        given(memberService.findMemberInfo(any()))
                .willReturn(TEST_MEMBER_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE)))
                .andDo(document("member/findInfo",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("프로필 썸네일 이미지 URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("배경 이미지 URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(memberService).should(times(1)).findMemberInfo(any());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        // given

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/logout")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("member/logout",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        )));

        // then
        then(memberService).should(times(1)).logout(any());
    }

    @Test
    @DisplayName("회원 알림설정 여부 조회")
    void findNotificationInfo() throws Exception {
        // given
        given(memberService.findNotificationInfo(any()))
                .willReturn(TEST_MEMBER_NOTIFICATION_RESPONSE1);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/notification/info")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_NOTIFICATION_RESPONSE1)))
                .andDo(document("member/findNotiInfo",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("adminNoti").type(JsonFieldType.BOOLEAN).description("공지사항 알림 여부"),
                                fieldWithPath("goodNoti").type(JsonFieldType.BOOLEAN).description("좋아요 알림 여부"),
                                fieldWithPath("commentNoti").type(JsonFieldType.BOOLEAN).description("댓글 알림 여부")
                        )));
    }

    @Test
    @DisplayName("배경사진 수정")
    void updateBackGroundImage() throws Exception {
        // given
        given(memberService.updateBackGroundImage(any(), any()))
                .willReturn(TEST_MEMBER_RESPONSE);

        // when
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/members/backgroundimg");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });

        // when
        mockMvc.perform(builder.file(TEST_IMAGE_FILE4)
                        .header(HttpHeaders.AUTHORIZATION,JWT_ACCESSTOKEN_TEST)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE)))
                .andDo(document("member/updateBackGroundImg",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestParts(
                                partWithName("image").description("수정할 배경사진 이미지")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("프로필 썸네일 이미지 URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("수정된 배경 이미지 URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(memberService).should(times(1)).updateBackGroundImage(any(), any());
    }

    @Test
    @DisplayName("배경사진 기본이미지로")
    void defaultBackGroundImage() throws Exception {
        // given
        given(memberService.defaultBackGroundImage(any()))
                .willReturn(TEST_MEMBER_RESPONSE2);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/members/backgroundimg/default")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE2)))
                .andDo(document("member/defaultBackGroundImg",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("기본 프로필 이미지 URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("기본 프로필 썸네일 이미지 URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("배경 이미지 URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(memberService).should(times(1)).defaultBackGroundImage(any());
    }
}