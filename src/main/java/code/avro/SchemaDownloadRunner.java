package code.avro;

import code.avro.schemaDownload.SchemaRegistryDownloader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SchemaDownloadRunner implements CommandLineRunner {

    private final SchemaRegistryDownloader downloader;

    @Autowired
    public SchemaDownloadRunner(SchemaRegistryDownloader downloader) {
        this.downloader = downloader;
    }


    @PostConstruct
    public void download() throws Exception {
        System.out.println("Running AVRO Schema Downloader");
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