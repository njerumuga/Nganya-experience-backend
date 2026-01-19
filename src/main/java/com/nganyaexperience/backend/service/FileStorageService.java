package com.nganyaexperience.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final String NGANYA_FOLDER = "nganyas";

    // ✅ SAVE IMAGE
    public String saveNganyaImage(MultipartFile file) {
        try {
            Path nganyaUploadDir = Paths.get(uploadDir, NGANYA_FOLDER);

            // Ensure directory exists
            Files.createDirectories(nganyaUploadDir);

            String filename =
                    "nganya_" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path filePath = nganyaUploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            // URL frontend can load
            return "/uploads/nganyas/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store nganya image", e);
        }
    }

    // ✅ DELETE IMAGE
    public void deleteFile(String relativePath) {
        if (relativePath == null) return;

        try {
            Path filePath = Paths.get(relativePath.replaceFirst("/", ""));
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            System.out.println("Failed to delete file: " + e.getMessage());
        }
    }
}
