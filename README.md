# ShiftSync — Notification Service

**Student Name:** <YOUR FULL NAME>
**Student Number:** <YOUR STUDENT ID>
**Slack Handle:** <YOUR SLACK HANDLE — optional>
**GCP Project ID:** <YOUR GCP PROJECT ID>

---

## Description

The Notification Service is one of three microservices in the ShiftSync platform. It stores two kinds of data: a log of **activity events** (things that happened in the system, e.g. a swap being requested) and **notifications** aimed at specific users.

It is backed by **MongoDB** rather than a relational database because these documents don't share one fixed shape — different event types carry different `metadata` fields, and forcing this into rigid relational tables would mean either an overly-normalized schema or a lot of unused/nullable columns. This service registers itself with Eureka and is intended to be called by other services (e.g. Scheduling Service, when a swap is approved) as well as directly by the frontend through the API Gateway.

---

## Technology Stack

- Java 25
- Spring Boot 3.x
- Spring Data MongoDB
- MongoDB (local or Atlas)
- Spring Cloud Config Client
- Netflix Eureka Client
- Maven

---

## Architecture Role

```
Frontend / Other Microservices → API Gateway → Notification Service → MongoDB
                                                       ↑
                                                 Eureka (service discovery)
                                                       ↑
                                                 Config Server (settings)
```

---

## Setup / Getting Started

### Prerequisites
- Java 25 installed
- Maven installed
- MongoDB running locally, or a MongoDB Atlas connection string
- Config Server and Eureka Server already running

### 1. Prepare MongoDB
Either start local MongoDB:
```bash
mongod --dbpath ~/mongodb-data
```
or use a free MongoDB Atlas cluster and note its connection string. No manual database/collection creation is needed — MongoDB creates them automatically on first write.

### 2. Confirm the Config Server has this service's settings
`config-server/config-repo/notification-service.yml` must exist with the correct `spring.data.mongodb.uri` for your environment, and the Config Server must be restarted after adding it.

### 3. Start dependencies first, in this order
1. `config-server` (port 8888)
2. `eureka-server` (port 8761)

### 4. Run this service
```bash
mvn spring-boot:run
```
The service starts on **port 8082** and registers itself with Eureka as `NOTIFICATION-SERVICE`.

### 5. Verify it's running
```bash
curl "http://localhost:8082/events?userId=1"
```
A `200 OK` with an empty array `[]` (on a fresh database) confirms the service is up and connected to MongoDB correctly.

---

## API Reference

All endpoints below are called directly on port 8082 for local testing. In the full system, the frontend reaches these through the API Gateway at `http://localhost:8080/api/notifications/...`.

### Activity Events
| Method | Path | Description |
|---|---|---|
| POST | `/events` | Log an activity event |
| GET | `/events?userId=` | List activity events for a given actor |

### Notifications
| Method | Path | Description |
|---|---|---|
| POST | `/notifications` | Create a notification for a user |
| GET | `/notifications?userId=` | List all notifications for a user |
| GET | `/notifications/unread?userId=` | List only unread notifications for a user |
| PUT | `/notifications/{id}/read` | Mark a notification as read |

### Example request bodies

**Log an activity event:**
```json
POST /events
{
  "eventType": "SWAP_REQUESTED",
  "actorId": "1",
  "shiftId": "1",
  "metadata": { "targetEmployeeId": "2", "note": "cant make it, appointment" }
}
```

**Create a notification:**
```json
POST /notifications
{
  "userId": "2",
  "message": "Alice requested to swap her shift with you.",
  "channel": "IN_APP"
}
```

---

## Data Model

| Document | Collection | Key Fields |
|---|---|---|
| ActivityEvent | `activity_events` | id (String), eventType, actorId, shiftId, timestamp, metadata (flexible map) |
| Notification | `notifications` | id (String), userId, message, channel (`IN_APP`/`EMAIL`), read (boolean), createdAt |

**Note:** IDs in this service are Strings (MongoDB's default generated ObjectId format), unlike Scheduling Service's PostgreSQL-backed Long IDs — this is an intentional, visible difference between the two database types used in this project.

---

## Local Testing

A full end-to-end curl test sequence (log differently-shaped activity events, create a notification, filter unread, mark read, confirm via MongoDB directly) is documented separately and was used to verify this service before any cloud deployment work began.
