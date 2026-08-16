package com.shiftsync.notification_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@Getter
@Setter
public class Notification {
    public enum Channel { IN_APP, EMAIL }

    @Id
    private String id;

    private String userId;
    private String message;
    private Channel channel;
    private boolean read;
    private Instant createdAt;

}
