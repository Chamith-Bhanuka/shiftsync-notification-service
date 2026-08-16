package com.shiftsync.notification_service.controller;

import com.shiftsync.notification_service.dto.ActivityEventRequest;
import com.shiftsync.notification_service.model.ActivityEvent;
import com.shiftsync.notification_service.repository.ActivityEventRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/events")
public class ActivityController {
    private final ActivityEventRepository repository;

    public ActivityController(ActivityEventRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ActivityEvent logEvent(@RequestBody ActivityEventRequest request) {
        ActivityEvent event = new ActivityEvent();
        event.setEventType(request.getEventType());
        event.setActorId(request.getActorId());
        event.setShiftId(request.getShiftId());
        event.setMetadata(request.getMetadata());
        event.setTimestamp(Instant.now());
        return repository.save(event);
    }

    @GetMapping
    public List<ActivityEvent> getEvents(@RequestParam String userId) {
        return repository.findByActorId(userId);
    }
}
