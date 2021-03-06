package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.model.post.response.PostAllSimpleResponse;
import inu.graduation.sns.model.post.response.PostResponse;
import inu.graduation.sns.model.post.response.PostSimpleGoodResponse;
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
                .apply(documentationConfiguration(restDocumentationContextProvider)// mockMvc Rest Docs ??????
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint()) // ????????? ?????????
                        .withResponseDefaults(prettyPrint())) // ????????? ?????????
                .build();
    }

    @Test
    @DisplayName("????????? ??????")
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
                                parameterWithName("categoryId").description("???????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access ??????")
                        ),
                        requestParts(
                                partWithName("request").description("????????? ?????? ?????????"),
                                partWithName("image").description("???????????? ????????? ??????").optional()
                        ),
                        requestPartFields("request",
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("countComment").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("imageDtoList.[].id").description(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("imageDtoList.[].imageUrl").description(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("imageDtoList.[].thumbnailImageUrl").description(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("goodDto.id").description(JsonFieldType.NULL).description("????????? ?????????"),
                                fieldWithPath("goodDto.isGood").description(JsonFieldType.BOOLEAN).description("?????? ????????? ????????? ??????")
                        )));

        // then
        then(postService).should(times(1)).createPost(any(), any(), any(), any());
    }

    @Test
    @DisplayName("????????? ??????(????????? ??????)")
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
                                parameterWithName("postId").description("????????? ?????????"),
                                parameterWithName("categoryId").description("???????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("countComment").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("imageDtoList.[].id").description(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("imageDtoList.[].imageUrl").description(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("imageDtoList.[].thumbnailImageUrl").description(JsonFieldType.STRING).description("????????? url")
                        )));

        // then
        then(postService).should(times(1)).updatePost(any(), any(), any(), any());
    }

    @Test
    @DisplayName("????????? ????????? ??????")
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
                                parameterWithName("postId").description("????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParts(
                                partWithName("image").description("????????? ????????? ??????")
                        )));

        // then
        then(postService).should(times(1)).updatePostImage(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ??????")
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
                                parameterWithName("postId").description("????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        )));

        // then
        then(postService).should(times(1)).deletePost(any(), any());
    }

    @Test
    @DisplayName("????????? ????????? ??????")
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
                .param("firstAddress","??????")
                .param("secondAddress", "??????")
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findPostsWeb",
                        requestParameters(
                                parameterWithName("firstAddress").description("????????? ??????"),
                                parameterWithName("secondAddress").description("????????? ??????"),
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content.[].score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("content.[].isOpen").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("content.[].countGood").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("content.[].countComment").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].memberDto.id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].memberDto.nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("????????? ?????????").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("?????? ????????? ????????? ??????"),
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
    }

    @Test
    @DisplayName("??? ????????? ?????? ??????")
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
                .param("firstAddress","??????")
                .param("secondAddress", "??????")
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("post/findPostsApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("firstAddress").description("????????? ??????"),
                                parameterWithName("secondAddress").description("????????? ??????"),
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
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
        then(postService).should(times(1)).findSimplePostList(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??? ??? ??????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content.[].score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("content.[].isOpen").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("content.[].countGood").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("content.[].countComment").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].memberDto.id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].memberDto.nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("????????? ?????????").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("?????? ????????? ????????? ??????"),
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
        then(postService).should(times(1)).findMyPostListWeb(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??? ??? ??????(?????????)")
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
                        .param("firstAddress","??????")
                        .param("secondAddress", "??????")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findMyPostsByAddressWeb",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("firstAddress").description("????????? ??????"),
                                parameterWithName("secondAddress").description("????????? ??????"),
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content.[].score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("content.[].isOpen").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("content.[].countGood").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("content.[].countComment").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].memberDto.id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].memberDto.nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("????????? ?????????").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("?????? ????????? ????????? ??????"),
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
        then(postService).should(times(1)).findMyPostListByAddressWeb(any(), any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??? ??? ????????????")
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
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
        then(postService).should(times(1)).findMyPostList(any(), any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? ??????")
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
                                parameterWithName("postId").description("????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("isOpen").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("countComment").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("memberDto.id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("memberDto.nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("imageDtoList.[].id").description(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("imageDtoList.[].imageUrl").description(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("imageDtoList.[].thumbnailImageUrl").description(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("goodDto.id").description(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("goodDto.isGood").description(JsonFieldType.BOOLEAN).description("?????? ????????? ????????? ??????")
                                )));

        // then
        then(postService).should(times(1)).findPost(any(), any());
    }

    @Test
    @DisplayName("????????? ??????????????? ????????? ??????")
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
                .param("hashtag", "??????")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postResponsePage)))
                .andDo(document("post/findByHashtagWeb",
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????"),
                                parameterWithName("hashtag").description("?????? ??? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content.[].score").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("content.[].isOpen").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("content.[].countGood").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("content.[].countComment").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("content.[].memberDto.id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].memberDto.nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].goodDto.id").type(JsonFieldType.VARIES).description("????????? ?????????").optional(),
                                fieldWithPath("content.[].goodDto.isGood").type(JsonFieldType.BOOLEAN).description("?????? ????????? ????????? ??????"),
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
        then(postService).should(times(1)).findPostsByHashtag(any(), any());
    }

    @Test
    @DisplayName("????????? ??????????????? ????????? ??????")
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
                .param("hashtag", "??????")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("post/findByHashtagApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????"),
                                parameterWithName("hashtag").description("?????? ??? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
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
        then(postService).should(times(1)).findPostsByhashtagApp(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??? ????????????")
    void findAllPostApp() throws Exception{
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostAllSimpleResponse> postAllSimpleResponseList = new ArrayList<>();
        postAllSimpleResponseList.add(TEST_POST_ALL_SIMPLE_RESPONSE1); postAllSimpleResponseList.add(TEST_POST_ALL_SIMPLE_RESPONSE2);
        SliceImpl<PostAllSimpleResponse> result = new SliceImpl<>(postAllSimpleResponseList, pageRequest, true);

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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].address").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
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
        then(postService).should(times(1)).findAllPostsApp(any());
    }

    @Test
    @DisplayName("????????? ????????? ??? ????????? ????????????")
    void findPostOrderByGood() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<PostSimpleGoodResponse> postSimpleGoodResponseList = new ArrayList<>();
        postSimpleGoodResponseList.add(TEST_POST_SIMPLE_GOOD_RESPONSE1); postSimpleGoodResponseList.add(TEST_POST_SIMPLE_GOOD_RESPONSE2);
        SliceImpl<PostSimpleGoodResponse> result = new SliceImpl<>(postSimpleGoodResponseList, pageRequest, true);

        given(postService.findPostOrderByGoodApp(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/m/posts/goods")
                        .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("post/findPostOrderByGoodApp",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearea Access ??????")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content.[].countGood").type(JsonFieldType.NUMBER).description("????????? ????????? ???"),
                                fieldWithPath("content.[].categoryDto.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("content.[].categoryDto.name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("content.[].imageDtoList.[].id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content.[].imageDtoList.[].imageUrl").type(JsonFieldType.STRING).description("????????? url"),
                                fieldWithPath("content.[].imageDtoList.[].thumbnailImageUrl").type(JsonFieldType.STRING).description("????????? url"),
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
        then(postService).should(times(1)).findPostOrderByGoodApp(any());
    }
}