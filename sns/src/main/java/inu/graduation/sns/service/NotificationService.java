package inu.graduation.sns.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.domain.Notification;
import inu.graduation.sns.domain.Post;
import inu.graduation.sns.domain.PushNoti;
import inu.graduation.sns.exception.MemberException;
import inu.graduation.sns.exception.NotificationException;
import inu.graduation.sns.model.notification.request.CreateNotificationRequest;
import inu.graduation.sns.model.notification.request.UpdateNotificationRequest;
import inu.graduation.sns.model.notification.response.AdminNotificationResponse;
import inu.graduation.sns.model.pushnoti.PushNotiResponse;
import inu.graduation.sns.repository.MemberRepository;
import inu.graduation.sns.repository.NotificationRepository;
import inu.graduation.sns.repository.PushNotiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final PushNotiRepository pushNotiRepository;

    @Value("${fcm.filePath}")
    private String FIREBASE_CONFIG_PATH;

    @PostConstruct
    public void initialize() {
        try {
            InputStream fcmOptionsInputStream = FIREBASE_CONFIG_PATH.startsWith("/") ? new FileSystemResource(FIREBASE_CONFIG_PATH).getInputStream()
                    : new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(fcmOptionsInputStream)).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void sendMessage(Post post, String title, String content) {
        pushNotiRepository.save(PushNoti.createPushNoti(title, content, post));
        if (!post.getMember().getFcmToken().isEmpty()) {
            try {
                Message message = Message.builder()
                        .setToken(post.getMember().getFcmToken())
                        .putData("title", title)
                        .putData("body", content)
                        .build();
                String response = FirebaseMessaging.getInstance().sendAsync(message).get();
                log.info("Sent message: " + response);
            } catch (ExecutionException e) {
                log.error("???????????? ?????? ??????");
            } catch (InterruptedException e) {
                log.error("???????????? ?????? ??????");
            } catch (IllegalArgumentException e) {
                log.error("???????????? ?????? ??????");
                throw new MemberException("fcm????????? ?????????????????????. ????????? ?????? ??????????????????.");
            }
        }
    }

    // ???????????? ?????? ??? ???????????????
    @Transactional
    public AdminNotificationResponse sendAllMessage(CreateNotificationRequest createNotificationRequest) {
        Notification createNotification = Notification.createNotification(createNotificationRequest);
        Notification saveNotification = notificationRepository.save(createNotification);

        Page<Member> allMember = memberRepository.findAllByAdminNotiIsTrue(PageRequest.of(0, 500));
        sendNotificationAll(allMember);
        if (allMember.getTotalPages() > 1) {
            for (int pageNumber = 1; pageNumber <= allMember.getTotalPages(); pageNumber++) {
                Page<Member> restMember = memberRepository.findAllByAdminNotiIsTrue(PageRequest.of(pageNumber, 500));
                sendNotificationAll(restMember);
            }
        }

        return new AdminNotificationResponse(saveNotification);
    }

    // ???????????? ??????
    @Transactional
    public AdminNotificationResponse updateNotification(UpdateNotificationRequest updateNotificationRequest, Long notificationId) {
        Notification findNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("???????????? ?????? ?????????????????????."));
        findNotification.updateNotification(updateNotificationRequest);

        return new AdminNotificationResponse(findNotification);
    }

    // ???????????? ??????
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification findNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("???????????? ?????? ?????????????????????."));
        notificationRepository.delete(findNotification);
    }

    // ???????????? ??????(???)
    public Slice<AdminNotificationResponse> findAllNotificationApp(Pageable pageable) {
        Slice<Notification> findAllNotification = notificationRepository.findAllNotification(pageable);

        return findAllNotification.map(notification -> new AdminNotificationResponse(notification));
    }

    // ???????????? ??????(???)
    public Page<AdminNotificationResponse> findAllNotificationWeb(Pageable pageable) {
        Page<Notification> findAllNotification = notificationRepository.findAll(pageable);

        return findAllNotification.map(notification -> new AdminNotificationResponse(notification));
    }

    // ???????????? ?????? ??????(???, ???)
    public AdminNotificationResponse findAdminNotification(Long notificationId) {
        Notification findAdminNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("???????????? ?????? ?????????????????????."));

        return new AdminNotificationResponse(findAdminNotification);
    }

    // ?????????, ?????? ?????? ??????(???)
    public Slice<PushNotiResponse> findMyPushNotificationApp(Long memberId, Pageable pageable) {
        Slice<PushNoti> findMyPushNoti = pushNotiRepository.findAllMyPushNotification(memberId, pageable);

        return findMyPushNoti.map(pushNoti -> new PushNotiResponse(pushNoti));
    }

    private void sendNotificationAll(Page<Member> allMemberList) {
        List<String> registrationTokens = new ArrayList<>();
        for (Member member : allMemberList.getContent()) {
            if (!member.getFcmToken().isEmpty()) {
                registrationTokens.add(member.getFcmToken());
            }
        }

        if (registrationTokens.size() != 0) {
            try {
                MulticastMessage message = MulticastMessage.builder()
                        .putData("title", AdminNoticeTitle())
                        .putData("body", createAdminNotice())
                        .addAllTokens(registrationTokens)
                        .build();

                BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
                log.info("Sent message: " + response);
            } catch (FirebaseMessagingException e) {
                log.error("???????????? ?????? ??????");
            }
        }
    }

    public static String createCommentNotice(String nickname) {
        return nickname + " ?????? ???????????? ????????? ???????????????.";
    }

    public static String createGoodNotice(String nickname) {
        return nickname + " ?????? ???????????? ???????????? ???????????????.";
    }

    public static String createAdminNotice() {
        return "??? ??????????????? ?????????????????????.";
    }

    public static String CommentNotificationTitle() {
        return "??? ??????";
    }

    public static String GoodNotificationTitle() {
        return "????????? ??????";
    }

    public static String AdminNoticeTitle() {
        return "??? ????????????";
    }
}
