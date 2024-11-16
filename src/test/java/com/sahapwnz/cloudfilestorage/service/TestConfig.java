package com.sahapwnz.cloudfilestorage.service;

import io.minio.MinioClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public MinioClient minioClient() throws InterruptedException
    {
        Thread.sleep(3000);
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    @Bean
    public FileService fileService(MinioClient minioClient) {
        return new FileService(minioClient);
    }
}