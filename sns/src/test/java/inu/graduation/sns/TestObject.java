package inu.graduation.sns;

import inu.graduation.sns.domain.Role;
import inu.graduation.sns.model.Image.dto.ImageDto;
import inu.graduation.sns.model.category.dto.CategoryDto;
import inu.graduation.sns.model.category.request.CategorySaveRequest;
import inu.graduation.sns.model.category.request.CategoryUpdateRequest;
import inu.graduation.sns.model.category.response.CategoryResponse;
import inu.graduation.sns.model.comment.request.CommentSaveRequest;
import inu.graduation.sns.model.comment.request.CommentUpdateRequest;
import inu.graduation.sns.model.comment.response.CommentResponse;
import inu.graduation.sns.model.common.CreateToken;
import inu.graduation.sns.model.good.dto.GoodDto;
import inu.graduation.sns.model.good.response.GoodCountResponse;
import inu.graduation.sns.model.member.dto.MemberDto;
import inu.graduation.sns.model.member.response.FindAllMemberResponse;
import inu.graduation.sns.model.member.response.LoginResponse;
import inu.graduation.sns.model.member.response.MemberNotificationResponse;
import inu.graduation.sns.model.member.response.MemberResponse;
import inu.graduation.sns.model.notification.request.CreateNotificationRequest;
import inu.graduation.sns.model.notification.response.IsAdminNotiResponse;
import inu.graduation.sns.model.notification.response.IsCommentNotiResponse;
import inu.graduation.sns.model.notification.response.IsGoodNotiResponse;
import inu.graduation.sns.model.notification.response.AdminNotificationResponse;
import inu.graduation.sns.model.post.request.PostSaveRequest;
import inu.graduation.sns.model.post.request.PostUpdateRequest;
import inu.graduation.sns.model.post.response.*;
import inu.graduation.sns.model.pushnoti.PushNotiResponse;
import inu.graduation.sns.model.role.dto.RoleDto;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestObject {

    public final static String JWT_ACCESSTOKEN_TEST = "Bearer access토큰";
    public final static String JWT_REFRESHTOKEN_TEST = "Bearer refresh토큰";

    public static final LoginResponse TEST_MEMBER_LOGIN_RESPONSE
            = new LoginResponse(CreateToken.from(JWT_ACCESSTOKEN_TEST, JWT_REFRESHTOKEN_TEST), Role.ROLE_MEMBER);

    public static final RoleDto TEST_ROLE_DTO
            = new RoleDto(Role.ROLE_MEMBER);

    public static final MemberResponse TEST_MEMBER_RESPONSE
            = new MemberResponse(1L, "황주환", "이미지url", "썸네일url", "배경이미지url", "ROLE_MEMBER");
    public static final MemberResponse TEST_MEMBER_RESPONSE2
            = new MemberResponse(1L, "닉넴", "이미지url", "썸네일url", "배경이미지url", "ROLE_MEMBER");
    public static final FindAllMemberResponse TEST_ALL_MEMBER_RESPONSE
            = new FindAllMemberResponse(1L, "김영만", "ROLE_MEMBER");
    public static final FindAllMemberResponse TEST_ALL_MEMBER_RESPONSE2
            = new FindAllMemberResponse(2L, "강서노", "ROLE_MEMBER");
    public static final FindAllMemberResponse TEST_ALL_MEMBER_RESPONSE3
            = new FindAllMemberResponse(3L, "박재권", "ROLE_ADMIN");

    public static final MemberResponse TEST_MEMBER_RESPONSE_UPDATE_PROFILEIMG
            = new MemberResponse(1L, "황주환", "수정된이미지url", "수정된썸네일url", "배경이미지url", "ROLE_MEMBER");
    public static final MemberResponse TEST_MEMBER_RESPONSE_DEFAULT_PROFILEIMG
            = new MemberResponse(1L, "황주환", "기본이미지url", "기본썸네일url", "배경이미지url", "ROLE_MEMBER");

    public static final MemberDto TEST_MEMBER_DTO
            = new MemberDto(1L, "닉네임");
    public static final MemberDto TEST_MEMBER_DTO2
            = new MemberDto(2L, "김영만");
    public static final MemberDto TEST_MEMBER_DTO3
            = new MemberDto(3L, "김우빈");

    public static final MemberNotificationResponse TEST_MEMBER_NOTIFICATION_RESPONSE1
            = new MemberNotificationResponse(true, false, true);

    public static final MockMultipartFile TEST_IMAGE_FILE1 = new MockMultipartFile(
            "image",
            "프로필사진.png",
            MediaType.IMAGE_PNG_VALUE,
            "<<image>>".getBytes(StandardCharsets.UTF_8));
    public static final MockMultipartFile TEST_IMAGE_FILE4 = new MockMultipartFile(
            "image",
            "배경사진.png",
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
            = new PostSaveRequest("게시글내용", "인천광역시 남동구 어쩌고저쩌고", 7, true);
    public static final PostUpdateRequest TEST_POST_UPDATE_REQUEST
            = new PostUpdateRequest("게시글내용", "인천광역시 남동구 어쩌고저쩌고", 7, true);

    public static final CategoryDto TEST_CATEGORY_DTO
            = new CategoryDto(1L, "카테고리1");
    public static final CategorySaveRequest TEST_CATEGORY_SAVE_REQUEST
            = new CategorySaveRequest("새로운 카테고리");
    public static final CategoryUpdateRequest TEST_CATEGORY_UPDATE_REQUEST
            = new CategoryUpdateRequest("수정할 카테고리");

    public static final CategoryResponse TEST_CATEGORY_RESPONSE1
            = new CategoryResponse(1L, "관광지");
    public static final CategoryResponse TEST_CATEGORY_RESPONSE2
            = new CategoryResponse(2L, "숙소");
    public static final CategoryResponse TEST_CATEGORY_RESPONSE3
            = new CategoryResponse(3L, "맛집");
    public static final CategoryResponse TEST_CATEGORY_RESPONSE4
            = new CategoryResponse(4L, "생성된 카테고리");
    public static final CategoryResponse TEST_CATEGORY_RESPONSE5
            = new CategoryResponse(5L, "수정된 카테고리");

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

    public static final GoodDto TEST_GOOD_DTO_FALSE
            = new GoodDto(null, false);
    public static final GoodDto TEST_GOOD_DTO_TRUE
            = new GoodDto(1L, true);

    public static final PostCreateResponse TEST_POST_CREATE_RESPONSE
            = new PostCreateResponse(1L, "게시글내용", "인천광역시 남동구 어쩌고저쩌고",  7,
            true, 0, 0, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_FALSE);
    public static final PostUpdateResponse TEST_POST_UPDATE_RESPONSE
            = new PostUpdateResponse(1L, "게시글내용", "인천광역시 남동구 어쩌고 저쩌고", 7,
            true, 13, 3, LocalDateTime.now(), LocalDateTime.now(), TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostResponse TEST_POST_RESPONSE
            = new PostResponse(1L, "게시글내용", "인천광역시 남동구 어쩌고저쩌고",  7,
            true, 13, 3, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_FALSE);
    public static final PostResponse TEST_POST_RESPONSE2
            = new PostResponse(2L, "게시글내용", "인천광역시 남동구 어쩌고저쩌고",  8,
            true, 53, 3, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_TRUE);
    public static final PostResponse TEST_POST_RESPONSE3
            = new PostResponse(3L, "게시글내용 #해쉬태그", "인천광역시 남동구 어쩌고저쩌고", 5,
            true, 23, 13, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_FALSE);
    public static final PostResponse TEST_POST_RESPONSE4
            = new PostResponse(4L, "게시글내용2 #해시", "인천광역시 남동구 어쩌고저쩌고",  7,
            true, 13, 3, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_TRUE);
    public static final PostResponse TEST_POST_RESPONSE5
            = new PostResponse(5L, "게시글내용 #해시", "인천광역시 남동구 어쩌고저쩌고",  8,
            true, 53, 3, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_FALSE);
    public static final PostResponse TEST_POST_RESPONSE6
            = new PostResponse(6L, "게시글내용 #해시", "인천광역시 남동구 어쩌고 저쩌고", 5,
            true, 23, 13, LocalDateTime.now(), LocalDateTime.now(),TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_TRUE);

    public static final PostDetailResponse TEST_POST_DETAIL_RESPONSE
            = new PostDetailResponse(1L, "게시글내용", "인천광역시 남동구 어쩌고 저쩌고", 7,
            true, 13, 3, LocalDateTime.now(), LocalDateTime.now(), TEST_MEMBER_DTO, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST, TEST_GOOD_DTO_TRUE);

    public static final PostSimpleResponse TEST_POST_SIMPLE_RESPONSE1
            = new PostSimpleResponse(1L, "게시글내용", TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostSimpleResponse TEST_POST_SIMPLE_RESPONSE2
            = new PostSimpleResponse(2L, "게시글내용",TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostSimpleResponse TEST_POST_SIMPLE_RESPONSE3
            = new PostSimpleResponse(3L, "게시글내용 #해쉬태그", TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostSimpleResponse TEST_POST_SIMPLE_RESPONSE4
            = new PostSimpleResponse(4L, "게시글내용 #해시", TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostSimpleResponse TEST_POST_SIMPLE_RESPONSE5
            = new PostSimpleResponse(5L, "게시글내용 #해시", TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostSimpleResponse TEST_POST_SIMPLE_RESPONSE6
            = new PostSimpleResponse(6L, "게시글내용 #해시", TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostAllSimpleResponse TEST_POST_ALL_SIMPLE_RESPONSE1
            = new PostAllSimpleResponse(1L, "게시글내용1", "인천광역시 남동구 어쩌구", TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostAllSimpleResponse TEST_POST_ALL_SIMPLE_RESPONSE2
            = new PostAllSimpleResponse(2L, "게시글내용2", "경상북도 예천군 어쩌구저쩌구", TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostSimpleGoodResponse TEST_POST_SIMPLE_GOOD_RESPONSE1
            = new PostSimpleGoodResponse(1L, "게시글내용1", 223, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);
    public static final PostSimpleGoodResponse TEST_POST_SIMPLE_GOOD_RESPONSE2
            = new PostSimpleGoodResponse(2L, "게시글내용2", 30, TEST_CATEGORY_DTO, TEST_IMAGE_DTO_LIST);

    public static final CommentSaveRequest TEST_COMMENT_SAVE_REQUEST
            = new CommentSaveRequest("댓글내용");
    public static final CommentUpdateRequest TEST_COMMENT_UPDATE_REQUEST
            = new CommentUpdateRequest("댓글내용");
    public static final CommentResponse TEST_COMMENT_RESPONSE
            = new CommentResponse(1L, "댓글내용", TEST_MEMBER_DTO);
    public static final CommentResponse TEST_COMMENT_RESPONSE2
            = new CommentResponse(2L, "댓글내용22", TEST_MEMBER_DTO3);
    public static final CommentResponse TEST_COMMENT_RESPONSE3
            = new CommentResponse(3L, "댓글내용ㄹㄴㅁㅇㄹㄴㅇㄹ", TEST_MEMBER_DTO);
    public static final CommentResponse TEST_COMMENT_RESPONSE4
            = new CommentResponse(4L, "댓글이다아아아악", TEST_MEMBER_DTO2);
    public static final CommentResponse TEST_COMMENT_RESPONSE5
            = new CommentResponse(5L, "댓글댓글댓글댓글댁글", TEST_MEMBER_DTO3);

    public static final GoodCountResponse TEST_GOOD_COUTN_RESPONSE
            = new GoodCountResponse(1L ,57);


    public static final CreateNotificationRequest TEST_NOTIFICATION_CREATE_REQUEST
            = new CreateNotificationRequest("공지사항 제목", "공지사항 내용");
    public static final CreateNotificationRequest TEST_NOTIFICATION_CREATE_REQUEST2
            = new CreateNotificationRequest("공지사항 제목 수정", "공지사항 내용 수정");

    public static final AdminNotificationResponse TEST_NOTIFICATION_RESPONSE1
            = new AdminNotificationResponse(1L, "공지사항 제목", "공지사항 내용", LocalDateTime.now(), LocalDateTime.now());
    public static final AdminNotificationResponse TEST_NOTIFICATION_RESPONSE2
            = new AdminNotificationResponse(1L, "공지사항 제목 수정", "공지사항 내용 수정", LocalDateTime.now(), LocalDateTime.now());
    public static final AdminNotificationResponse TEST_NOTIFICATION_RESPONSE3
            = new AdminNotificationResponse(3L, "공지사항 제목33", "공지사항 내용32312", LocalDateTime.now(), LocalDateTime.now());

    public static final IsAdminNotiResponse TEST_IS_ADMIN_NOTIFICATION_RESPONSE
            = new IsAdminNotiResponse(true);
    public static final IsGoodNotiResponse TEST_IS_GOOD_NOTIFICATION_RESPONSE
            = new IsGoodNotiResponse(true);
    public static final IsCommentNotiResponse TEST_IS_COMMENT_NOTIFICATION_RESPONSE
            = new IsCommentNotiResponse(true);

    public static final PushNotiResponse TEST_PUSH_NOTIFICATION_RESPONSE1
            = new PushNotiResponse(1L, "새 댓글", "어쩌구 님이 게시글에 댓글을 달았습니다.", 3L, LocalDateTime.now());
    public static final PushNotiResponse TEST_PUSH_NOTIFICATION_RESPONSE2
            = new PushNotiResponse(2L, "새 댓글", "어쩌구저쩌구 님이 게시글에 댓글을 달았습니다.", 4L, LocalDateTime.now());
    public static final PushNotiResponse TEST_PUSH_NOTIFICATION_RESPONSE3
            = new PushNotiResponse(3L, "좋아요 알림", "어쩌구 님이 게시글에 좋아요를 눌렀습니다.", 3L, LocalDateTime.now());
    public static final PushNotiResponse TEST_PUSH_NOTIFICATION_RESPONSE4
            = new PushNotiResponse(4L, "좋아요 알림", "어쩌구 님이 게시글에 좋아요를 눌렀습니다.", 5L, LocalDateTime.now());
}
