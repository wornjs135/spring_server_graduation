package inu.graduation.sns.model.member.response;

import inu.graduation.sns.domain.Member;
import inu.graduation.sns.domain.Role;
import inu.graduation.sns.model.common.CreateToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {

    private CreateToken createToken;
    private Role role;

    public LoginResponse(CreateToken createToken, Role role) {
        this.createToken = createToken;
        this.role = role;
    }
}
