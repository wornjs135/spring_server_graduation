package inu.graduation.sns.controller;

import inu.graduation.sns.config.security.LoginMember;
import inu.graduation.sns.domain.Comment;
import inu.graduation.sns.model.comment.request.CommentSaveRequest;
import inu.graduation.sns.model.comment.request.CommentUpdateRequest;
import inu.graduation.sns.model.comment.response.CommentResponse;
import inu.graduation.sns.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@LoginMember Long memberId, @PathVariable Long postId,
                                      @RequestBody @Valid CommentSaveRequest commentSaveRequest){
        return ResponseEntity.ok(commentService.saveComment(memberId, postId, commentSaveRequest));
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@LoginMember Long memberId, @PathVariable Long commentId,
                                        @RequestBody @Valid CommentUpdateRequest commentUpdateRequest){
        return ResponseEntity.ok(commentService.updateComment(memberId, commentId, commentUpdateRequest));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@LoginMember Long memberId, @PathVariable Long commentId){
        commentService.deleteComment(memberId, commentId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 게시글의 댓글 조회(앱)
    @GetMapping("/m/posts/{postId}/comments")
    public ResponseEntity<Slice<CommentResponse>> findCommentsByPostIdApp(@PathVariable Long postId, Pageable pageable){
        return ResponseEntity.ok(commentService.findCommentsByPostId(postId, pageable));
    }

    // 게시글의 댓글 조회(웹)
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> findCommentsByPostId(@PathVariable Long postId, Pageable pageable){
        return ResponseEntity.ok(commentService.findCommentsByPost(postId, pageable));
    }
}
