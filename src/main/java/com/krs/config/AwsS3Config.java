package com.krs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krs.service.AwsS3Service;
import com.krs.service.impl.AwsS3ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.net.URI;


@Configuration
public class AwsS3Config {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint-url}")
    private String endpointUrl;

    @Bean
    public S3Client s3Client() {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();

        if (!bucketExists(s3Client)) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }

        return s3Client;
    }

    private boolean bucketExists(S3Client s3Client) {
        return s3Client.listBuckets().buckets().stream()
                .anyMatch(b -> b.name().equals(bucketName));
    }

    @Bean
    public AwsS3Service awsS3Service(S3Client s3Client) {
        return new AwsS3ServiceImpl(s3Client, bucketName, new ObjectMapper());
    }
}