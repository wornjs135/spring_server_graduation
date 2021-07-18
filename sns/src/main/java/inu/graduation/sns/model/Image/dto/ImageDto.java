package inu.graduation.sns.model.Image.dto;

import inu.graduation.sns.domain.Image;
import lombok.Data;
import lombok.Getter;

@Data
public class ImageDto {
    private Long id;
    private String imageUrl;
    private String thumbnailImageUrl;

    public static ImageDto from(Image image){
        ImageDto imageDto = new ImageDto();
        imageDto.id = image.getId();
        imageDto.imageUrl = image.getImageUrl();
        imageDto.thumbnailImageUrl = image.getThumbnailImageUrl();
        return imageDto;
    }
}
