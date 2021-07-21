package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.service.CategoryService;
import inu.graduation.sns.service.CommentService;
import inu.graduation.sns.service.MemberService;
import inu.graduation.sns.service.PostService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static inu.graduation.sns.TestObject.*;
import static org.junit.jupiter.api.Assertions.*;
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
}