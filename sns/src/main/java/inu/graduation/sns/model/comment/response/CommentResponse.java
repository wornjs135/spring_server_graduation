package inu.graduation.sns.model.comment.response;

import inu.graduation.sns.domain.Comment;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.model.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private MemberDto memberDto;

    public CommentResponse(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.memberDto = MemberDto.from(comment.getMember());
    }
}
