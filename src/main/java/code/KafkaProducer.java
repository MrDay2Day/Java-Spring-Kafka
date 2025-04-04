package code;

import com.example.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer<T> { // Generic type T declared at class level

    private final KafkaTemplate<String, T> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, T data) {
        kafkaTemplate.send(topic, data);
    }
}