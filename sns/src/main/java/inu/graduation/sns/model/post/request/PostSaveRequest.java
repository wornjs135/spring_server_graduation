package inu.graduation.sns.model.post.request;

import lombok.Data;

@Data
public class PostSaveRequest {

    private String content;
    private String firstAddress;
    private String secondAddress;
    private String restAddress;
    private Integer score;
    private Boolean isOpen;
}
