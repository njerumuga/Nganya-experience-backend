package com.nganyaexperience.backend.controller;

import com.nganyaexperience.backend.entity.Event;
import com.nganyaexperience.backend.entity.TicketType;
import com.nganyaexperience.backend.repository.BookingRepository;
import com.nganyaexperience.backend.repository.EventRepository;
import com.nganyaexperience.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminEventController {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final FileStorageService fileStorageService;

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
    ) {

        String posterUrl = null;
        if (poster != null && !poster.isEmpty()) {
            posterUrl = fileStorageService.saveEventPoster(poster);
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

    // ✅ DELETE EVENT
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteEvent(@PathVariable Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        for (TicketType ticket : event.getTickets()) {
            bookingRepository.deleteAllByTicketTypeId(ticket.getId());
        }

        fileStorageService.deleteFile(event.getPosterUrl());
        eventRepository.delete(event);
    }
}
