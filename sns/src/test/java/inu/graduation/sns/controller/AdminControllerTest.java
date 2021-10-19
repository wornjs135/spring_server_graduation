package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.model.member.response.FindAllMemberResponse;
import inu.graduation.sns.model.member.response.MemberResponse;
import inu.graduation.sns.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static inu.graduation.sns.TestObject.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class AdminControllerTest {
    @MockBean
    private CategoryService categoryService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private NotificationService notificationService;

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
    @DisplayName("관리자 카테고리 생성")
    void createCategory() throws Exception {
        // given
        given(categoryService.createCategory(any()))
                .willReturn(TEST_CATEGORY_RESPONSE4);

        String body = objectMapper.writeValueAsString(TEST_CATEGORY_SAVE_REQUEST);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/admin/categories")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_CATEGORY_RESPONSE4)))
                .andDo(document("admin/createCateogry",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("생성할 카테고리 이름")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름")
                        )));

        // then
        then(categoryService).should(times(1)).createCategory(any());
    }

    @Test
    @DisplayName("관리자 카테고리 수정")
    void updateCategory() throws Exception {
        // given
        given(categoryService.updateCategory(any(), any()))
                .willReturn(TEST_CATEGORY_RESPONSE5);
        String body = objectMapper.writeValueAsString(TEST_CATEGORY_UPDATE_REQUEST);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/admin/categories/{categoryId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_CATEGORY_RESPONSE5)))
                .andDo(document("admin/updateCategory",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("수정할 카테고리 이름")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름")
                        )));

        // then
        then(categoryService).should(times(1)).updateCategory(any(), any());
    }

    @Test
    @DisplayName("관리자 카테고리 삭제")
    void deleteCategory() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/categories/{categoryId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteCategory",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        )));

        // then
        then(categoryService).should(times(1)).deleteCategory(any());
    }

    @Test
    @DisplayName("관리자 회원 강퇴")
    void deleteMember() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/members/{memberId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteMember",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        )));

        // then
        then(memberService).should(times(1)).adminDeleteMember(any());
    }

    @Test
    @DisplayName("관리자 게시글 삭제")
    void deletePost() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/posts/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deletePost",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        )));

        // then
        then(postService).should(times(1)).adminDeletePost(any());
    }

    @Test
    @DisplayName("관리자 댓글 삭제")
    void deleteComment() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/comments/{commentId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteComment",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        )));

        // then
        then(commentService).should(times(1)).adminDeleteComment(any());
    }

    @Test
    @DisplayName("관리자 회원 조회(닉네임으로)")
    void findMemberByNickname() throws Exception {
        // given
        given(memberService.adminFindMember(any()))
                .willReturn(TEST_MEMBER_RESPONSE2);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/members")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .param("nickname", "닉넴")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE2)))
                .andDo(document("admin/findMemberByNickname",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("nickname").description("조회할 닉네임")
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
        then(memberService).should(times(1)).adminFindMember(any());
    }

    @Test
    @DisplayName("관리자 (웹) 회원 전체 조회")
    void findAllMember() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<FindAllMemberResponse> allMember = new ArrayList<>();
        allMember.add(TEST_ALL_MEMBER_RESPONSE); allMember.add(TEST_ALL_MEMBER_RESPONSE2); allMember.add(TEST_ALL_MEMBER_RESPONSE3);

        PageImpl<FindAllMemberResponse> allMemberPage = new PageImpl<>(allMember, pageRequest, allMember.size());

        given(memberService.findAllMember(any()))
                .willReturn(allMemberPage);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/members/all")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(allMemberPage)))
                .andDo(document("admin/findAllMemberWeb",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("content.[].nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("content.[].role").type(JsonFieldType.STRING).description("회원 권한"),
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
        then(memberService).should(times(1)).findAllMember(any());
    }

    @Test
    @DisplayName("관리자 공지사항 등록 + 알림 보내기")
    void sendNotification() throws Exception {
        // given
        given(notificationService.sendAllMessage(any()))
                .willReturn(TEST_NOTIFICATION_RESPONSE1);
        String body = objectMapper.writeValueAsString(TEST_NOTIFICATION_CREATE_REQUEST);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/admin/notification")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_NOTIFICATION_RESPONSE1)))
                .andDo(document("admin/createNotification",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("공지사항 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("공지사항 내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("공지사항 식별자"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("공지사항 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("공지사항 내용"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                        )));

        // then
        then(notificationService).should(times(1)).sendAllMessage(any());
    }

    @Test
    @DisplayName("관리자 공지사항 수정")
    void updateNotification() throws Exception {
        // given
        given(notificationService.updateNotification(any(), any()))
                .willReturn(TEST_NOTIFICATION_RESPONSE2);
        String body = objectMapper.writeValueAsString(TEST_NOTIFICATION_CREATE_REQUEST2);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/admin/notification/{notificationId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_NOTIFICATION_RESPONSE2)))
                .andDo(document("admin/updateNotification",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("notificationId").description("공지사항 식별자")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("공지사항 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("공지사항 내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("공지사항 식별자"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("공지사항 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("공지사항 내용"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                        )));

        // then
        then(notificationService).should(times(1)).updateNotification(any(), any());
    }

    @Test
    @DisplayName("관리자 공지사항 삭제")
    void deleteNotification() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/notification/{notificationId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteNotification",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("notificationId").description("공지사항 식별자")
                        )));

        // then
        then(notificationService).should(times(1)).deleteNotification(any());
    }


}