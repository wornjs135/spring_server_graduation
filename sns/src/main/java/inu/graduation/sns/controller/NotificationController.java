package inu.graduation.sns.controller;

import inu.graduation.sns.config.security.LoginMember;
import inu.graduation.sns.model.notification.response.IsAdminNotiResponse;
import inu.graduation.sns.model.notification.response.IsCommentNotiResponse;
import inu.graduation.sns.model.notification.response.IsGoodNotiResponse;
import inu.graduation.sns.model.notification.response.AdminNotificationResponse;
import inu.graduation.sns.model.pushnoti.PushNotiResponse;
import inu.graduation.sns.service.MemberService;
import inu.graduation.sns.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    // 공지사항 알림받기 여부 설정
    @PatchMapping("/setting/adminnoti")
    public ResponseEntity<IsAdminNotiResponse> settingNotification(@LoginMember Long memberId) {
        return ResponseEntity.ok(memberService.updateNotification(memberId));
    }

    // 좋아요 알림받기 여부 설정
    @PatchMapping("/setting/goodnoti")
    public ResponseEntity<IsGoodNotiResponse> settingGoodNotification(@LoginMember Long memberId) {
        return ResponseEntity.ok(memberService.updateGoodNotification(memberId));
    }

    // 댓글 알림받기 여부 설정
    @PatchMapping("/setting/commentnoti")
    public ResponseEntity<IsCommentNotiResponse> settingCommentNotification(@LoginMember Long memberId) {
        return ResponseEntity.ok(memberService.updateCommentNotification(memberId));
    }

    // 공지사항 리스트 조회(웹)
    @GetMapping("/notifications")
    public ResponseEntity<Page<AdminNotificationResponse>> findAllNotificationWeb(Pageable pageable) {
        return ResponseEntity.ok(notificationService.findAllNotificationWeb(pageable));
    }

    // 공지사항 리스트 조회(앱)
    @GetMapping("/m/notifications")
    public ResponseEntity<Slice<AdminNotificationResponse>> findAllNotificationApp(Pageable pageable) {
        return ResponseEntity.ok(notificationService.findAllNotificationApp(pageable));
    }

    // 좋아요, 댓글 알림 리스트 조회(앱)
    @GetMapping("/m/members/notifications")
    public ResponseEntity<Slice<PushNotiResponse>> findMyPushNotificationApp(@LoginMember Long memberId, Pageable pageable) {
        return ResponseEntity.ok(notificationService.findMyPushNotificationApp(memberId, pageable));
    }

}
