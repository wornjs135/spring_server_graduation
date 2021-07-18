package inu.graduation.sns.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage {

    private String profileImageUrl;
    private String profileThumbnailImageUrl;
    private String imageStoreName;

    public static ProfileImage createDefaultProfileImage(){
        ProfileImage profileImage = new ProfileImage();
        profileImage.profileImageUrl = "기본이미지";
        profileImage.profileThumbnailImageUrl = "기본썸넹ㄹ";
        profileImage.imageStoreName = "기본이미지이름";
        return profileImage;
    }

    public static ProfileImage updateProfileImage(String profileImageUrl, String thumbnailImageUrl, String imageStoreName){
        ProfileImage profileImage = new ProfileImage();
        profileImage.profileImageUrl = profileImageUrl;
        profileImage.profileThumbnailImageUrl = thumbnailImageUrl;
        profileImage.imageStoreName = imageStoreName;
        return profileImage;
    }
}
