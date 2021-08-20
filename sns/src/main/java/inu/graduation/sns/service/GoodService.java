package inu.graduation.sns.service;

import inu.graduation.sns.domain.Good;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.domain.Post;
import inu.graduation.sns.exception.GoodException;
import inu.graduation.sns.exception.MemberException;
import inu.graduation.sns.exception.PostException;
import inu.graduation.sns.model.good.response.GoodCountResponse;
import inu.graduation.sns.repository.GoodRepository;
import inu.graduation.sns.repository.MemberRepository;
import inu.graduation.sns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoodService {

    private final GoodRepository goodRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    //private final NotificationService notificationService;

    // 좋아요 하기
    @Transactional
    public GoodCountResponse saveGood(Long memberId, Long postId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        if (goodRepository.findByMemberAndPost(findMember, findPost).isPresent()){
            throw new GoodException("이미 좋아요를 누른 게시글입니다.");
        }
        Good good = Good.createGood(findMember, findPost);
        Good savedGood = goodRepository.save(good);
        findPost.addGoodCount();

        //notificationService.sendMessage("게시글 작성자 토큰", "새 좋아요", NotificationService.createGoodNotice(findMember.getNickname()));

        return new GoodCountResponse(savedGood.getId(), findPost.getCountGood());
    }

    // 좋아요 취소
    @Transactional
    public GoodCountResponse cancleGood(Long memberId, Long goodId) {
        Good findGood = goodRepository.findById(goodId)
                .orElseThrow(() -> new GoodException("존재하지 않는 좋아요입니다."));
        if(!findGood.getMember().getId().equals(memberId)){
            throw new GoodException("본인의 좋아요가 아닙니다.");
        }
        Post findPost = postRepository.findById(findGood.getPost().getId())
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        findPost.removeGoodCount();
        goodRepository.delete(findGood);

        return new GoodCountResponse(findGood.getId(), findPost.getCountGood());
    }
}
