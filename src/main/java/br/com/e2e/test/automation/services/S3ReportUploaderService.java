package br.com.e2e.test.automation.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

@Service
public class S3ReportUploaderService {
        //example how to put content on S3 Bucket
    private static final Logger logger = LoggerFactory.getLogger(S3ReportUploaderService.class);

    private final AmazonS3 s3Client;

    public S3ReportUploaderService() {
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    public void uploadDirectoryToS3(Path reportDestination, String bucketName, String s3Prefix) throws IOException {
        if (!Files.exists(reportDestination) || !Files.isDirectory(reportDestination)) {
            throw new IllegalArgumentException("reportDestination must be an existing directory");
        }

        Files.walk(reportDestination)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String key = s3Prefix + "/" + reportDestination.relativize(path).toString().replace("\\", "/");
                    logger.info("Uploading {} to s3://{}/{}", path, bucketName, key);
                    s3Client.putObject(new PutObjectRequest(bucketName, key, path.toFile()));
                });
    }
}