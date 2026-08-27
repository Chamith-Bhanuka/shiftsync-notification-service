package com.shiftsync.notification_service.controller;

import com.shiftsync.notification_service.dto.NotificationRequest;
import com.shiftsync.notification_service.model.Notification;
import com.shiftsync.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(NotificationRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public Notification create(@RequestBody NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setMessage(request.getMessage());
        notification.setChannel(Notification.Channel.valueOf(request.getChannel()));
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        Notification saved = repository.save(notification);

        // Broadcast to WebSocket subscribers for real-time live push
        try {
            messagingTemplate.convertAndSend("/topic/notifications/" + saved.getUserId(), saved);
            messagingTemplate.convertAndSend("/topic/activity", saved);
        } catch (Exception e) {
            log.warn("Failed to broadcast WebSocket notification: {}", e.getMessage());
        }

        return saved;
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
