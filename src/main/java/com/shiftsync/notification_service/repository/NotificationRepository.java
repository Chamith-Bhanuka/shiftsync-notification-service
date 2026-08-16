package com.shiftsync.notification_service.repository;

import com.shiftsync.notification_service.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndReadFalse(String userId);
}
