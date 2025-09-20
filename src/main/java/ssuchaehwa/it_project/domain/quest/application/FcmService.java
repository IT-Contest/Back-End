//package ssuchaehwa.it_project.domain.quest.application;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class FcmService {
//
//    private final FirebaseMessaging firebaseMessaging;
//
//    public void sendNotification(String token, String title, String body, Map<String, String> data) {
//        try {
//            Message message = Message.builder()
//                    .setToken(token)
//                    .setNotification(Notification.builder()
//                            .setTitle(title)
//                            .setBody(body)
//                            .build())
//                    .putAllData(data)
//                    .build();
//
//            firebaseMessaging.send(message);
//        } catch (FirebaseMessagingException e) {
//            // 로깅만 하고 예외는 전파하지 않음 (푸시 실패해도 초대는 성공해야 하니까)
//            log.warn("FCM 발송 실패: {}", e.getMessage());
//        }
//    }
//}
