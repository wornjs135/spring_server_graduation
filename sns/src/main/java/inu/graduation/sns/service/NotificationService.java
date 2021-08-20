package inu.graduation.sns.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import inu.graduation.sns.domain.Member;
import inu.graduation.sns.model.notification.request.CreateNotificationRequest;
import inu.graduation.sns.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

//    private final MemberRepository memberRepository;
//
//    @Value("${fcm.filePath}")
//    private String FIREBASE_CONFIG_PATH;
//
//    @PostConstruct
//    public void initialize() {
//        try {
//            InputStream fcmOptionsInputStream = FIREBASE_CONFIG_PATH.startsWith("/") ? new FileSystemResource(FIREBASE_CONFIG_PATH).getInputStream()
//                    : new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream();
//
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(fcmOptionsInputStream)).build();
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//                log.info("Firebase application has been initialized");
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }
//
//    public void sendMessage(String targetToken, String title, String content) {
//        Message message = Message.builder()
//                .setToken(targetToken)
//                .putData("title", title)
//                .putData("body", content)
//                .build();
//        try {
//            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
//            log.info("Sent message: " + response);
//        } catch (ExecutionException e) {
//            log.error("푸쉬알림 전송 실패");
//        } catch (InterruptedException e) {
//            log.error("푸쉬알림 전송 실패");
//        }
//    }
//
//    public void sendAllMessage(CreateNotificationRequest createNotificationRequest) {
//        // 회원 조회해와서 회원들 fcm토큰 리스트에 추가해서 다 보내는거 구현하기
//        Page<Member> allMember = memberRepository.findAll(PageRequest.of(0, 500));
//        sendNotificationAll(createNotificationRequest, allMember);
//        if (allMember.getTotalPages() > 0) {
//            for (int pageNumber = 1; pageNumber <= allMember.getTotalPages(); pageNumber++) {
//                Page<Member> allMember2 = memberRepository.findAll(PageRequest.of(pageNumber, 500));
//                sendNotificationAll(createNotificationRequest, allMember2);
//            }
//        }
//    }
//
//    private void sendNotificationAll(CreateNotificationRequest createNotificationRequest, Page<Member> allMemberList) {
//        List<String> registrationTokens = new ArrayList<>();
//        for (Member member : allMemberList.getContent()) {
//            // 예제로 리프레쉬토큰 넣어놧음.
//            registrationTokens.add(member.getRefreshToken());
//        }
//
//        MulticastMessage message = MulticastMessage.builder()
//                .putData("title", createNotificationRequest.getTitle())
//                .putData("body", createNotificationRequest.getContent())
//                .addAllTokens(registrationTokens)
//                .build();
//
//        try {
//            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
//            log.info("Sent message: " + response);
//        } catch (FirebaseMessagingException e) {
//            log.error("푸쉬알림 전송 실패");
//        }
//    }
//
//    public static String createCommentNotice(String nickname) {
//        return nickname + " 님이 게시글에 댓글을 달았습니다.";
//    }
//
//    public static String createGoodNotice(String nickname) {
//        return nickname + " 님이 게시글에 좋아요를 눌렀습니다.";
//    }
}
