package com.nganyaexperience.backend.controller;

import com.nganyaexperience.backend.dto.AdminEventRequest;
import com.nganyaexperience.backend.dto.TicketTypeRequest;
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
import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminEventController {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final FileStorageService fileStorageService;

    // ✅ CREATE EVENT (WITH POSTER + TICKETS)
    @PostMapping(consumes = "multipart/form-data")
    public Event createEvent(
            @RequestPart("event") AdminEventRequest eventRequest,
            @RequestPart("tickets") List<TicketTypeRequest> tickets,
            @RequestPart(value = "poster", required = false) MultipartFile poster
    ) {

        String posterUrl = null;
        if (poster != null && !poster.isEmpty()) {
            posterUrl = fileStorageService.saveEventPoster(poster);
        }

        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .description(eventRequest.getDescription())
                .location(eventRequest.getLocation())
                .date(LocalDate.parse(eventRequest.getDate()))
                .time(LocalTime.parse(eventRequest.getTime()))
                .status(Event.Status.valueOf(eventRequest.getStatus()))
                .posterUrl(posterUrl)
                .build();

        Event savedEvent = eventRepository.save(event);

        for (TicketTypeRequest t : tickets) {
            TicketType ticket = TicketType.builder()
                    .name(t.getName())
                    .price(t.getPrice())
                    .capacity(t.getCapacity())
                    .event(savedEvent)
                    .build();

            savedEvent.getTickets().add(ticket);
        }

        return eventRepository.save(savedEvent);
    }

    // ✅ DELETE EVENT (CLEAN CASCADE)
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
