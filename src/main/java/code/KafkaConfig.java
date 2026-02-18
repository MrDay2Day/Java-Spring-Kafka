package code;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaListenerErrorHandler validationErrorHandler() {
        return (Message<?> message, ListenerExecutionFailedException exception) -> {
            System.err.println("Error in listener: " + exception.getMessage());
            return null;
        };
    }
}