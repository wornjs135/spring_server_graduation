package inu.graduation.sns.model.Image.dto;

import inu.graduation.sns.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
