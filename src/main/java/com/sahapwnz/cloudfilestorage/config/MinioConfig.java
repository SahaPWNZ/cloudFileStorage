package com.sahapwnz.cloudfilestorage.config;


import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Bean
    public MinioClient minioClient(@Value("${minio.name}") String minioname,
                                   @Value("${minio.password}") String password,
                                   @Value("${minio.host}") String minioHost) {
        return MinioClient.builder()
                .endpoint(minioHost)
//                .endpoint("http://localhost:9000")
                .credentials(minioname, password)
                .build();
    }
}
