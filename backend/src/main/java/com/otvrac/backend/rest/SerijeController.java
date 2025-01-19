package com.otvrac.backend.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otvrac.backend.domain.Epizode;
import com.otvrac.backend.domain.Serije;
import com.otvrac.backend.service.SerijeService;
import com.otvrac.backend.wrapper.ResponseWrapper;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/serije")
public class SerijeController {

    @Autowired
    private final SerijeService serijeService;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    public SerijeController(SerijeService serijeService) {
        this.serijeService = serijeService;
    }

    @GetMapping
    public ResponseEntity<?> getAllSerije() {
        List<Serije> serije = serijeService.getAllSerije();
        List<Map<String, Object>> jsonLdList = serije.stream()
                .map(Serije::toJsonLd)
                .toList();
        return ResponseEntity.ok(jsonLdList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSerijaById(@PathVariable Integer id) {
        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper("Bad Request", "Invalid ID: ID must be a positive integer", null));
        }

        Serije serija = serijeService.getSerijaById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serija with ID " + id + " not found"));

        return ResponseEntity.ok(serija.toJsonLd());
    }

    @GetMapping("/{id}/epizode")
    public ResponseEntity<?> getEpizodeBySerijaId(@PathVariable Integer id) {
        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper("Bad Request", "Invalid ID: ID must be a positive integer", null));
        }

        Serije serija = serijeService.getSerijaById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serija with ID " + id + " not found"));

        List<Map<String, Object>> jsonLdList = serija.getEpizode().stream()
                .map(Epizode::toJsonLd)
                .toList();

        return ResponseEntity.ok(jsonLdList);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSerija(@RequestBody Serije serija) {
        Serije novaSerija = serijeService.createSerija(serija);
        return ResponseEntity.status(201).body(novaSerija.toJsonLd());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSerija(@PathVariable Integer id, @RequestBody Serije updatedSerija) {
        Serije serija = serijeService.updateSerija(id, updatedSerija);
        return ResponseEntity.ok(serija.toJsonLd());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSerija(@PathVariable Integer id) {
        serijeService.deleteSerija(id);
        return ResponseEntity.ok(new ResponseWrapper("OK", "Serija deleted successfully", null));
    }

    @GetMapping("/search")
    public ResponseEntity<?> getSerijeWithCombinedFilter(
            @RequestParam(required = false, defaultValue = "sve") String attribute,
            @RequestParam(required = false, defaultValue = "") String filter) {

        List<Serije> serije = filter == null || filter.trim().isEmpty()
                ? serijeService.getAllSerije()
                : serijeService.getSerijeWithFilteredAttributes(attribute, filter);

        List<Map<String, Object>> jsonLdList = serije.stream()
                .map(Serije::toJsonLd)
                .toList();

        return ResponseEntity.ok(jsonLdList);
    }

    @GetMapping("/download/json")
    public ResponseEntity<InputStreamResource> downloadJson(
            @RequestParam(required = false, defaultValue = "sve") String attribute,
            @RequestParam(required = false, defaultValue = "") String filter) throws IOException {

        List<Serije> serijeList = serijeService.search(filter, attribute);
        List<Map<String, Object>> jsonLdList = serijeList.stream()
                .map(Serije::toJsonLd)
                .toList();

        String json = jacksonObjectMapper.writeValueAsString(jsonLdList);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(json.getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"serije.json\"")
                .body(new InputStreamResource(byteArrayInputStream));
    }

    @GetMapping("/download/csv")
    public ResponseEntity<InputStreamResource> downloadCsv(
            @RequestParam(required = false, defaultValue = "sve") String attribute,
            @RequestParam(required = false, defaultValue = "") String filter) throws IOException {

        List<Serije> serijeList = serijeService.search(filter, attribute);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(byteArrayOutputStream);

        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Id", "Naslov", "Zanr", "Godina Izlaska", "Ocjena", "Broj Sezona", "Jezik", "Autor", "Mreza", "Naziv Epizode", "Sezona", "Broj Epizode", "Datum Emitiranja", "Trajanje", "Ocjena Epizode", "Scenarist", "Redatelj"));
        for (Serije serija : serijeList) {
            for (Epizode epizoda : serija.getEpizode()) {
                csvPrinter.printRecord(
                        serija.getId(),
                        serija.getNaslov(),
                        serija.getZanr(),
                        serija.getGodinaIzlaska(),
                        serija.getOcjena(),
                        serija.getBrojSezona(),
                        serija.getJezik(),
                        serija.getAutor(),
                        serija.getMreza(),
                        epizoda.getNazivEpizode(),
                        epizoda.getSezona(),
                        epizoda.getBrojEpizode(),
                        epizoda.getDatumEmitiranja(),
                        epizoda.getTrajanje(),
                        epizoda.getOcjena(),
                        epizoda.getScenarist(),
                        epizoda.getRedatelj()
                );
            }
        }
        csvPrinter.flush();
        csvPrinter.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"serije.csv\"")
                .body(new InputStreamResource(byteArrayInputStream));
    }

    @PostMapping("/refresh-files")
    public ResponseEntity<?> refreshFiles() {
        try {
            // Fetch data from the database
            List<Serije> serijeList = serijeService.getAllSerije();

            // Create CSV file
            try (Writer writer = new FileWriter("./src/main/resources/static/serije.csv");
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                         "Id", "Naslov", "Zanr", "Godina Izlaska", "Ocjena", "Broj Sezona", "Jezik", "Autor", "Mreza",
                         "Naziv Epizode", "Sezona", "Broj Epizode", "Datum Emitiranja", "Trajanje", "Ocjena Epizode",
                         "Scenarist", "Redatelj"))) {
                for (Serije serija : serijeList) {
                    for (Epizode epizoda : serija.getEpizode()) {
                        csvPrinter.printRecord(
                                serija.getId(),
                                serija.getNaslov(),
                                serija.getZanr(),
                                serija.getGodinaIzlaska(),
                                serija.getOcjena(),
                                serija.getBrojSezona(),
                                serija.getJezik(),
                                serija.getAutor(),
                                serija.getMreza(),
                                epizoda.getNazivEpizode(),
                                epizoda.getSezona(),
                                epizoda.getBrojEpizode(),
                                epizoda.getDatumEmitiranja(),
                                epizoda.getTrajanje(),
                                epizoda.getOcjena(),
                                epizoda.getScenarist(),
                                epizoda.getRedatelj()
                        );
                    }
                }
            }

            // Create JSON file
            String json = jacksonObjectMapper.writeValueAsString(serijeList);
            try (Writer writer = new FileWriter("./src/main/resources/static/serije.json")) {
                writer.write(json);
            }

            return ResponseEntity.ok(new ResponseWrapper("OK", "Files refreshed successfully", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ResponseWrapper("Error", "Failed to refresh files", null));
        }
    }
}
