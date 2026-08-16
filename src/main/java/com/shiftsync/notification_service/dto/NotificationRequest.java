package com.shiftsync.notification_service.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String userId;
    private String message;
    private String channel; // "IN_APP" or "EMAIL"
}
