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

    // ---------- SAVE EVENT POSTER ----------
    public String saveEventPoster(MultipartFile file) {
        return saveFile(file, "events", "event");
    }

    // ---------- SAVE NGANYA IMAGE ----------
    public String saveNganyaImage(MultipartFile file) {
        return saveFile(file, "nganyas", "nganya");
    }

    // ---------- COMMON LOGIC ----------
    private String saveFile(MultipartFile file, String folder, String prefix) {
        try {
            Path dir = Paths.get(uploadDir, folder);
            Files.createDirectories(dir);

            String filename = prefix + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = dir.resolve(filename);

            file.transferTo(filePath.toFile());

            // 🔥 ALWAYS RETURN /uploads/**
            return "/uploads/" + folder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    // ---------- DELETE ----------
    public void deleteFile(String publicUrl) {
        if (publicUrl == null) return;

        try {
            Path path = Paths.get(publicUrl.substring(1)); // remove leading /
            Files.deleteIfExists(path);
        } catch (Exception ignored) {}
    }
}
