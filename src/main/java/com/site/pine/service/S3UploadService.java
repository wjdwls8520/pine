package com.site.pine.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

        try {
            // 1. URL 디코딩 (한글 깨짐 방지)
            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8.toString());

            String key = decodedPath;

            // 2. Key 추출 로직
            if (decodedPath.startsWith("http")) {
                int index = decodedPath.indexOf(".amazonaws.com/");
                if (index != -1) {
                    key = decodedPath.substring(index + ".amazonaws.com/".length());
                } else {
                    System.out.println("⚠️ S3 URL 형식이 아님, 원본 경로 사용: " + decodedPath);
                }
            }

            // 3. 앞의 슬래시 제거
            if (key.startsWith("/")) {
                key = key.substring(1);
            }

            // 콘솔 출력
            System.out.println("========================================");
            System.out.println("[S3 삭제 요청]");
            System.out.println("원본 URL : " + path);
            System.out.println("추출 Key : " + key);

            // 4. 삭제 실행
            amazonS3.deleteObject(bucket, key);

            System.out.println("-> 삭제 완료");
            System.out.println("========================================");

        } catch (Exception e) {
            System.out.println("❌ S3 파일 삭제 실패: " + path);
            e.printStackTrace(); // 에러 내용은 봐야 하므로 출력
        }
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
