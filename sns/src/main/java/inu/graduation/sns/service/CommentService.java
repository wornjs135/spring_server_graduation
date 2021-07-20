package inu.graduation.sns.service;

import inu.graduation.sns.domain.Comment;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.domain.Post;
import inu.graduation.sns.exception.CommentException;
import inu.graduation.sns.exception.MemberException;
import inu.graduation.sns.exception.PostException;
import inu.graduation.sns.model.comment.request.CommentSaveRequest;
import inu.graduation.sns.model.comment.request.CommentUpdateRequest;
import inu.graduation.sns.model.comment.response.CommentResponse;
import inu.graduation.sns.repository.CommentRepository;
import inu.graduation.sns.repository.MemberRepository;
import inu.graduation.sns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 댓글 생성
    @Transactional
    public CommentResponse saveComment(Long memberId, Long postId, CommentSaveRequest commentSaveRequest) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        Comment comment = Comment.saveComment(commentSaveRequest.getContent(), findMember, findPost);
        findPost.addCommentCount();
        Comment savedComment = commentRepository.save(comment);
        return new CommentResponse(savedComment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long memberId, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException("존재하지 않는 댓글입니다."));

        if(!findComment.getMember().equals(findMember)){
            throw new CommentException("본인의 댓글이 아닙니다.");
        }
        findComment.updateComment(commentUpdateRequest.getContent());

        return new CommentResponse(findComment);
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteComment(Long memberId, Long commentId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException("존재하지 않는 댓글입니다."));
        if(!findComment.getMember().equals(findMember)){
            throw new CommentException("본인의 댓글이 아닙니다.");
        }
        Post findPost = postRepository.findById(findComment.getPost().getId())
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        findPost.removeCommentCount();
        commentRepository.delete(findComment);

        return true;
    }

    // 관리자가 댓글 삭제
    @Transactional
    public boolean adminDeleteComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException("존재하지 않는 댓글입니다."));
        Post findPost = postRepository.findById(findComment.getPost().getId())
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        findPost.removeCommentCount();
        commentRepository.delete(findComment);

        return true;
    }

    // 게시글의 댓글들 검색(앱)
    public Slice<CommentResponse> findCommentsByPostId(Long postId, Pageable pageable) {
        return commentRepository.findCommentsByPostId(postId, pageable);
    }

    // 게시글의 댓글들 검색(웹)
    public Page<CommentResponse> findCommentsByPost(Long postId, Pageable pageable) {
        Page<Comment> result = commentRepository.findCommentsByPost(postId, pageable);

        return result.map(comment -> new CommentResponse(comment));
    }
}
