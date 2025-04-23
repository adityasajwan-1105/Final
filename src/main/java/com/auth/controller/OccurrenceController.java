package com.auth.controller;

import com.auth.model.Occurrence;
import com.auth.model.User;
import com.auth.service.OccurrenceService;
import com.auth.repository.UserRepository;
import com.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/occurrences")
public class OccurrenceController {

    @Autowired
    private OccurrenceService occurrenceService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createOccurrence(
            @RequestPart(value = "occurrence") String occurrenceJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // This helps with LocalDateTime serialization
            Occurrence occurrence = mapper.readValue(occurrenceJson, Occurrence.class);
            
            // Validate required fields
            if (occurrence.getTitle() == null || occurrence.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Title is required"));
            }
            
            if (occurrence.getDescription() == null || occurrence.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Description is required"));
            }
            
            if (occurrence.getLatitude() == 0 || occurrence.getLongitude() == 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Location coordinates are required"));
            }

            if (photos == null || photos.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "At least one photo is required"));
            }
            
            // Set default values
            occurrence.setReporterId(occurrence.getReporterId() != null ? occurrence.getReporterId() : "anonymous");
            occurrence.setStatus("PENDING");
            occurrence.setReportedAt(LocalDateTime.now());
            occurrence.setActiveOnMap(false); // Initially set to false until verified
            
            // Create the occurrence
            Occurrence savedOccurrence = occurrenceService.createOccurrence(occurrence, photos);
            return ResponseEntity.ok(savedOccurrence);
            
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "An unexpected error occurred while creating the occurrence";
            }
            return ResponseEntity.badRequest()
                .body(Map.of("message", errorMessage));
        }
    }

    @GetMapping
    public ResponseEntity<List<Occurrence>> getAllOccurrences() {
        return ResponseEntity.ok(occurrenceService.getAllOccurrences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Occurrence> getOccurrenceById(@PathVariable String id) {
        return occurrenceService.getOccurrenceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Occurrence>> getOccurrencesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(occurrenceService.getOccurrencesByStatus(status));
    }

    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<List<Occurrence>> getOccurrencesByReporter(@PathVariable String reporterId) {
        return ResponseEntity.ok(occurrenceService.getOccurrencesByReporterId(reporterId));
    }

    @GetMapping("/map/active")
    public ResponseEntity<List<Occurrence>> getActiveOccurrencesOnMap() {
        return ResponseEntity.ok(occurrenceService.getActiveOccurrencesOnMap());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Occurrence> updateOccurrenceStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam String verifiedBy,
            @RequestParam(required = false) String verificationNotes) {
        return ResponseEntity.ok(occurrenceService.updateOccurrenceStatus(id, status, verifiedBy, verificationNotes));
    }

    @PutMapping("/{id}/map-visibility")
    public ResponseEntity<Occurrence> toggleMapVisibility(@PathVariable String id) {
        return ResponseEntity.ok(occurrenceService.toggleMapVisibility(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOccurrence(@PathVariable String id) {
        occurrenceService.deleteOccurrence(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/photos/{photoIndex}")
    public ResponseEntity<byte[]> getOccurrencePhoto(
            @PathVariable String id,
            @PathVariable int photoIndex) {
        byte[] photoData = occurrenceService.getOccurrencePhoto(id, photoIndex);
        String contentType = occurrenceService.getOccurrencePhotoContentType(id, photoIndex);

        if (photoData != null && contentType != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(photoData);
        }
        return ResponseEntity.notFound().build();
    }
} 