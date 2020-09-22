package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
public class TimeEntryController {
    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        this.timeEntryRepository.create(timeEntryToCreate)
                );
    }

    @GetMapping("/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable("timeEntryId") long timeEntryId) {
        TimeEntry timeEntry = this.timeEntryRepository.find(timeEntryId);
        if (timeEntry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(timeEntry);
        }

    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        return ResponseEntity.ok(this.timeEntryRepository.list());
    }

    @PutMapping("/time-entries/{timeEntryId}")
    public ResponseEntity update(@PathVariable("timeEntryId") long timeEntryId, @RequestBody TimeEntry expected) {
        TimeEntry timeEntry = this.timeEntryRepository.update(timeEntryId, expected);
        if (timeEntry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(timeEntry);
        }
    }

    @DeleteMapping("/time-entries/{timeEntryId}")
    public ResponseEntity delete(@PathVariable("timeEntryId") long timeEntryId) {
        this.timeEntryRepository.delete(timeEntryId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }
}
