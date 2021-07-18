package inu.graduation.sns.controller;

import inu.graduation.sns.domain.Category;
import inu.graduation.sns.model.category.request.CategorySaveRequest;
import inu.graduation.sns.model.category.request.CategoryUpdateRequest;
import inu.graduation.sns.service.CategoryService;
import inu.graduation.sns.service.CommentService;
import inu.graduation.sns.service.MemberService;
import inu.graduation.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final CategoryService categoryService;
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;

    // 관리자 로그인 구현하기

    // 카테고리 생성
    @PostMapping("/admin/categories")
    public ResponseEntity createCategory(@RequestBody @Valid CategorySaveRequest categorySaveRequest){
        categoryService.createCategory(categorySaveRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 카테고리 수정
    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity updateCategory(@PathVariable Long categoryId,
                                         @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest){
        categoryService.updateCategory(categoryId, categoryUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 카테고리 삭제
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable Long categoryId){
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회원 삭제
    @DeleteMapping("/admin/members/{memberId}")
    public ResponseEntity deleteMember(@PathVariable Long memberId){
        memberService.adminDeleteMember(memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 게시글 삭제
    @DeleteMapping("/admin/posts/{postId}")
    public ResponseEntity deletePost(@PathVariable Long postId){
        postService.adminDeletePost(postId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 댓글 삭제
    @DeleteMapping("/admin/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commentId){
        commentService.adminDeleteComment(commentId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
