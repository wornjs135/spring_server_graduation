package inu.graduation.sns;

import inu.graduation.sns.model.member.response.MemberResponse;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

public class TestObject {

    public static final MemberResponse TEST_MEMBER_RESPONSE
            = new MemberResponse(1L,"이메일", "황주환", "이미지", "썸네일");

    public static final MemberResponse TEST_MEMBER_RESPONSE_UPDATE_PROFILEIMG
            = new MemberResponse(1L,"이메일", "황주환", "수정된이미지", "수정된썸네일");

    public static final MockMultipartFile TEST_IMAGE_FILE = new MockMultipartFile(
            "image",
            "프로필사진.png",
            MediaType.IMAGE_PNG_VALUE,
            "<<image>>".getBytes(StandardCharsets.UTF_8));


}
