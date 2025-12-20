package com.site.pine.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

//    // S3 URL → 로컬 파일 다운로드
//    public void downloadFile(String fileUrl, Path targetPath) throws IOException {
//        try (InputStream in = new URL(fileUrl).openStream()) {
//            Files.copy(in, targetPath);
//        }
//    }

    // 로컬 파일(Path) → S3 업로드
    public String saveLocalFile(Path filePath) throws IOException {

        String fileName = UUID.randomUUID() + "_" + filePath.getFileName().toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(Files.size(filePath));
        metadata.setContentType("image/jpeg");

        String folderName = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String key = folderName + "/" + fileName;

        try (InputStream in = Files.newInputStream(filePath)) {
            amazonS3.putObject(bucket, key, in, metadata);
        }

        return amazonS3.getUrl(bucket, key).toString();
    }
}
