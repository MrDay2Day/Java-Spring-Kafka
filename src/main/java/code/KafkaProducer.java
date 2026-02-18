package code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

@Service
public class KafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, T>> sendMessage(String topic, T data) {
        return kafkaTemplate.send(topic, data);
    }

    public void sendAsync(String topic, T data, BiConsumer<SendResult<String, T>, Throwable> callback) {
        CompletableFuture<SendResult<String, T>> future = kafkaTemplate.send(topic, data);
        future.whenComplete(callback);
    }

    public SendResult<String, T> sendSync(String topic, T data) throws ExecutionException, InterruptedException {
        return kafkaTemplate.send(topic, data).get();
    }
}