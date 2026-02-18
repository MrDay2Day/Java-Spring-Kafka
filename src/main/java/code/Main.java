package code;

import code.schemaDownload.SchemaDownloadRunner;
import com.example.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Main {
    @Component
    public static class UserRunner {

        private final KafkaProducer<User> kafkaProducer;

        @Autowired
        public UserRunner(KafkaProducer<User> kafkaProducer) {
            this.kafkaProducer = kafkaProducer;
        }

        public void run() {
            User user = new User();
            user.setAge(123);
            user.setName("John Doe");

            kafkaProducer.sendMessage("users", user);

            System.out.println("User message sent to Kafka.");
        }
    }

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
//
//        // Initializing the Kafka producer
//        UserRunner runner = context.getBean(UserRunner.class);
//        runner.run();
//
//        // Download schemas from registry
//        SchemaDownloadRunner schemaRunner = context.getBean(SchemaDownloadRunner.class);
//        schemaRunner.download();
    }
}