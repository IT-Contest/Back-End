package ssuchaehwa.it_project.global.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FireBaseConfig {

    // @PostConstruct
    // public void init() {
    //     try {
    //         String filePath = System.getenv("FIREBASE_CONFIG_PATH");
    //         if (filePath == null) {
    //             filePath = "/home/ubuntu/config/firebase-service-account.json"; // 기본 경로
    //         }

    //         FileInputStream serviceAccount = new FileInputStream(filePath);

    //         FirebaseOptions options = FirebaseOptions.builder()
    //                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    //                 .build();

    //         if (FirebaseApp.getApps().isEmpty()) {
    //             FirebaseApp.initializeApp(options);
    //             System.out.println("✅ Firebase SDK initialized successfully");
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

   @PostConstruct
   public void init() {
       try {
           InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();

           FirebaseOptions options = FirebaseOptions.builder()
                   .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                   .build();

           if (FirebaseApp.getApps().isEmpty()) {
               FirebaseApp.initializeApp(options);
               System.out.println("✅ Firebase SDK initialized successfully (local)");
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
