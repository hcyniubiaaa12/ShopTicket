package com.shop.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "minio")
@Data
@Component
public class MinioData {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}

