package inu.graduation.sns.model.comment.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommentSaveRequest {

    @NotNull
    private String content;
}
