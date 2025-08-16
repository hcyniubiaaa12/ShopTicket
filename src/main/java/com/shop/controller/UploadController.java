package com.shop.controller;

import com.shop.minio.MinioData;
import com.shop.result.Result;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@CrossOrigin
@RestController
public class UploadController {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioData minioData;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        try {
            boolean b = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioData.getBucketName()).build());
            if (!b) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioData.getBucketName()).build());
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(minioData.getBucketName())
                        .config(createBucketPolicyConfig(minioData.getBucketName()))
                        .build());
            }

            String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" +
                    UUID.randomUUID() + "-" + file.getOriginalFilename();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioData.getBucketName())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(filename)
                    .contentType(file.getContentType())
                    .build());

            String url = String.join("/", minioData.getEndpoint(), minioData.getBucketName(), filename);
            return Result.success(url);


        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }

    }


    private String createBucketPolicyConfig(String bucketName) {

        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }


}

