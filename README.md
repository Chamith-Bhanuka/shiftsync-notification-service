# ShiftSync — Notification & Real-Time Event Service

**Student Name:** Chamith Bhanuka Widanapathirana  
**Student ID / Number:** 241711051  
**Slack Handle:** Chamith Bhanuka  
**GCP Project ID:** project-a58ee7a4-4913-4af2-a6d  
**Course:** ITS 2130 — Enterprise Cloud Architecture  

---

## Description

The Notification Service is one of three core domain microservices in the ShiftSync platform. Backed by MongoDB Atlas, it manages flexible activity event auditing and targeted employee notifications, with real-time push delivery over WebSocket / STOMP.

---

## Key Features

- **MongoDB Atlas Integration**: Stores flexible polymorphic activity events (`activity_events`) and user notifications (`notifications`) in a managed MongoDB Atlas cloud cluster (`shiftsync-notification.9pknogf.mongodb.net`).
- **Real-Time WebSocket & STOMP Push**: Full STOMP messaging over WebSocket (`/ws`) delivering live push notifications directly to user destination topics (`/topic/notifications/{userId}`) and system-wide broadcast topics (`/topic/activity`).
- **Read / Unread State Management**: Tracks notification statuses, provides unread count feeds, and supports individual and batch mark-as-read operations.

---

## Technology Stack

- Java 25
- Spring Boot 3.x
- Spring Data MongoDB
- Spring WebSocket & STOMP Messaging
- MongoDB Atlas
- Spring Cloud Netflix Eureka Client
- Spring Cloud Config Client
- Maven
