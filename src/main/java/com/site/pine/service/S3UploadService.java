package com.site.pine.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public String saveFile(MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFilename; // S3에 저장되는 실제 파일명

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String folderName = today.format(formatter);

        // ★ 업로드될 S3 경로 생성 (오늘 날짜 폴더 + 파일명)
        String key = folderName + "/" + fileName;

        amazonS3.putObject(bucket, key, file.getInputStream(), metadata);

        return amazonS3.getUrl(bucket, key).toString(); // URL도 저장 파일명 기반
    }

    public void deleteFile(String path) {
        if (path == null || path.isEmpty()) return;

        String key = path;

        // URL → S3 key 변환
        if (path.startsWith("http")) {
            key = path.substring(path.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
            if (key.startsWith("/")) {
                key = key.substring(1);
            }
        }

        System.out.println("S3 DELETE key = " + key);

        amazonS3.deleteObject(bucket, key);

        System.out.println("S3 DELETE DONE");
    }
}
