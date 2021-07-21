package inu.graduation.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inu.graduation.sns.config.security.JwtTokenProvider;
import inu.graduation.sns.config.security.LoginMemberArgumentResolver;
import inu.graduation.sns.service.GoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@WebMvcTest(controllers = GoodController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class GoodControllerTest {

    @MockBean
    private GoodService goodService;

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
    @DisplayName("좋아요 하기")
    void saveGood() throws Exception {
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(goodService.saveGood(any(), any()))
                .willReturn(TEST_GOOD_COUTN_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/posts/{postId}/goods", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GOOD_COUTN_RESPONSE)))
                .andDo(document("good/create",
                        pathParameters(
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("좋아요 식별자"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("해당 게시글 좋아요 개수")
                        )));

        // then
        then(goodService).should(times(1)).saveGood(any(), any());
    }

    @Test
    @DisplayName("좋아요 취소")
    void cancleGood() throws Exception {
        // given
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(goodService.cancleGood(any(), any()))
                .willReturn(TEST_GOOD_COUTN_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/posts/goods/{goodId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, JWT_ACCESSTOKEN_TEST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GOOD_COUTN_RESPONSE)))
                .andDo(document("good/delete",
                        pathParameters(
                                parameterWithName("goodId").description("좋아요 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("취소된 좋아요 식별자"),
                                fieldWithPath("countGood").type(JsonFieldType.NUMBER).description("해당 게시글 좋아요 개수")
                        )));

        // then
        then(goodService).should(times(1)).cancleGood(any(), any());
    }
}