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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = MemberController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class MemberControllerTest {

    private final String JWT_ACCESSTOKEN_TEST = "Bearer access토큰";
    private final String JWT_REFRESHTOKEN_TEST = "Bearer refresh토큰";

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
        CreateToken createToken = CreateToken.from(JWT_ACCESSTOKEN_TEST, JWT_REFRESHTOKEN_TEST);

        given(memberService.kakaoLoginMember(any()))
                .willReturn(createToken);

        mockMvc.perform(post("/members/login")
                .header("kakaoToken","kakaoAccessToken"))
                .andExpect(status().isOk())
                .andExpect(header().string("accessToken", createToken.getAccessToken()))
                .andExpect(header().string("refreshToken", createToken.getRefreshToken()))
                .andDo(document("member/create",
                        requestHeaders(
                                headerWithName("kakaoToken").description("카카오 액세스 토큰")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("Access 토큰"),
                                headerWithName("refreshToken").description("Refresh 토큰")
                        )));

        then(memberService).should(times(1)).kakaoLoginMember(any());
    }

    @Test
    @DisplayName("access토큰 재발급")
    void refresh() throws Exception{
        given(memberService.refresh(any(), any()))
                .willReturn(JWT_ACCESSTOKEN_TEST);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/members/refresh")
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

        Member member = Member.createMember(12313, "nana@naver.com");
        member.updateNickname(memberUpdateRequest.getNickname());
        String body = objectMapper.writeValueAsString(memberUpdateRequest);

        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(memberService.updateMember(any(), any()))
                .willReturn(member);

        mockMvc.perform(patch("members/nickname")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(document("member/updateNickname",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestParts(

                        )));
    }

    @Test
    void updateProfileImage() {
    }

    @Test
    void deleteMember() {
    }

    @Test
    void findMemberInfo() {
    }
}