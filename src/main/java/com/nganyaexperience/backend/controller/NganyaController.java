package com.nganyaexperience.backend.controller;

import com.nganyaexperience.backend.entity.Nganya;
import com.nganyaexperience.backend.repository.NganyaRepository;
import com.nganyaexperience.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NganyaController {

    private final NganyaRepository nganyaRepository;
    private final FileStorageService fileStorageService;

    // ✅ GET ALL
    @GetMapping("/nganyas")
    public List<Nganya> getNganyas() {
        return nganyaRepository.findAll();
    }

    // ✅ CREATE
    @PostMapping("/admin/nganyas")
    public Nganya createNganya(
            @RequestParam String name,
            @RequestParam String size,
            @RequestParam(required = false) MultipartFile image
    ) {
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = fileStorageService.saveNganyaImage(image);
        }

        Nganya nganya = Nganya.builder()
                .name(name)
                .size(size)
                .imageUrl(imageUrl)
                .build();

        return nganyaRepository.save(nganya);
    }

    // ✅ DELETE (NEW)
    @DeleteMapping("/admin/nganyas/{id}")
    public ResponseEntity<?> deleteNganya(@PathVariable Long id) {
        Nganya nganya = nganyaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nganya not found"));

        // Delete image from disk
        fileStorageService.deleteFile(nganya.getImageUrl());

        // Delete DB record
        nganyaRepository.delete(nganya);

        return ResponseEntity.ok().build();
    }
}
