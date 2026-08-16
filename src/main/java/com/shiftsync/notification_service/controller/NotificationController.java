package com.shiftsync.notification_service.controller;

import com.shiftsync.notification_service.dto.NotificationRequest;
import com.shiftsync.notification_service.model.Notification;
import com.shiftsync.notification_service.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationRepository repository;

    public NotificationController(NotificationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Notification create(@RequestBody NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setMessage(request.getMessage());
        notification.setChannel(Notification.Channel.valueOf(request.getChannel()));
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        return repository.save(notification);
    }

    @GetMapping
    public List<Notification> getForUser(@RequestParam String userId) {
        return repository.findByUserId(userId);
    }

    @GetMapping("/unread")
    public List<Notification> getUnread(@RequestParam String userId) {
        return repository.findByUserIdAndReadFalse(userId);
    }

    @PutMapping("/{id}/read")
    public Notification markRead(@PathVariable String id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        notification.setRead(true);
        return repository.save(notification);
    }
}
