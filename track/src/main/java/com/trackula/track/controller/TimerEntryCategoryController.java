package com.trackula.track.controller;

import com.trackula.track.dto.CreateTimerEntryCategoryRequest;
import com.trackula.track.model.TimerEntryCategory;
import com.trackula.track.repository.TimerEntryCategoryJdbcRepository;
import com.trackula.track.repository.TimerEntryCategoryRepository;
import com.trackula.track.repository.TimerEntryRepository;
import com.trackula.track.service.AuthService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/timer-entry-category")
public class TimerEntryCategoryController {

    private final TimerEntryCategoryJdbcRepository timerEntryCategoryJdbcRepository;
    private final TimerEntryCategoryRepository timerEntryCategoryRepository;
    private final TimerEntryRepository timerEntryRepository;
    private final AuthService authService;

    public TimerEntryCategoryController(TimerEntryCategoryJdbcRepository timerEntryCategoryJdbcRepository, TimerEntryCategoryRepository timerEntryCategoryRepository, TimerEntryRepository timerEntryRepository, AuthService authService) {
        this.timerEntryCategoryJdbcRepository = timerEntryCategoryJdbcRepository;
        this.timerEntryCategoryRepository = timerEntryCategoryRepository;
        this.timerEntryRepository = timerEntryRepository;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<Void> createTimerEntryCategory(@RequestBody CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest, @CurrentOwner String owner) {
        Long categoryId = createTimerEntryCategoryRequest.getCategoryId();
        Long timerEntryId = createTimerEntryCategoryRequest.getTimerEntryId();
        if(categoryId == null || timerEntryId == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean isInDatabaseAlready = timerEntryCategoryRepository.existsByCategoryIdAndTimerEntryId(categoryId, timerEntryId);
        if(isInDatabaseAlready) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        TimerEntryCategory newTimerEntryCategory = new TimerEntryCategory(
                null,
                createTimerEntryCategoryRequest.getTimerEntryId(),
                createTimerEntryCategoryRequest.getCategoryId(),
                owner
        );
        TimerEntryCategory createdTimerEntryCategory = timerEntryCategoryJdbcRepository.save(newTimerEntryCategory);
        URI uri = URI.create("/timer-entry-category/" + createdTimerEntryCategory.id());
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{timerEntryId}")
    public ResponseEntity<List<TimerEntryCategory>> getTimerEntryCategoriesByTimerEntryId(@PathVariable Long timerEntryId, @CurrentOwner String owner) {
        boolean isTimerEntryInDatabase;
        if (authService.isAdmin()) {
            isTimerEntryInDatabase = timerEntryRepository.existsById(timerEntryId);
        } else {
            isTimerEntryInDatabase = timerEntryRepository.existsByIdAndOwner(timerEntryId, owner);
        }

        if (!isTimerEntryInDatabase) {
            return ResponseEntity.notFound().build();
        }

        Iterable<TimerEntryCategory> timerEntryCategoriesIterable = timerEntryCategoryRepository.findAllByTimerEntryId(timerEntryId);
        List<TimerEntryCategory> timerEntryCategories = StreamSupport.stream(timerEntryCategoriesIterable.spliterator(), false).toList();
        return ResponseEntity.ok(timerEntryCategories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimerEntryCategory(@PathVariable Long id, @CurrentOwner String owner) {
        boolean isInDatabase = false;
        if(authService.isAdmin()) {
            isInDatabase = timerEntryCategoryRepository.existsById(id);
        } else {
            isInDatabase = timerEntryCategoryRepository.existsByIdAndOwner(id, owner);
        }

        if(!isInDatabase) {
            return ResponseEntity.notFound().build();
        }

        timerEntryCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
