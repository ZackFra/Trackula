package com.trackula.track.controller;

import com.trackula.track.model.TimerEntry;
import com.trackula.track.repository.TimerEntryCategoryRepository;
import com.trackula.track.repository.TimerEntryRepository;
import com.trackula.track.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/timer-entry")
public class TimerEntryController {
    private final TimerEntryRepository timerEntryRepository;
    private final TimerEntryCategoryRepository timerEntryCategoryRepository;
    private final AuthService authService;
    public TimerEntryController(TimerEntryRepository timerEntryRepository, TimerEntryCategoryRepository timerEntryCategoryRepository, AuthService authService) {
        this.timerEntryRepository = timerEntryRepository;
        this.timerEntryCategoryRepository = timerEntryCategoryRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<TimerEntry>> getAllTimerEntries(Principal principal) {
        Iterable<TimerEntry> timerEntriesIterable;
        if(authService.isAdmin()) {
            timerEntriesIterable = timerEntryRepository.findAll();
        } else {
            timerEntriesIterable = timerEntryRepository.findAllByOwner(principal.getName());
        }
        List<TimerEntry> timerEntries = StreamSupport.stream(timerEntriesIterable.spliterator(), false)
            .toList();
        return ResponseEntity.ok(timerEntries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimerEntry> getTimerEntryById(@PathVariable Long id, Principal principal) {
        Optional<TimerEntry> timerEntryOptional;
        if(authService.isAdmin()) {
            timerEntryOptional = timerEntryRepository.findById(id);
        } else {
            timerEntryOptional = timerEntryRepository.findByIdAndOwner(id, principal.getName());
        }
        if(timerEntryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TimerEntry timerEntry = timerEntryOptional.get();
        return ResponseEntity.ok(timerEntry);
    }

    @PostMapping
    public ResponseEntity<Void> createTimerEntry(@RequestBody TimerEntry timerEntry, Principal principal) {
        if(timerEntry.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        if(timerEntry.timeTracked() == null) {
            return ResponseEntity.badRequest().build();
        }
        if(timerEntry.owner() != null) {
            return ResponseEntity.badRequest().build();
        }

        TimerEntry newTimerEntry = new TimerEntry(
                null,
                principal.getName(),
                timerEntry.timeTracked()
        );
        TimerEntry createdTimerEntry = timerEntryRepository.save(newTimerEntry);
        URI uri = URI.create("/timer-entry/" + createdTimerEntry.id());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTimerEntry(@PathVariable Long id,
                                                 @RequestBody
                                                 TimerEntry timerEntry, Principal principal) {
        if(timerEntry.id() != null && !id.equals(timerEntry.id())) {
            return ResponseEntity.badRequest().build();
        }
        Optional<TimerEntry> existingTimerEntryOptional = timerEntryRepository.findByIdAndOwner(id, principal.getName());
        if(existingTimerEntryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TimerEntry existingTimerEntry = existingTimerEntryOptional.get();
        if(existingTimerEntry.timeTracked().equals(timerEntry.timeTracked())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        TimerEntry updatedTimerEntry = new TimerEntry(
            existingTimerEntry.id(),
            existingTimerEntry.owner(),
            timerEntry.timeTracked()
        );
        timerEntryRepository.save(updatedTimerEntry);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTimerEntry(@PathVariable Long id, Principal principal) {
        boolean isInDatabase;
        if(authService.isAdmin()) {
            isInDatabase = timerEntryRepository.existsById(id);
        } else {
            isInDatabase = timerEntryRepository.existsByIdAndOwner(id, principal.getName());
        }
        if(!isInDatabase) {
            return ResponseEntity.notFound().build();
        }
        timerEntryCategoryRepository.deleteByTimerEntryId(id);
        timerEntryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
