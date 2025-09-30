package ssuchaehwa.it_project.global.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmService {

    public void sendNotification(String token, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .putAllData(data) // 커스텀 데이터 전달 가능
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    ) // ✅ Builder 패턴 사용
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ FCM 발송 성공: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
