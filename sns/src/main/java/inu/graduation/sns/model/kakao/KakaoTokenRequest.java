package inu.graduation.sns.model.kakao;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class KakaoTokenRequest {

    @NotNull
    private String accessToken;
}
