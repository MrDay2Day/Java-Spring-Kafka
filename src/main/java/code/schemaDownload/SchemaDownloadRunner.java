package code.schemaDownload;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SchemaDownloadRunner implements CommandLineRunner {

    private final SchemaRegistryDownloader downloader;

    @Autowired
    public SchemaDownloadRunner(SchemaRegistryDownloader downloader) {
        this.downloader = downloader;
    }

    public void download() throws Exception {
        downloader.downloadAllSchemas();
    }

    @Override
    public void run(String... args) throws Exception {
        // To download a specific schema
        // downloader.downloadSchema("users-value");

        // To download all available schemas
        downloader.downloadAllSchemas();
    }
}