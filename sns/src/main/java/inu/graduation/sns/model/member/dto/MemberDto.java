package inu.graduation.sns.model.member.dto;

import inu.graduation.sns.domain.Member;
import lombok.Data;

@Data
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
