package com.shiftsync.notification_service.repository;

import com.shiftsync.notification_service.model.ActivityEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityEventRepository extends MongoRepository<ActivityEvent, String> {
    List<ActivityEvent> findByActorId(String actorId);
}
