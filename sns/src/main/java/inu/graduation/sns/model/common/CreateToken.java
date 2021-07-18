package inu.graduation.sns.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateToken {

    private String accessToken;
    private String refreshToken;

    public static CreateToken from(String accessToken, String refreshToken){
        CreateToken createToken = new CreateToken();
        createToken.accessToken = accessToken;
        createToken.refreshToken = refreshToken;
        return createToken;
    }
}
