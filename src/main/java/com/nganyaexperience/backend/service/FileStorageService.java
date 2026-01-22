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

    // ---------- CORE SAVE LOGIC ----------
    private String saveFile(MultipartFile file, String folder, String prefix) {
        try {
            // 🔥 ABSOLUTE PATH (CRITICAL FIX)
            Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path targetDir = baseDir.resolve(folder);

            Files.createDirectories(targetDir); // ALWAYS create

            String filename =
                    prefix + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path targetFile = targetDir.resolve(filename);

            file.transferTo(targetFile);

            // 🔥 PUBLIC URL (frontend safe)
            return "/uploads/" + folder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    // ---------- DELETE ----------
    public void deleteFile(String publicUrl) {
        if (publicUrl == null) return;

        try {
            Path path = Paths.get(uploadDir)
                    .resolve(publicUrl.replace("/uploads/", ""))
                    .toAbsolutePath();

            Files.deleteIfExists(path);
        } catch (Exception ignored) {}
    }
}
