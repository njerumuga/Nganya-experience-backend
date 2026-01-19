package com.nganyaexperience.backend.controller;

import com.nganyaexperience.backend.entity.Event;
import com.nganyaexperience.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    // ✅ Create Event
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    // ✅ Get all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // ✅ Get event by ID
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    // ✅ Upload poster image (admin-friendly)
    @PostMapping("/upload-poster")
    public String uploadPoster(@RequestParam("file") MultipartFile file) throws IOException {
        // Generate unique filename
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Path.of("uploads/events/" + fileName);

        // Ensure directories exist
        Files.createDirectories(path.getParent());

        // Save file locally
        Files.write(path, file.getBytes());

        // Return frontend-ready URL
        return "/events/" + fileName;
    }
}
