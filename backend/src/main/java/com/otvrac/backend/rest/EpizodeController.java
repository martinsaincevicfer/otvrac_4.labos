package com.otvrac.backend.rest;

import com.otvrac.backend.domain.Epizode;
import com.otvrac.backend.service.EpizodeService;
import com.otvrac.backend.wrapper.ResponseWrapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/epizode")
public class EpizodeController {

    @Autowired
    private final EpizodeService epizodeService;

    public EpizodeController(EpizodeService epizodeService) {
        this.epizodeService = epizodeService;
    }

    @GetMapping
    public ResponseEntity<?> getAllEpizode() {
        List<Epizode> epizode = epizodeService.getAllEpizode();

        // Convert each Epizoda object to JSON-LD format
        List<Map<String, Object>> jsonLdEpizode = epizode.stream()
                .map(Epizode::toJsonLd)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ResponseWrapper("OK", "Fetched all epizode", jsonLdEpizode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEpizodaById(@PathVariable Integer id) {
        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper("Bad Request", "Invalid ID: ID must be a positive integer", null));
        }

        Epizode epizoda = epizodeService.getEpizodaById(id)
                .orElseThrow(() -> new EntityNotFoundException("Epizoda with ID " + id + " not found"));

        // Return the Epizoda object in JSON-LD format
        return ResponseEntity.ok(new ResponseWrapper("OK", "Fetched epizoda with ID: " + id, epizoda.toJsonLd()));
    }
}