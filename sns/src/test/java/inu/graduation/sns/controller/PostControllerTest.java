package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.model.post.response.PostResponse;
import inu.graduation.sns.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcBuilderCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

import static inu.graduation.sns.TestObject.*;

@WebMvcTest(controllers = PostController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class PostControllerTest {

    @MockBean
    private PostService postService;

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
    @DisplayName("게시글 생성")
    void createPostWithImages() throws Exception{
        // given
        MockMultipartFile request = new MockMultipartFile("request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(TEST_POST_SVAE_REQUEST).getBytes(StandardCharsets.UTF_8));

        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/posts/categories/{categoryId}", 1L)
                .file(request).file(TEST_IMAGE_FILE2).file(TEST_IMAGE_FILE3)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andDo(document("post/create",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        requestParts(
                                partWithName("request").description("게시글 생성 데이터"),
                                partWithName("image").description("게시글의 이미지 파일").optional()
                        ),
                        requestPartFields("request",
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("firstAddress").type(JsonFieldType.STRING).description("첫번째 주소"),
                                fieldWithPath("secondAddress").type(JsonFieldType.STRING).description("두번째 주소"),
                                fieldWithPath("restAddress").type(JsonFieldType.STRING).description("나머지 주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부")
                        )));

        // then
        then(postService).should(times(1)).createPost(any(), any(), any(), any());
    }

    @Test
    @DisplayName("게시글 수정(이미지 제외)")
    void updatePost() throws Exception{
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(postService.updatePost(any(), any(), any(), any()))
                .willReturn(TEST_POST_DETAIL_RESPONSE);
        String body = objectMapper.writeValueAsString(TEST_POST_UPDATE_REQUEST);
        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/posts/{postId}/categories/{categoryId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION,JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_POST_DETAIL_RESPONSE)))
                .andDo(document("post/updatePost",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자"),
                                parameterWithName("categoryId").description("카테고리 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("firstAddress").type(JsonFieldType.STRING).description("첫번째 주소"),
                                fieldWithPath("secondAddress").type(JsonFieldType.STRING).description("두번째 주소"),
                                fieldWithPath("restAddress").type(JsonFieldType.STRING).description("나머지 주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("firstAddress").type(JsonFieldType.STRING).description("첫번째 주소"),
                                fieldWithPath("secondAddress").type(JsonFieldType.STRING).description("두번째 주소"),
                                fieldWithPath("restAddress").type(JsonFieldType.STRING).description("나머지 주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("좋야요 개수"),
                                fieldWithPath("countComment").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("게시글 수정 시간"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("작성자 식별자"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름")
                        )));

        // then
        then(postService).should(times(1)).updatePost(any(), any(), any(), any());
    }

    @Test
    @DisplayName("게시글 이미지 수정")
    void updatePostImage() throws Exception{
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/posts/{postId}/images", 1L);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });

        MockMultipartHttpServletRequestBuilder builder1 = (MockMultipartHttpServletRequestBuilder) builder.requestAttr(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/posts/{postId}/images");

        // when
        mockMvc.perform(builder1
                .file(TEST_IMAGE_FILE2).file(TEST_IMAGE_FILE3)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andDo(document("post/updatePostImage",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParts(
                                partWithName("image").description("수정할 이미지 파일")
                        )));

        // then
        then(postService).should(times(1)).updatePostImage(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() throws Exception{
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/posts/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST))
                .andExpect(status().isOk())
                .andDo(document("post/delete",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        )));

        // then
        then(postService).should(times(1)).deletePost(any(), any());
    }

    @Test
    @DisplayName("웹에서 게시글 조회")
    void findPostByAddress() throws Exception{
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostResponse> postResponseList = new ArrayList<>();
        postResponseList.add(TEST_POST_RESPONSE); postResponseList.add(TEST_POST_RESPONSE2); postResponseList.add(TEST_POST_RESPONSE3);

        PageImpl<PostResponse> postResponsePage = new PageImpl<>(postResponseList, pageRequest, postResponseList.size());

        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(postService.findPostByAddress(any(), any(), any()))
                .willReturn(postResponsePage);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .param("firstAddress","인천")
                .param("secondAddress", "남동")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findPostsWeb",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("firstAddress").description("첫번째 주소"),
                                parameterWithName("secondAddress").description("두번째 주소")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].firstAddress").type(JsonFieldType.STRING).description("첫번째주소"),
                                fieldWithPath("content.[].secondAddress").type(JsonFieldType.STRING).description("두번째주소"),
                                fieldWithPath("content.[].restAddress").type(JsonFieldType.STRING).description("나머지주소"),
                                fieldWithPath("content.[].score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("content.[].isOpen").type(JsonFieldType.BOOLEAN).description("공개여부"),
                                fieldWithPath("content.[].countGood").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                fieldWithPath("content.[].countComment").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("게시글 생성시간"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("게시글 수정시간"),
                                fieldWithPath("content.[].memberDto.id").type(JsonFieldType.NUMBER).description("작성자 식별자"),
                                fieldWithPath("content.[].memberDto.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("썸네일 url"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description(""),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description(""),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description(""),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("총 데이터 개수"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description(""),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("검색 데이터 개수"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description(""),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description(""),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description(""),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description(""),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description(""),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("")
                        )));
    }
}