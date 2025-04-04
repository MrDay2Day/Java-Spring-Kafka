package code.schemaDownload;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        String filename = outputDirectory + "/" + schema.getName() + ".avsc";
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