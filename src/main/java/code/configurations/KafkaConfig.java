package code.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    // Error Handler for Listeners
    @Bean
    public KafkaListenerErrorHandler validationErrorHandler() {
        return (Message<?> message, ListenerExecutionFailedException exception) -> {
            System.err.println("Error in listener: " + exception.getMessage());
            return null;
        };
    }

    // Global Error Handling (Retries & Dead Letter Queues) - Current validationErrorHandler is a simple listener-level handler. You can configure a global CommonErrorHandler (like DefaultErrorHandler) to handle retries and backoff policies before giving up.
    @Bean
    public DefaultErrorHandler errorHandler() {
        // Retry 3 times with 1 second interval, then log the error
        return new DefaultErrorHandler(new FixedBackOff(1000L, 3));
    }

    // Automatic Topic Builder - You can define NewTopic beans to ensure your topics exist when the application starts. This is very useful for development and testing.
    @Bean
    public NewTopic tradesTopic() {
        return TopicBuilder.name("trades")
                .partitions(10)
                .replicas(1)
                .build();
    }

    // If you need to override settings from application.properties for specific listeners (e.g., specific deserializers or batch listeners), you can define a custom KafkaListenerContainerFactory.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // Enable batch listening
        // factory.setBatchListener(true);
        return factory;
    }

    // JSON Message Converter - If you plan to send/receive POJOs (Plain Old Java Object eg: User and Trade - They are just data carriers) as JSON (instead of Avro) without manually serializing them, you can add a RecordMessageConverter.
    @Bean
    public RecordMessageConverter converter() {
        return new StringJsonMessageConverter();
    }
}