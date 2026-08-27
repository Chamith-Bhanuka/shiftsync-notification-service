package com.shiftsync.notification_service.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri:mongodb+srv://cbhanukawp2002_db_user:SBLABJGXoRp6OIMA@shiftsync-notification.9pknogf.mongodb.net/shiftsync_notifications?retryWrites=true&w=majority&appName=shiftsync-notification}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "shiftsync_notifications");
    }
}
