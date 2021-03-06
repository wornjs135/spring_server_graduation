package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.model.notification.response.AdminNotificationResponse;
import inu.graduation.sns.model.pushnoti.PushNotiResponse;
import inu.graduation.sns.service.MemberService;
import inu.graduation.sns.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.ArrayList;
import java.util.List;

import static inu.graduation.sns.TestObject.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;


@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class NotificationControllerTest {

    @MockBean
    private NotificationService notificationService;

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
    void setUp(WebApplicationContext wac, RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .alwaysDo(print())
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("???????????? ???????????? ?????? ??????")
    void settingNotification() throws Exception {
        // given
        given(memberService.updateNotification(any()))
                .willReturn(TEST_IS_ADMIN_NOTIFICATION_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/setting/adminnoti")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_IS_ADMIN_NOTIFICATION_RESPONSE)))
                .andDo(document("notification/changeAdminNoti",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("adminNoti").type(JsonFieldType.BOOLEAN).description("???????????? ?????? ?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).updateNotification(any());
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ??????")
    void settingGoodNotification() throws Exception {
        // given
        given(memberService.updateGoodNotification(any()))
                .willReturn(TEST_IS_GOOD_NOTIFICATION_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/setting/goodnoti")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_IS_GOOD_NOTIFICATION_RESPONSE)))
                .andDo(document("notification/changeGoodNoti",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("goodNoti").type(JsonFieldType.BOOLEAN).description("????????? ?????? ?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).updateGoodNotification(any());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ??????")
    void settingCommentNotification() throws Exception {
        // given
        given(memberService.updateCommentNotification(any()))
                .willReturn(TEST_IS_COMMENT_NOTIFICATION_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/setting/commentnoti")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_IS_COMMENT_NOTIFICATION_RESPONSE)))
                .andDo(document("notification/changeCommentNoti",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("commentNoti").type(JsonFieldType.BOOLEAN).description("?????? ?????? ?????? ??????")
                        )));

        // then
        then(memberService).should(times(1)).updateCommentNotification(any());
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ??????")
    void findAllNotificationWeb() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<AdminNotificationResponse> adminNotiList = new ArrayList<>();
        adminNotiList.add(TEST_NOTIFICATION_RESPONSE1); adminNotiList.add(TEST_NOTIFICATION_RESPONSE3);

        PageImpl<AdminNotificationResponse> adminNotiPage = new PageImpl<>(adminNotiList, pageRequest, adminNotiList.size());

        given(notificationService.findAllNotificationWeb(any()))
                .willReturn(adminNotiPage);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/notifications")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(adminNotiPage)))
                .andDo(document("notification/findAllAdminNotiWeb",
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("???????????? ????????????"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("???????????? ????????????"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ???"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("???????????? ??????"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("??? ????????? ??????"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????")
                        )));

        // then
        then(notificationService).should(times(1)).findAllNotificationWeb(any());
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ??????")
    void findAllNotificationApp() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<AdminNotificationResponse> adminNotiList = new ArrayList<>();
        adminNotiList.add(TEST_NOTIFICATION_RESPONSE1); adminNotiList.add(TEST_NOTIFICATION_RESPONSE3);

        SliceImpl<AdminNotificationResponse> adminNotiSlice = new SliceImpl<>(adminNotiList, pageRequest, true);

        given(notificationService.findAllNotificationApp(any()))
                .willReturn(adminNotiSlice);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/notifications")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(adminNotiSlice)))
                .andDo(document("notification/findAllAdminNotiApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("???????????? ????????????"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("???????????? ????????????"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ???"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("???????????? ??????"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("?????? ????????? ????????? ???"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????")
                        )));

        // then
        then(notificationService).should(times(1)).findAllNotificationApp(any());
    }

    @Test
    @DisplayName("???????????? ?????? ??????")
    void findAdminNotification() throws Exception {
        // given
        given(notificationService.findAdminNotification(any()))
                .willReturn(TEST_NOTIFICATION_RESPONSE1);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/notifications/{notificationId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_NOTIFICATION_RESPONSE1)))
                .andDo(document("notification/findAdminNoti",
                        pathParameters(
                                parameterWithName("notificationId").description("???????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("???????????? ????????????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("???????????? ????????????")
                                )));

        // then
        then(notificationService).should(times(1)).findAdminNotification(any());
    }

    @Test
    @DisplayName("????????? ?????????,?????? ?????? ????????? ??????")
    void findMyPushNotificationApp() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PushNotiResponse> pushNotiResponseList = new ArrayList<>();
        pushNotiResponseList.add(TEST_PUSH_NOTIFICATION_RESPONSE1); pushNotiResponseList.add(TEST_PUSH_NOTIFICATION_RESPONSE2);
        pushNotiResponseList.add(TEST_PUSH_NOTIFICATION_RESPONSE3); pushNotiResponseList.add(TEST_PUSH_NOTIFICATION_RESPONSE4);

        SliceImpl<PushNotiResponse> pushNotiResponseSlice = new SliceImpl<>(pushNotiResponseList, pageRequest, true);

        given(notificationService.findMyPushNotificationApp(any(), any()))
                .willReturn(pushNotiResponseSlice);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/members/notifications")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pushNotiResponseSlice)))
                .andDo(document("notification/findOtherNotiApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("?????????, ?????? ?????? ?????????"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("content.[].postId").type(JsonFieldType.NUMBER).description("?????? ????????? ?????????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("?????? ????????????"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ???"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("???????????? ??????"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("?????? ????????? ????????? ???"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????")
                        )));

        // then
        then(notificationService).should(times(1)).findMyPushNotificationApp(any(), any());
    }
}