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
                .apply(documentationConfiguration(restDocumentationContextProvider)// mockMvc Rest Docs ??????
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint()) // ????????? ?????????
                        .withResponseDefaults(prettyPrint())) // ????????? ?????????
                .build();
    }

    @Test
    @DisplayName("????????? ?????????")
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
                                headerWithName("kakaoToken").description("????????? ????????? ??????"),
                                headerWithName("fcmToken").description("fcm ??????").optional()
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("Access ??????"),
                                headerWithName("refreshToken").description("Refresh ??????")
                        ),
                        responseFields(
                                fieldWithPath("role").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).kakaoLoginMember(any(), any());
    }

    @Test
    @DisplayName("access?????? ?????????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("?????? Refresh ??????")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("????????? Access ??????")
                        )));

        then(memberService).should(times(1)).refresh(any(), any());
    }

    @Test
    @DisplayName("????????? ??????")
    void updateMemberNickname() throws Exception{
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest();
        memberUpdateRequest.setNickname("?????????");

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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("?????? ????????? URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        then(memberService).should(times(1)).updateMember(any(),any());
    }

    @Test
    @DisplayName("??????????????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        requestParts(
                                partWithName("image").description("????????? ??????????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? ????????? URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("?????? ????????? URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).updateProfileImg(any(), any());
    }

    @Test
    @DisplayName("?????? ????????????????????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("?????? ????????? ????????? URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("?????? ????????? ????????? ????????? URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("?????? ????????? URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).defaultProfileImage(any());
    }

    @Test
    @DisplayName("?????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        )));

        // then
        then(memberService).should(times(1)).deleteMember(any());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("?????? ????????? URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).findMemberInfo(any());
    }

    @Test
    @DisplayName("????????????")
    void logout() throws Exception {
        // given

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/logout")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("member/logout",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        )));

        // then
        then(memberService).should(times(1)).logout(any());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("adminNoti").type(JsonFieldType.BOOLEAN).description("???????????? ?????? ??????"),
                                fieldWithPath("goodNoti").type(JsonFieldType.BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("commentNoti").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????")
                        )));
    }

    @Test
    @DisplayName("???????????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        requestParts(
                                partWithName("image").description("????????? ???????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("????????? ?????? ????????? URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).updateBackGroundImage(any(), any());
    }

    @Test
    @DisplayName("???????????? ??????????????????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("?????? ????????? ????????? URL"),
                                fieldWithPath("profileThumbnailImageUrl").type(JsonFieldType.STRING).description("?????? ????????? ????????? ????????? URL"),
                                fieldWithPath("backGroundImageUrl").type(JsonFieldType.STRING).description("?????? ????????? URL"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).defaultBackGroundImage(any());
    }
}