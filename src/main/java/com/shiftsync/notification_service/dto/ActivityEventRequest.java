package com.shiftsync.notification_service.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ActivityEventRequest {
    private String eventType;
    private String actorId;
    private String shiftId;
    private Map<String, Object> metadata;
}
