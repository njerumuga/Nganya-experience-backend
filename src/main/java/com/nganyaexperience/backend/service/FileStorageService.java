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
    private String uploadDir; // "uploads"

    private static final String NGANYA_FOLDER = "nganyas";

    // Save nganya image (persistent)
    public String saveNganyaImage(MultipartFile file) {
        try {
            Path nganyaUploadDir = Paths.get("/mnt/data/uploads", NGANYA_FOLDER);
            Files.createDirectories(nganyaUploadDir);

            String filename = "nganya_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = nganyaUploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            return "/uploads/nganyas/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store nganya image", e);
        }
    }

    // Save event/general file (non-nganya)
    public String saveEventImage(MultipartFile file) {
        try {
            Path eventUploadDir = Paths.get(uploadDir); // relative uploads folder
            Files.createDirectories(eventUploadDir);

            String filename = "event_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = eventUploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store event image", e);
        }
    }

    // Delete image
    public void deleteFile(String relativePath) {
        if (relativePath == null) return;

        try {
            Path filePath;
            if (relativePath.startsWith("/uploads/nganyas/")) {
                filePath = Paths.get("/mnt/data/uploads", relativePath.replaceFirst("/uploads/", ""));
            } else {
                filePath = Paths.get(uploadDir, relativePath.replaceFirst("/uploads/", ""));
            }
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            System.out.println("Failed to delete file: " + e.getMessage());
        }
    }
}
