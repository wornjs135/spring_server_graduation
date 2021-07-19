package inu.graduation.sns;

import inu.graduation.sns.model.Image.dto.ImageDto;
import inu.graduation.sns.model.category.dto.CategoryDto;
import inu.graduation.sns.model.category.response.CategoryResponse;
import inu.graduation.sns.model.member.dto.MemberDto;
import inu.graduation.sns.model.member.response.MemberResponse;
import inu.graduation.sns.model.post.request.PostSaveRequest;
import inu.graduation.sns.model.post.request.PostUpdateRequest;
import inu.graduation.sns.model.post.response.PostDetailResponse;
import inu.graduation.sns.model.post.response.PostResponse;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestObject {

    public final static String JWT_ACCESSTOKEN_TEST = "Bearer access토큰";
    public final static String JWT_REFRESHTOKEN_TEST = "Bearer refresh토큰";

    public static final MemberResponse TEST_MEMBER_RESPONSE
            = new MemberResponse(1L,"이메일", "황주환", "이미지", "썸네일");

    public static final MemberResponse TEST_MEMBER_RESPONSE_UPDATE_PROFILEIMG
            = new MemberResponse(1L,"이메일", "황주환", "수정된이미지", "수정된썸네일");

    public static final MemberDto TEST_MEMBER_DTO
            = new MemberDto(1L, "닉네임");

    public static final MockMultipartFile TEST_IMAGE_FILE1 = new MockMultipartFile(
            "image",
            "프로필사진.png",
            MediaType.IMAGE_PNG_VALUE,
            "<<image>>".getBytes(StandardCharsets.UTF_8));
    public static final MockMultipartFile TEST_IMAGE_FILE2 = new MockMultipartFile(
            "image",
            "게시글사진1.png",
            MediaType.IMAGE_PNG_VALUE,
            "<<image>>".getBytes(StandardCharsets.UTF_8));
    public static final MockMultipartFile TEST_IMAGE_FILE3 = new MockMultipartFile(
            "image",
            "게시글사진2.jpeg",
            MediaType.IMAGE_JPEG_VALUE,
            "<<image>>".getBytes(StandardCharsets.UTF_8));

    public static final PostSaveRequest TEST_POST_SVAE_REQUEST
            = new PostSaveRequest("게시글내용", "인천광역시", "남동구", "나머지주소", 7, true);
    public static final PostUpdateRequest TEST_POST_UPDATE_REQUEST
            = new PostUpdateRequest("게시글내용", "인천광역시", "남동구", "나머지주소", 7, true);

    public static final CategoryDto TEST_CATEGORY_DTO
            = new CategoryDto(1L, "카테고리1");

    public static final CategoryResponse TEST_CATEGORY_RESPONSE1
            = new CategoryResponse(1L, "관광지");
    public static final CategoryResponse TEST_CATEGORY_RESPONSE2
            = new CategoryResponse(2L, "숙소");
    public static final CategoryResponse TEST_CATEGORY_RESPONSE3
            = new CategoryResponse(3L, "맛집");

    public static final ImageDto TEST_IMAGE_DTO1
            = new ImageDto(1L, "이미지url", "썸네일url");
    public static final ImageDto TEST_IMAGE_DTO2
            = new ImageDto(2L, "이미지url", "썸네일url");
    public static final ImageDto TEST_IMAGE_DTO3
            = new ImageDto(3L, "이미지url", "썸네일url");
    public static final List<ImageDto> TEST_IMAGE_DTO_LIST
            = new ArrayList<>(){
        {
            add(TEST_IMAGE_DTO1);
            add(TEST_IMAGE_DTO2);
            add(TEST_IMAGE_DTO3);
        }
    };

    public static final PostResponse TEST_POST_RESPONSE
            = new PostResponse(1L, "게시글내용", "인천광역시", "남동구", "나머지주소", 7,
            true, 13, 3, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostResponse TEST_POST_RESPONSE2
            = new PostResponse(2L, "게시글내용", "인천광역시", "남동구", "나머지주소", 8,
            true, 53, 3, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostResponse TEST_POST_RESPONSE3
            = new PostResponse(3L, "게시글내용", "인천광역시", "남동구", "나머지주소", 5,
            true, 23, 13, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);

    public static final PostDetailResponse TEST_POST_DETAIL_RESPONSE
            = new PostDetailResponse(1L, "게시글내용", "인천광역시", "남동구", "나머지주소", 7,
            true, 13, 3, LocalDateTime.now(), LocalDateTime.now(), TEST_MEMBER_DTO, TEST_CATEGORY_DTO);
}
