package com.shiftsync.notification_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "activity_events")
@Getter
@Setter
public class ActivityEvent {
    @Id
    private String id;

    private String eventType;
    private String actorId;
    private String shiftId;
    private Instant timestamp;
    private Map<String, Object> metadata;

}
