package com.gangchu.gangchutrip.mission.voice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload.dir:uploads/voice-missions}")
    private String uploadDir;
    
    @Value("${server.domain:http://localhost:8080}")
    private String serverDomain;
    
    public String saveVoiceFile(MultipartFile file, Long memberId, Long missionId) throws IOException {
        String memberDir = String.format("member-%d/mission-%d", memberId, missionId);
        Path uploadPath = Paths.get(uploadDir, memberDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = generateFileName(file);
        Path filePath = uploadPath.resolve(fileName);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return String.format("%s/api/files/voice/%s/%s", serverDomain, memberDir, fileName);
    }
    
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            String contentType = file.getContentType();
            if ("audio/mpeg".equals(contentType)) {
                extension = ".mp3";
            } else if ("audio/wav".equals(contentType)) {
                extension = ".wav";
            } else if ("audio/ogg".equals(contentType)) {
                extension = ".ogg";
            } else {
                extension = ".mp3";
            }
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s%s", timestamp, uuid, extension);
    }
}