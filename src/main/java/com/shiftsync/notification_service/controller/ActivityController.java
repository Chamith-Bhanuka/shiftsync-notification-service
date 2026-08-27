package com.shiftsync.notification_service.controller;

import com.shiftsync.notification_service.dto.ActivityEventRequest;
import com.shiftsync.notification_service.model.ActivityEvent;
import com.shiftsync.notification_service.model.Notification;
import com.shiftsync.notification_service.repository.ActivityEventRepository;
import com.shiftsync.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class ActivityController {
    private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
    private final ActivityEventRepository activityRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ActivityController(ActivityEventRepository activityRepository,
                              NotificationRepository notificationRepository,
                              SimpMessagingTemplate messagingTemplate) {
        this.activityRepository = activityRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ActivityEvent logEvent(@RequestBody ActivityEventRequest request) {
        ActivityEvent event = new ActivityEvent();
        event.setEventType(request.getEventType());
        event.setActorId(request.getActorId());
        event.setShiftId(request.getShiftId());
        event.setMetadata(request.getMetadata());
        event.setTimestamp(Instant.now());
        ActivityEvent savedEvent = activityRepository.save(event);

        // Auto-generate notifications based on event type
        try {
            Map<String, Object> meta = request.getMetadata();
            String eventType = request.getEventType();

            if ("SWAP_CREATED".equalsIgnoreCase(eventType) && meta != null) {
                String reqName = String.valueOf(meta.getOrDefault("requestingEmployeeName", "A colleague"));
                Object targetId = meta.get("targetEmployeeId");
                if (targetId != null && !"-1".equals(String.valueOf(targetId))) {
                    saveAndBroadcast(String.valueOf(targetId), reqName + " requested a shift swap with you (Shift #" + request.getShiftId() + ").");
                }
                saveAndBroadcast("1", reqName + " submitted a shift swap request (Shift #" + request.getShiftId() + ").");
            } else if ("SWAP_APPROVED".equalsIgnoreCase(eventType) && meta != null) {
                Object reqId = meta.get("requestingEmployeeId");
                Object targetId = meta.get("targetEmployeeId");
                String reqName = String.valueOf(meta.getOrDefault("requestingEmployeeName", "Staff"));
                if (reqId != null) {
                    saveAndBroadcast(String.valueOf(reqId), "Your shift swap request (Shift #" + request.getShiftId() + ") was APPROVED by manager.");
                }
                if (targetId != null && !"-1".equals(String.valueOf(targetId))) {
                    saveAndBroadcast(String.valueOf(targetId), "The shift swap with " + reqName + " (Shift #" + request.getShiftId() + ") was APPROVED by manager.");
                }
            } else if ("SWAP_REJECTED".equalsIgnoreCase(eventType) && meta != null) {
                Object reqId = meta.get("requestingEmployeeId");
                Object targetId = meta.get("targetEmployeeId");
                String reqName = String.valueOf(meta.getOrDefault("requestingEmployeeName", "Staff"));
                if (reqId != null) {
                    saveAndBroadcast(String.valueOf(reqId), "Your shift swap request (Shift #" + request.getShiftId() + ") was REJECTED by manager.");
                }
                if (targetId != null && !"-1".equals(String.valueOf(targetId))) {
                    saveAndBroadcast(String.valueOf(targetId), "The shift swap request with " + reqName + " (Shift #" + request.getShiftId() + ") was REJECTED by manager.");
                }
            } else if ("SWAP_RESPONSE".equalsIgnoreCase(eventType) && meta != null) {
                Object reqId = meta.get("requestingEmployeeId");
                String targetName = String.valueOf(meta.getOrDefault("targetEmployeeName", "Target staff"));
                boolean willing = Boolean.parseBoolean(String.valueOf(meta.getOrDefault("willingToCover", "false")));
                String statusText = willing ? "agreed to cover" : "declined";
                if (reqId != null) {
                    saveAndBroadcast(String.valueOf(reqId), targetName + " " + statusText + " your shift swap request (Shift #" + request.getShiftId() + ").");
                }
                saveAndBroadcast("1", targetName + " responded to swap request (" + (willing ? "Agreed" : "Declined") + ") — awaiting decision.");
            }

            messagingTemplate.convertAndSend("/topic/activity", savedEvent);
        } catch (Exception e) {
            log.warn("Error processing event notifications: {}", e.getMessage());
        }

        return savedEvent;
    }

    private void saveAndBroadcast(String userId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        n.setChannel(Notification.Channel.IN_APP);
        n.setRead(false);
        n.setCreatedAt(Instant.now());
        Notification saved = notificationRepository.save(n);
        try {
            messagingTemplate.convertAndSend("/topic/notifications/" + userId, saved);
        } catch (Exception e) {
            log.warn("Failed to broadcast to /topic/notifications/{}: {}", userId, e.getMessage());
        }
    }

    @GetMapping
    public List<ActivityEvent> getEvents(@RequestParam String userId) {
        return activityRepository.findByActorId(userId);
    }
}
