package com.otvrac.backend.rest;

import com.otvrac.backend.domain.Epizode;
import com.otvrac.backend.service.EpizodeService;
import com.otvrac.backend.wrapper.ResponseWrapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        return ResponseEntity.ok(new ResponseWrapper("OK", "Fetched all epizode", epizode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEpizodaById(@PathVariable Integer id) {
        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper("Bad Request", "Invalid ID: ID must be a positive integer", null));
        }

        Epizode epizoda = epizodeService.getEpizodaById(id)
                .orElseThrow(() -> new EntityNotFoundException("Epizoda with ID " + id + " not found"));

        return ResponseEntity.ok(new ResponseWrapper("OK", "Fetched epizoda with ID: " + id, epizoda));
    }
}