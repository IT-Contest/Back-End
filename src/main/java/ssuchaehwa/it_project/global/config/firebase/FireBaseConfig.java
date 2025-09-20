package ssuchaehwa.it_project.global.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FireBaseConfig {

    @PostConstruct
    public void init() {
        try {
            String filePath = System.getenv("FIREBASE_CONFIG_PATH");
            if (filePath == null) {
                filePath = "/home/ec2-user/config/firebase-service-account.json"; // 기본 경로
            }

            FileInputStream serviceAccount = new FileInputStream(filePath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase SDK initialized successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ FirebaseMessaging Bean 등록
    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
