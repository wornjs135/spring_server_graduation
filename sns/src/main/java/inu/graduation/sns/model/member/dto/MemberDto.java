package inu.graduation.sns.model.member.dto;

import inu.graduation.sns.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String nickname;

    public static MemberDto from(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.id = member.getId();
        memberDto.nickname = member.getNickname();
        return memberDto;
    }
}
