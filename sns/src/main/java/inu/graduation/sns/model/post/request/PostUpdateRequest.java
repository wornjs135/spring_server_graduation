package inu.graduation.sns.model.post.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    private String content;
    private String address;
    private Integer score;
    private Boolean isOpen;
}
