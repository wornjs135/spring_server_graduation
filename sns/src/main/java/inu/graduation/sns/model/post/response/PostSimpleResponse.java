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
public class PostSimpleResponse {
    private Long id;
    private String content;
    private List<ImageDto> imageDtoList;

    public PostSimpleResponse(Post post){
        this.id = post.getId();
        this.content = post.getContent();
        this.imageDtoList = post.getImageList().stream().map(image -> ImageDto.from(image))
                .collect(Collectors.toList());
    }
}
