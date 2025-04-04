package code;

import com.example.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "users", groupId = "user-group")
    public void consumeUser(User user) {
        System.out.println("Consumed user: " + user.getName());
    }
}