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
                .apply(documentationConfiguration(restDocumentationContextProvider)// mockMvc Rest Docs ??????
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint()) // ????????? ?????????
                        .withResponseDefaults(prettyPrint())) // ????????? ?????????
                .build();
    }

    @Test
    @DisplayName("????????? ???????????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ???????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("???????????? ??????")
                        )));

        // then
        then(categoryService).should(times(1)).createCategory(any());
    }

    @Test
    @DisplayName("????????? ???????????? ??????")
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
                                parameterWithName("categoryId").description("???????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ???????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("???????????? ??????")
                        )));

        // then
        then(categoryService).should(times(1)).updateCategory(any(), any());
    }

    @Test
    @DisplayName("????????? ???????????? ??????")
    void deleteCategory() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/categories/{categoryId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteCategory",
                        pathParameters(
                                parameterWithName("categoryId").description("???????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        )));

        // then
        then(categoryService).should(times(1)).deleteCategory(any());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void deleteMember() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/members/{memberId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteMember",
                        pathParameters(
                                parameterWithName("memberId").description("?????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        )));

        // then
        then(memberService).should(times(1)).adminDeleteMember(any());
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    void deletePost() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/posts/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deletePost",
                        pathParameters(
                                parameterWithName("postId").description("????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        )));

        // then
        then(postService).should(times(1)).adminDeletePost(any());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void deleteComment() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/comments/{commentId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteComment",
                        pathParameters(
                                parameterWithName("commentId").description("?????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        )));

        // then
        then(commentService).should(times(1)).adminDeleteComment(any());
    }

    @Test
    @DisplayName("????????? ?????? ??????(???????????????)")
    void findMemberByNickname() throws Exception {
        // given
        given(memberService.adminFindMember(any()))
                .willReturn(TEST_MEMBER_RESPONSE2);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/members")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .param("nickname", "??????")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_MEMBER_RESPONSE2)))
                .andDo(document("admin/findMemberByNickname",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("nickname").description("????????? ?????????")
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
        then(memberService).should(times(1)).adminFindMember(any());
    }

    @Test
    @DisplayName("????????? (???) ?????? ?????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("content.[].nickname").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("content.[].role").type(JsonFieldType.STRING).description("?????? ??????"),
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
        then(memberService).should(times(1)).findAllMember(any());
    }

    @Test
    @DisplayName("????????? ???????????? ?????? + ?????? ?????????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("???????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(notificationService).should(times(1)).sendAllMessage(any());
    }

    @Test
    @DisplayName("????????? ???????????? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        pathParameters(
                                parameterWithName("notificationId").description("???????????? ?????????")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("???????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("?????? ??????")
                        )));

        // then
        then(notificationService).should(times(1)).updateNotification(any(), any());
    }

    @Test
    @DisplayName("????????? ???????????? ??????")
    void deleteNotification() throws Exception {
        // given

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/notification/{notificationId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("admin/deleteNotification",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        pathParameters(
                                parameterWithName("notificationId").description("???????????? ?????????")
                        )));

        // then
        then(notificationService).should(times(1)).deleteNotification(any());
    }


}