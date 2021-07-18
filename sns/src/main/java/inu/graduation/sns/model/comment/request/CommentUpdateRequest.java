package inu.graduation.sns.model.comment.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommentUpdateRequest {

    @NotNull
    private String content;
}
