package code.app.consumers;

import com.trades.Trade;
import com.example.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "users", groupId = "user-group", concurrency = "3", errorHandler = "validationErrorHandler")
    public void consumeUser(User user) {
        try {
            System.out.println("Consumed user: " + user.getName() + " on thread: " + Thread.currentThread().getName());
        } catch (Exception e) {
            System.err.println("Error processing user: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "trades", groupId = "java-consumer-1", concurrency = "10", errorHandler = "validationErrorHandler")
    public void consumeTrade(Trade trade) {
        try {
            System.out.println("Regular Trade Received on thread: " + Thread.currentThread().getName());
            System.out.println("Symbol \t\t: " + trade.getSymbol());
            System.out.println("side \t\t: " + trade.getSide());
            System.out.println("price \t\t: " + trade.getPrice());
            System.out.println("amount \t\t: " + trade.getAmount());
            System.out.println("timestamp \t: " + trade.getTimestamp());
        } catch (Exception e) {
            System.err.println("Error processing trade: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "high-value-trades", groupId = "java-consumer-2", concurrency = "10", errorHandler = "validationErrorHandler")
    public void consumeHighVolumeTrade(Trade trade) {
        try {
            System.out.println("High Value Trade Received on thread: " + Thread.currentThread().getName());
            System.out.println("Symbol \t\t: " + trade.getSymbol());
            System.out.println("side \t\t: " + trade.getSide());
            System.out.println("price \t\t: " + trade.getPrice());
            System.out.println("amount \t\t: " + trade.getAmount());
            System.out.println("timestamp \t: " + trade.getTimestamp());
        } catch (Exception e) {
            System.err.println("Error processing trade: " + e.getMessage());
        }
    }
}