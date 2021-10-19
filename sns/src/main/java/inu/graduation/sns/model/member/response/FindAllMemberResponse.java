package inu.graduation.sns.model.member.response;

import inu.graduation.sns.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindAllMemberResponse {

    private Long id;
    private String nickname;
    private String role;

    public FindAllMemberResponse(Member member){
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.role = member.getRole().toString();
    }
}
