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

    // Use /mnt/data/uploads on Render or default to local uploads folder
    @Value("${file.upload-dir:/mnt/data/uploads}")
    private String uploadDir;

    private static final String NGANYA_FOLDER = "nganyas";

    // ✅ SAVE IMAGE (Render-compatible)
    public String saveNganyaImage(MultipartFile file) {
        try {
            // Ensure nganyas folder exists
            Path nganyaUploadDir = Paths.get(uploadDir, NGANYA_FOLDER);
            Files.createDirectories(nganyaUploadDir);

            // Generate unique filename
            String filename = "nganya_" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Save file to the folder
            Path filePath = nganyaUploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            // Return URL the frontend can fetch
            // Make sure your backend serves /uploads/** from uploadDir
            return "/uploads/nganyas/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store nganya image", e);
        }
    }

    // ✅ DELETE IMAGE
    public void deleteFile(String relativePath) {
        if (relativePath == null) return;

        try {
            // Remove leading / and resolve to uploadDir
            Path filePath = Paths.get(uploadDir, relativePath.replaceFirst("^/uploads/", ""));
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            System.out.println("Failed to delete file: " + e.getMessage());
        }
    }
}
