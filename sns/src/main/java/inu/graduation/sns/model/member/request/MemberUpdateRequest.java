package inu.graduation.sns.model.member.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class
MemberUpdateRequest {

    @NotBlank
    private String nickname;
}
