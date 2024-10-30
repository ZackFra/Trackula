package com.trackula.track.controller;

import com.trackula.track.dto.CreateTimerEntryRequest;
import com.trackula.track.dto.UpdateTimerEntryRequest;
import com.trackula.track.model.TimerEntry;
import com.trackula.track.repository.TimerEntryCategoryRepository;
import com.trackula.track.repository.TimerEntryJdbcRepository;
import com.trackula.track.repository.TimerEntryRepository;
import com.trackula.track.service.AuthService;
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
    private final TimerEntryJdbcRepository timerEntryJdbcRepository;

    public TimerEntryController(TimerEntryRepository timerEntryRepository, TimerEntryCategoryRepository timerEntryCategoryRepository, AuthService authService, TimerEntryJdbcRepository timerEntryJdbcRepository) {
        this.timerEntryRepository = timerEntryRepository;
        this.timerEntryCategoryRepository = timerEntryCategoryRepository;
        this.authService = authService;
        this.timerEntryJdbcRepository = timerEntryJdbcRepository;
    }

    @GetMapping
    public ResponseEntity<List<TimerEntry>> getAllTimerEntries(@CurrentOwner String owner) {
        Iterable<TimerEntry> timerEntriesIterable;
        if(authService.isAdmin()) {
            timerEntriesIterable = timerEntryRepository.findAll();
        } else {
            timerEntriesIterable = timerEntryRepository.findAllByOwner(owner);
        }
        List<TimerEntry> timerEntries = StreamSupport.stream(timerEntriesIterable.spliterator(), false)
            .toList();
        return ResponseEntity.ok(timerEntries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimerEntry> getTimerEntryById(@PathVariable Long id, @CurrentOwner String owner) {
        Optional<TimerEntry> timerEntryOptional;
        if(authService.isAdmin()) {
            timerEntryOptional = timerEntryRepository.findById(id);
        } else {
            timerEntryOptional = timerEntryRepository.findByIdAndOwner(id, owner);
        }
        if(timerEntryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TimerEntry timerEntry = timerEntryOptional.get();
        return ResponseEntity.ok(timerEntry);
    }

    @PostMapping
    public ResponseEntity<Void> createTimerEntry(@RequestBody CreateTimerEntryRequest createTimerEntryRequest, @CurrentOwner String owner) {
        TimerEntry newTimerEntry = new TimerEntry(
                null,
                owner,
                createTimerEntryRequest.getTimeTracked()
        );
        TimerEntry createdTimerEntry = timerEntryJdbcRepository.save(newTimerEntry);
        URI uri = URI.create("/timer-entry/" + createdTimerEntry.id());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTimerEntry(@PathVariable Long id, @RequestBody UpdateTimerEntryRequest updateTimerEntryRequest, Principal principal) {
        Optional<TimerEntry> existingTimerEntryOptional;
        boolean isAdmin = authService.isAdmin();
        if(isAdmin) {
            existingTimerEntryOptional = timerEntryRepository.findById(id);
        } else {
            existingTimerEntryOptional = timerEntryRepository.findByIdAndOwner(id, principal.getName());
        }

        if(existingTimerEntryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TimerEntry existingTimerEntry = existingTimerEntryOptional.get();

        String owner = existingTimerEntry.owner();
        Long timeTracked = existingTimerEntry.timeTracked();
        if(updateTimerEntryRequest.getOwner() != null){
            owner = updateTimerEntryRequest.getOwner();
        }

        if(updateTimerEntryRequest.getTimeTracked() != null) {
            timeTracked = updateTimerEntryRequest.getTimeTracked();
        }

        TimerEntry updatedTimerEntry = new TimerEntry(
            existingTimerEntry.id(),
            owner,
            timeTracked
        );
        timerEntryRepository.save(updatedTimerEntry);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTimerEntry(@PathVariable Long id, @CurrentOwner String owner) {
        boolean isInDatabase;
        if(authService.isAdmin()) {
            isInDatabase = timerEntryRepository.existsById(id);
        } else {
            isInDatabase = timerEntryRepository.existsByIdAndOwner(id, owner);
        }
        if(!isInDatabase) {
            return ResponseEntity.notFound().build();
        }
        timerEntryCategoryRepository.deleteByTimerEntryId(id);
        timerEntryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
