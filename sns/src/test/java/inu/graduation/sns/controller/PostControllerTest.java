package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.model.post.response.PostResponse;
import inu.graduation.sns.model.post.response.PostSimpleResponse;
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
import org.springframework.data.domain.SliceImpl;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

        given(postService.createPost(any(), any(), any(), any()))
                .willReturn(TEST_POST_CREATE_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/posts/categories/{categoryId}", 1L)
                .file(request).file(TEST_IMAGE_FILE2).file(TEST_IMAGE_FILE3)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_POST_CREATE_RESPONSE)))
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
                                fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                fieldWithPath("countComment").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성시간"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("게시글 수정시간"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("작성자 식별자"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("imageDtoList.[].id").description(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("imageDtoList.[].imageUrl").description(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("imageDtoList.[].thumbnailImageUrl").description(JsonFieldType.STRING).description("썸네일 url"),
                                fieldWithPath("goodDto.id").description(JsonFieldType.NULL).description("좋아요 식별자"),
                                fieldWithPath("goodDto.isGood").description(JsonFieldType.BOOLEAN).description("해당 게시글 좋아요 여부")
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
                .willReturn(TEST_POST_UPDATE_RESPONSE);
        String body = objectMapper.writeValueAsString(TEST_POST_UPDATE_REQUEST);
        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/posts/{postId}/categories/{categoryId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION,JWT_ACCESSTOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_POST_UPDATE_RESPONSE)))
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
                                fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("첫번째 주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("좋야요 개수"),
                                fieldWithPath("countComment").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("게시글 수정 시간"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("작성자 식별자"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("imageDtoList.[].id").description(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("imageDtoList.[].imageUrl").description(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("imageDtoList.[].thumbnailImageUrl").description(JsonFieldType.STRING).description("썸네일 url")
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

        given(postService.findPostByAddress(any(), any(), any()))
                .willReturn(postResponsePage);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts")
                .param("firstAddress","인천")
                .param("secondAddress", "남동")
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findPostsWeb",
                        requestParameters(
                                parameterWithName("firstAddress").description("첫번째 주소"),
                                parameterWithName("secondAddress").description("두번째 주소"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("주소"),
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
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("좋아요 식별자").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("해당 게시글 좋아요 여부"),
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
    }

    @Test
    @DisplayName("앱 게시글 간단 조회")
    void findSimplePosts() throws Exception{
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostSimpleResponse> postSimpleResponseList = new ArrayList<>();
        postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE1); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE2); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE3);
        SliceImpl<PostSimpleResponse> result = new SliceImpl<>(postSimpleResponseList, pageRequest, true);

        given(postService.findSimplePostList(any(), any(), any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/posts")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .param("firstAddress","인천")
                .param("secondAddress", "남동")
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("post/findPostsApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("firstAddress").description("첫번째 주소"),
                                parameterWithName("secondAddress").description("두번째 주소"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("썸네일 url"),
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
        then(postService).should(times(1)).findSimplePostList(any(), any(), any());
    }

    @Test
    @DisplayName("웹에서 내가 쓴 글 조회")
    void findMyPostListWeb() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostResponse> postResponseList = new ArrayList<>();
        postResponseList.add(TEST_POST_RESPONSE); postResponseList.add(TEST_POST_RESPONSE2); postResponseList.add(TEST_POST_RESPONSE3);

        PageImpl<PostResponse> postResponsePage = new PageImpl<>(postResponseList, pageRequest, postResponseList.size());

        given(postService.findMyPostListWeb(any(), any()))
                .willReturn(postResponsePage);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/posts")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findByPostsWeb",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("주소"),
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
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("좋아요 식별자").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("해당 게시글 좋아요 여부"),
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
        then(postService).should(times(1)).findMyPostListWeb(any(), any());
    }

    @Test
    @DisplayName("웹에서 내가 쓴 글 조회(주소로)")
    void findMyPostListByAddressWeb() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostResponse> postResponseList = new ArrayList<>();
        postResponseList.add(TEST_POST_RESPONSE); postResponseList.add(TEST_POST_RESPONSE2); postResponseList.add(TEST_POST_RESPONSE3);

        PageImpl<PostResponse> postResponsePage = new PageImpl<>(postResponseList, pageRequest, postResponseList.size());

        given(postService.findMyPostListByAddressWeb(any(), any(), any(), any()))
                .willReturn(postResponsePage);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/posts/address")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .param("firstAddress","인천")
                        .param("secondAddress", "남동")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findMyPostsByAddressWeb",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("firstAddress").description("첫번째 주소"),
                                parameterWithName("secondAddress").description("두번째 주소"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("주소"),
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
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("좋아요 식별자").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("해당 게시글 좋아요 여부"),
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
        then(postService).should(times(1)).findMyPostListByAddressWeb(any(), any(), any(), any());
    }

    @Test
    @DisplayName("앱에서 내가 쓴 글 간단조회")
    void findMyPostListApp() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostSimpleResponse> postSimpleResponseList = new ArrayList<>();
        postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE1); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE2); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE3);
        SliceImpl<PostSimpleResponse> result = new SliceImpl<>(postSimpleResponseList, pageRequest, true);

        given(postService.findMyPostList(any(), any()))
                .willReturn(result);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/members/posts")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("post/findMyPostsApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("썸네일 url"),
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
        then(postService).should(times(1)).findMyPostList(any(), any());
    }

    @Test
    @DisplayName("앱에서 게시글 상세 조회")
    void findPostDetail() throws Exception{
        // given
        given(postService.findPost(any(), any()))
                .willReturn(TEST_POST_DETAIL_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/posts/{postId}",1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_POST_DETAIL_RESPONSE)))
                .andDo(document("post/findPostDetailApp",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("공개여부"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                fieldWithPath("countComment").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성시간"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("게시글 수정시간"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("작성자 식별자"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("imageDtoList.[].id").description(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("imageDtoList.[].imageUrl").description(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("imageDtoList.[].thumbnailImageUrl").description(JsonFieldType.STRING).description("썸네일 url"),
                                fieldWithPath("goodDto.id").description(JsonFieldType.NUMBER).description("좋아요 식별자"),
                                fieldWithPath("goodDto.isGood").description(JsonFieldType.BOOLEAN).description("해당 게시글 좋아요 여부")
                                )));

        // then
        then(postService).should(times(1)).findPost(any(), any());
    }

    @Test
    @DisplayName("웹에서 해시태그로 게시글 검색")
    void findPostsByHashtag() throws Exception{
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostResponse> postResponseList = new ArrayList<>();
        postResponseList.add(TEST_POST_RESPONSE4); postResponseList.add(TEST_POST_RESPONSE5); postResponseList.add(TEST_POST_RESPONSE6);

        PageImpl<PostResponse> postResponsePage = new PageImpl<>(postResponseList, pageRequest, postResponseList.size());

        given(postService.findPostsByHashtag(any(), any()))
                .willReturn(postResponsePage);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/hashtag")
                .param("page", "0")
                .param("size", "20")
                .param("hashtag", "해시")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findByHashtagWeb",
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수"),
                                parameterWithName("hashtag").description("검색 할 해시태그")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("주소"),
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
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("좋아요 식별자").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("해당 게시글 좋아요 여부"),
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
        then(postService).should(times(1)).findPostsByHashtag(any(), any());
    }

    @Test
    @DisplayName("앱에서 해시태그로 게시글 검색")
    void findPostsByHashtagApp() throws Exception{
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostSimpleResponse> postSimpleResponseList = new ArrayList<>();
        postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE4); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE5); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE6);
        SliceImpl<PostSimpleResponse> result = new SliceImpl<>(postSimpleResponseList, pageRequest, true);

        given(postService.findPostsByhashtagApp(any(), any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/posts/hashtag")
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .param("page", "0")
                .param("size", "20")
                .param("hashtag", "해시")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("post/findByHashtagApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수"),
                                parameterWithName("hashtag").description("검색 할 해시태그")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("썸네일 url"),
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
        then(postService).should(times(1)).findPostsByhashtagApp(any(), any());
    }

    @Test
    @DisplayName("앱에서 전체 글 간단조회")
    void findAllPostApp() throws Exception{
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostSimpleResponse> postSimpleResponseList = new ArrayList<>();
        postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE4); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE5); postSimpleResponseList.add(TEST_POST_SIMPLE_RESPONSE6);
        SliceImpl<PostSimpleResponse> result = new SliceImpl<>(postSimpleResponseList, pageRequest, true);

        given(postService.findAllPostsApp(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/posts/all")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("post/findAllPostApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("카테고리 식별자"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("이미지 식별자"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("이미지 url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("썸네일 url"),
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
        then(postService).should(times(1)).findAllPostsApp(any());
    }
}