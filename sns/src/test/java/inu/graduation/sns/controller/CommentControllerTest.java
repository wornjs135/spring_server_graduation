package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.model.comment.response.CommentResponse;
import inu.graduation.sns.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
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

import static inu.graduation.sns.TestObject.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class CommentControllerTest {

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
    @DisplayName("댓글 생성")
    void createComment() throws Exception {
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(commentService.saveComment(any(), any(), any()))
                .willReturn(TEST_COMMENT_RESPONSE);
        String body = objectMapper.writeValueAsString(TEST_COMMENT_SAVE_REQUEST);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/posts/{postId}/comments", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_COMMENT_RESPONSE)))
                .andDo(document("comment/create",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("작성자 식별자"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("작성자 닉네임")
                        )));

        // then
        then(commentService).should(times(1)).saveComment(any(), any(), any());
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(commentService.updateComment(any(), any(), any()))
                .willReturn(TEST_COMMENT_RESPONSE);
        String body = objectMapper.writeValueAsString(TEST_COMMENT_UPDATE_REQUEST);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/comments/{commentId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_COMMENT_RESPONSE)))
                .andDo(document("comment/update",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("작성자 식별자"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("작성자 닉네임")
                        )));

        // then
        then(commentService).should(times(1)).updateComment(any(), any(), any());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/comments/{commentId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("comment/delete",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        )));

        // then
        then(commentService).should(times(1)).deleteComment(any(), any());
    }

    @Test
    @DisplayName("웹에서 게시글의 댓글 조회")
    void findCommentsByPostId() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<CommentResponse> comments = new ArrayList<>();
        comments.add(TEST_COMMENT_RESPONSE); comments.add(TEST_COMMENT_RESPONSE2); comments.add(TEST_COMMENT_RESPONSE3);
        comments.add(TEST_COMMENT_RESPONSE4); comments.add(TEST_COMMENT_RESPONSE5);
        PageImpl<CommentResponse> commentResponsePage = new PageImpl<>(comments, pageRequest, comments.size());

        given(commentService.findCommentsByPost(any(), any()))
                .willReturn(commentResponsePage);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}/comments", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentResponsePage)))
                .andDo(document("comment/findCommentsWeb",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("댓글내용"),
                                fieldWithPath("content.[].memberDto.id").type(JsonFieldType.NUMBER).description("댓글 작성자 식별자"),
                                fieldWithPath("content.[].memberDto.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
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
        then(commentService).should(times(1)).findCommentsByPost(any(), any());
    }

    @Test
    @DisplayName("앱에서 게시글의 댓글 조회")
    void findCommentsByPostIdApp() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<CommentResponse> comments = new ArrayList<>();
        comments.add(TEST_COMMENT_RESPONSE); comments.add(TEST_COMMENT_RESPONSE2); comments.add(TEST_COMMENT_RESPONSE3);
        comments.add(TEST_COMMENT_RESPONSE4); comments.add(TEST_COMMENT_RESPONSE5);
        SliceImpl<CommentResponse> commentResponseSlice = new SliceImpl<>(comments, pageRequest, true);

        given(commentService.findCommentsByPostId(any(), any()))
                .willReturn(commentResponseSlice);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/posts/{postId}/comments", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentResponseSlice)))
                .andDo(document("comment/findCommentsApp",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("댓글내용"),
                                fieldWithPath("content.[].memberDto.id").type(JsonFieldType.NUMBER).description("댓글 작성자 식별자"),
                                fieldWithPath("content.[].memberDto.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
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
        then(commentService).should(times(1)).findCommentsByPostId(any(), any());
    }
}