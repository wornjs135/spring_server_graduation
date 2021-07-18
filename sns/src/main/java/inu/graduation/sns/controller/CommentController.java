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
    public ResponseEntity createComment(@LoginMember Long memberId, @PathVariable Long postId,
                                      @RequestBody @Valid CommentSaveRequest commentSaveRequest){
        commentService.saveComment(memberId, postId, commentSaveRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity updateComment(@LoginMember Long memberId, @PathVariable Long commentId,
                                        @RequestBody @Valid CommentUpdateRequest commentUpdateRequest){
        commentService.updateComment(memberId, commentId, commentUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@LoginMember Long memberId, @PathVariable Long commentId){
        commentService.deleteComment(memberId, commentId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 게시글의 댓글 검색(앱)
    @GetMapping("/m/posts/{postId}/comments")
    public ResponseEntity<Slice<CommentResponse>> findCommentsByPostIdApp(@PathVariable Long postId, Pageable pageable){
        Slice<CommentResponse> findComments = commentService.findCommentsByPostId(postId, pageable);

        return ResponseEntity.ok(findComments);
    }

    // 게시글의 댓글 검색(웹)
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> findCommentsByPostId(@PathVariable Long postId, Pageable pageable){
        Page<CommentResponse> findComments = commentService.findCommentsByPost(postId, pageable);

        return ResponseEntity.ok(findComments);
    }
}
