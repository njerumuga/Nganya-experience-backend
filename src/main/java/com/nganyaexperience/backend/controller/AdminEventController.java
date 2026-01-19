package com.nganyaexperience.backend.controller;

import com.nganyaexperience.backend.entity.Event;
import com.nganyaexperience.backend.entity.TicketType;
import com.nganyaexperience.backend.repository.BookingRepository;
import com.nganyaexperience.backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminEventController {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository; // needed for deleting bookings

    // ✅ CREATE EVENT
    @PostMapping(consumes = "multipart/form-data")
    public Event createEvent(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam Event.Status status,
            @RequestParam(required = false) MultipartFile poster
    ) throws Exception {

        String posterUrl = null;

        if (poster != null && !poster.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + poster.getOriginalFilename();
            Path uploadPath = Path.of("uploads/events/" + fileName);
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, poster.getBytes());

            posterUrl = "/events/" + fileName;
        }

        Event event = Event.builder()
                .title(title)
                .description(description)
                .location(location)
                .date(LocalDate.parse(date))
                .time(LocalTime.parse(time))
                .status(status)
                .posterUrl(posterUrl)
                .build();

        return eventRepository.save(event);
    }

    // ✅ DELETE EVENT (cascade tickets & bookings)
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        // Delete all bookings for this event's tickets
        for (TicketType ticket : event.getTickets()) {
            bookingRepository.deleteAllByTicketTypeId(ticket.getId());
        }

        // Delete poster file if exists
        if (event.getPosterUrl() != null) {
            File posterFile = new File("uploads" + event.getPosterUrl());
            if (posterFile.exists()) {
                posterFile.delete();
            }
        }

        // Delete event (tickets will be deleted automatically if mapped with CascadeType.ALL in Event entity)
        eventRepository.delete(event);
    }
}
