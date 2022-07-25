package ca.dal.cs.wanderer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootApplication
public class WandererApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(WandererApplication.class, args);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(
                        new ClassPathResource("serviceAccountKey.json").getInputStream()
                ))
                .build();

        FirebaseApp.initializeApp(options);

    }
}
