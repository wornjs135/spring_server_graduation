package inu.graduation.sns.model.post.response;

import inu.graduation.sns.domain.Post;
import inu.graduation.sns.model.Image.dto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostImageUrlResponse {

    private List<ImageDto> imageDtoList;

    public PostImageUrlResponse(Post post) {
        this.imageDtoList = post.getImageList().stream().map(image -> ImageDto.from(image))
                .collect(Collectors.toList());
    }
}
