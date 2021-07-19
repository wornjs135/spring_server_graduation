package inu.graduation.sns.model.post.response;

import inu.graduation.sns.domain.Post;
import inu.graduation.sns.model.Image.dto.ImageDto;
import inu.graduation.sns.model.category.dto.CategoryDto;
import inu.graduation.sns.model.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String content;
    private String firstAddress;
    private String secondAddress;
    private String restAddress;
    private Integer score;
    private Boolean isOpen;
    private Integer countGood;
    private Integer countComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MemberDto memberDto;
    private CategoryDto categoryDto;
    private List<ImageDto> imageDtoList;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.firstAddress = post.getFirstAddress();
        this.secondAddress = post.getSecondAddress();
        this.restAddress = post.getRestAddress();
        this.score = post.getScore();
        this.isOpen = post.getIsOpen();
        this.countGood = post.getCountGood();
        this.countComment = post.getCountComment();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.memberDto = MemberDto.from(post.getMember());
        this.categoryDto = CategoryDto.from(post.getCategory());
        this.imageDtoList = post.getImageList().stream().map(image -> ImageDto.from(image))
                .collect(Collectors.toList());
    }
}
