package inu.graduation.sns.controller;

import inu.graduation.sns.model.category.request.CategorySaveRequest;
import inu.graduation.sns.model.category.request.CategoryUpdateRequest;
import inu.graduation.sns.model.category.response.CategoryResponse;
import inu.graduation.sns.model.member.response.FindAllMemberResponse;
import inu.graduation.sns.model.member.response.MemberResponse;
import inu.graduation.sns.model.notification.request.CreateNotificationRequest;
import inu.graduation.sns.model.notification.request.UpdateNotificationRequest;
import inu.graduation.sns.model.notification.response.AdminNotificationResponse;
import inu.graduation.sns.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final CategoryService categoryService;
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final NotificationService notificationService;

    // 카테고리 생성
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategorySaveRequest categorySaveRequest){
        return ResponseEntity.ok(categoryService.createCategory(categorySaveRequest));
    }

    // 카테고리 수정
    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity updateCategory(@PathVariable Long categoryId,
                                         @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryUpdateRequest));
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

    // 닉네임으로 회원id 조회
    @GetMapping("/admin/members")
    public ResponseEntity<MemberResponse> findMemberByNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(memberService.adminFindMember(nickname));
    }

    // (웹) 회원 전체 목록
    @GetMapping("/admin/members/all")
    public ResponseEntity<Page<FindAllMemberResponse>> findAllMember(Pageable pageable) {
        return ResponseEntity.ok(memberService.findAllMember(pageable));
    }

    // 공지사항 등록 + 알림 보내기
    @PostMapping("/admin/notification")
    public ResponseEntity<AdminNotificationResponse> sendNotification(@RequestBody @Valid CreateNotificationRequest createNotificationRequest) {
        return ResponseEntity.ok(notificationService.sendAllMessage(createNotificationRequest));
    }

    // 공지사항 수정
    @PatchMapping("/admin/notification/{notificationId}")
    public ResponseEntity<AdminNotificationResponse> updateNotification(@RequestBody @Valid UpdateNotificationRequest updateNotificationRequest,
                                                                        @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.updateNotification(updateNotificationRequest, notificationId));
    }

    // 공지사항 삭제
    @DeleteMapping("/admin/notification/{notificationId}")
    public ResponseEntity deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}