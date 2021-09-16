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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
    @DisplayName("공지사항 알림받기 여부 설정")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("adminNoti").type(JsonFieldType.BOOLEAN).description("공지사항 알림 설정 여부")
                        )));

        // then
        then(memberService).should(times(1)).updateNotification(any());
    }

    @Test
    @DisplayName("좋아요 알림받기 여부 설정")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("goodNoti").type(JsonFieldType.BOOLEAN).description("좋아요 알림 설정 여부")
                        )));

        // then
        then(memberService).should(times(1)).updateGoodNotification(any());
    }

    @Test
    @DisplayName("댓글 알림받기 여부 설정")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("commentNoti").type(JsonFieldType.BOOLEAN).description("댓글 알림 설정 여부")
                        )));

        // then
        then(memberService).should(times(1)).updateCommentNotification(any());
    }

    @Test
    @DisplayName("웹에서 공지사항 리스트 조회")
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
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("공지사항 식별자"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("공지사항 제목"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("공지사항 내용"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("공지사항 생성시간"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("공지사항 수정시간"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지의 데이터 수"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("총 데이터 개수"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 크기"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부")
                        )));

        // then
        then(notificationService).should(times(1)).findAllNotificationWeb(any());
    }

    @Test
    @DisplayName("앱에서 공지사항 리스트 조회")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("공지사항 식별자"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("공지사항 제목"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("공지사항 내용"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("공지사항 생성시간"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("공지사항 수정시간"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지의 데이터 수"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현제 페이지 데이터 수"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부")
                        )));

        // then
        then(notificationService).should(times(1)).findAllNotificationApp(any());
    }

    @Test
    @DisplayName("앱에서 좋아요,댓글 알림 리스트 조회")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("좋아요, 댓글 알림 식별자"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("알림 제목"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("알림 내용"),
                                fieldWithPath("content.[].postId").type(JsonFieldType.NUMBER).description("해당 게시글 식별자"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("알림 생성시간"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지의 데이터 수"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현제 페이지 데이터 수"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부")
                        )));

        // then
        then(notificationService).should(times(1)).findMyPushNotificationApp(any(), any());
    }
}