# Spring Boot Kafka Integration Example

This repository demonstrates how to integrate Apache Kafka with a Spring Boot application using Spring Kafka, enabling seamless message publishing and consumption with Avro serialization.

## 🚀 Overview

This project showcases a complete Kafka integration within a Spring Boot application, featuring:

- Kafka Producer and Consumer implementation
- Avro serialization for message payload
- Confluent Schema Registry integration
- Spring autowiring and component configurations
- Schema Registry schema downloader tool

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- Kafka cluster (localhost:9092, localhost:9093)
- Confluent Schema Registry (localhost:8081)

## 🏗️ Project Structure

```
├── src/main/java/com/example/
│   ├── User.java                  # Avro-generated model
│   └── code/
│       ├── Main.java              # Application entry point
│       ├── KafkaProducer.java     # Generic message producer
│       ├── KafkaConsumer.java     # Message consumer implementation
│       └── schemaDownload/
│           ├── SchemaRegistryDownloader.java  # Schema downloading utility
│           └── SchemaDownloadRunner.java      # Runner for schema downloads
├── src/main/resources/
│   ├── application.properties     # Kafka and application configurations
│   └── avro/                      # Avro schema definitions
└── pom.xml                        # Maven dependencies
```

## 🔧 Configuration

The application is configured in `application.properties`:

```properties
# Kafka broker configuration
spring.kafka.bootstrap-servers=localhost:9092,localhost:9093

# Producer serialization
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=io.confluent.kafka.serializers.KafkaAvroSerializer

# Consumer deserialization
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=io.confluent.kafka.serializers.KafkaAvroDeserializer

# Schema registry configuration
spring.kafka.consumer.properties.schema.registry.url=http://localhost:8081
spring.kafka.producer.properties.schema.registry.url=http://localhost:8081

# Avro specific record configurations
spring.kafka.consumer.properties.specific.avro.reader=true
spring.kafka.consumer.properties.value.subject.name.strategy=io.confluent.kafka.serializers.subject.RecordNameStrategy
spring.kafka.consumer.properties.schema.registry.schema.reflection=true

# Schema downloader configuration
schema.registry.url=http://localhost:8081
schema.output.directory=./schemas
```

## 🔌 Usage

The application demonstrates:

1. Creating a `User` object with sample data
2. Sending the user to a Kafka topic using a generic producer
3. Consuming the message from the same topic using a Kafka listener
4. Downloading Avro schemas from the Schema Registry

### Running the Application

```bash
mvn clean package
java -jar target/Java-Spring-Kafka-1.0-SNAPSHOT.jar
```

## 🛠️ Implementation Details

### Producer Component

```java
@Service
public class KafkaProducer<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, T data) {
        kafkaTemplate.send(topic, data);
    }
}
```

### Consumer Component

```java
@Service
public class KafkaConsumer {
    @KafkaListener(topics = "users", groupId = "user-group")
    public void consumeUser(User user) {
        System.out.println("Consumed user: " + user.getName());
    }
}
```

### Schema Registry Downloader

```java
@Component
public class SchemaRegistryDownloader {
    private final SchemaRegistryClient schemaRegistryClient;
    private final String outputDirectory;

    public SchemaRegistryDownloader(
            @Value("${schema.registry.url}") String schemaRegistryUrl,
            @Value("${schema.output.directory:./schemas}") String outputDirectory) {
        this.schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryUrl, 100);
        this.outputDirectory = outputDirectory;
    }

    /**
     * Download schema by subject name and save as AVSC file
     */
    public void downloadSchema(String subject) throws IOException, RestClientException {
        // Get latest schema version
        Schema schema = new Schema.Parser().parse(
                schemaRegistryClient.getLatestSchemaMetadata(subject).getSchema());
        
        // Create directory if it doesn't exist
        Path directory = Paths.get(outputDirectory);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        
        // Save schema to file
        String filename = outputDirectory + "/" + subject + ".avsc";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(schema.toString(true));
        }
        
        System.out.println("Schema for subject '" + subject + "' saved to " + filename);
    }
    
    /**
     * Download all schemas from registry
     */
    public void downloadAllSchemas() throws IOException, RestClientException {
        for (String subject : schemaRegistryClient.getAllSubjects()) {
            downloadSchema(subject);
        }
        System.out.println("All schemas downloaded successfully");
    }
}
```

### Schema Download Runner

```java
@Component
public class SchemaDownloadRunner {
    private final SchemaRegistryDownloader downloader;
    
    @Autowired
    public SchemaDownloadRunner(SchemaRegistryDownloader downloader) {
        this.downloader = downloader;
    }
    
    public void download() throws Exception {
        // To download a specific schema
        downloader.downloadSchema("users-value");
        
        // To download all available schemas
        // downloader.downloadAllSchemas();
    }
}
```

### Main Application

```java
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
        }
    }

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        
        // Initializing the Kafka producer
        UserRunner runner = context.getBean(UserRunner.class);
        runner.run();
        
        // Download schemas from registry
        SchemaDownloadRunner schemaRunner = context.getBean(SchemaDownloadRunner.class);
        schemaRunner.download();
    }
}
```

## 🔍 Common Issues and Troubleshooting

If you encounter the error: `Cannot convert from [org.apache.avro.generic.GenericData$Record] to [com.example.User]`, ensure:

1. The `User` class properly implements `org.apache.avro.specific.SpecificRecord`
2. You've configured the consumer with `spring.kafka.consumer.properties.specific.avro.reader=true`
3. The Schema Registry URL is correct and accessible

## 📥 Working with Schema Downloads

The schema downloader utility provides the following capabilities:

1. Download a specific schema by subject name
2. Download all available schemas from the registry
3. Save schemas as properly formatted `.avsc` files for use in code generation

To generate source from `.asc` files run: 

```bash
mvn clean generate-sources
```

This is particularly useful for:
- Version control of schemas
- Documentation purposes
- Local development and testing
- Code generation without direct registry access

## 📦 Dependencies

- Spring Boot Starter
- Spring Kafka
- Spring Web (for Schema Registry REST client)
- Apache Avro
- Confluent Schema Registry
- Confluent Avro Serializers
- Jackson for JSON processing

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.
