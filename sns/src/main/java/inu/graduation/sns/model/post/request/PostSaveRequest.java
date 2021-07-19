package inu.graduation.sns.model.post.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSaveRequest {

    private String content;
    private String firstAddress;
    private String secondAddress;
    private String restAddress;
    private Integer score;
    private Boolean isOpen;
}
