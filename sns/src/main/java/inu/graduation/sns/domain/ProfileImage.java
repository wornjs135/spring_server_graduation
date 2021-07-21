package inu.graduation.sns.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Embeddable;

import static inu.graduation.sns.model.common.DefaultProfielImg.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage {

    private String profileImageUrl;
    private String profileThumbnailImageUrl;
    private String imageStoreName;

    public static ProfileImage createDefaultProfileImage(){
        ProfileImage profileImage = new ProfileImage();
        profileImage.profileImageUrl = DEFAULT_PROFILE_IMG;
        profileImage.profileThumbnailImageUrl = DEFAULT_PROFILE_THUMBNAIL_IMG;
        profileImage.imageStoreName = "kakao_2.jpg";
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
