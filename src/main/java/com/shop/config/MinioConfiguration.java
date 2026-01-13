package com.shop.config;


import com.shop.minio.MinioData;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioData.class)
public class MinioConfiguration {


    @Bean
    public MinioClient minioClient(MinioData minioData) {
        return MinioClient.builder()
                .endpoint(minioData.getEndpoint())
                .credentials(minioData.getAccessKey(), minioData.getSecretKey())
                .build();
    }
}
