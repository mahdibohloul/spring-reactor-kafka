# Spring Reactor Kafka Samples

This module contains sample applications demonstrating how to use the Spring Reactor Kafka library.

## Overview

The sample application demonstrates:

- **Kafka Controllers**: Using `@KafkaController` and `@ReactiveKafkaListener` annotations
- **Configuration Providers**: Implementing `KafkaReceiverConfigurationProvider` for different message types
- **Message Processing**: Reactive processing of Kafka messages with proper error handling
- **REST API**: Endpoints to trigger message production for testing
- **Multiple Event Types**: User events, Order events, and Notification events

## Prerequisites

- Java 21+
- Apache Kafka running on localhost:9092 (or configure `kafka.bootstrap-servers` in application.properties)
- Gradle 8.0+

## Running the Application

1. **Start Kafka** (if not already running):
   ```bash
   # Using Docker
   docker run -d --name kafka -p 9092:9092 apache/kafka:latest
   
   # Or using your local Kafka installation
   bin/kafka-server-start.sh config/server.properties
   ```

2. **Create Kafka Topics** (optional - the application will work with auto-created topics):
   ```bash
   kafka-topics.sh --create --topic user-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
   kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
   kafka-topics.sh --create --topic notification-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
   ```

3. **Run the Application**:
   ```bash
   ./gradlew :spring-reactor-kafka-samples:bootRun
   ```

4. **Access the Application**:
    - Application: http://localhost:8080
    - Health Check: http://localhost:8080/api/samples/health
    - Actuator: http://localhost:8080/actuator

## API Endpoints

### Send Single Events

#### User Events

```bash
curl -X POST http://localhost:8080/api/samples/user-events \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "eventType": "USER_CREATED"
  }'
```

#### Order Events

```bash
curl -X POST http://localhost:8080/api/samples/order-events \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order123",
    "userId": "user123",
    "eventType": "ORDER_CREATED"
  }'
```

#### Notification Events

```bash
curl -X POST http://localhost:8080/api/samples/notification-events \
  -H "Content-Type: application/json" \
  -d '{
    "notificationId": "notification123",
    "userId": "user123",
    "type": "EMAIL"
  }'
```

### Send Bulk Events

#### Bulk User Events

```bash
curl -X POST http://localhost:8080/api/samples/bulk/user-events \
  -H "Content-Type: application/json" \
  -d '{
    "count": 10,
    "userIdPrefix": "bulk-user"
  }'
```

#### Bulk Order Events

```bash
curl -X POST http://localhost:8080/api/samples/bulk/order-events \
  -H "Content-Type: application/json" \
  -d '{
    "count": 5,
    "orderIdPrefix": "bulk-order",
    "userIdPrefix": "bulk-user"
  }'
```

#### Bulk Notification Events

```bash
curl -X POST http://localhost:8080/api/samples/bulk/notification-events \
  -H "Content-Type: application/json" \
  -d '{
    "count": 15,
    "notificationIdPrefix": "bulk-notification",
    "userIdPrefix": "bulk-user"
  }'
```

## Event Types

### User Events

- `USER_CREATED`
- `USER_UPDATED`
- `USER_DELETED`
- `USER_LOGIN`
- `USER_LOGOUT`

### Order Events

- `ORDER_CREATED`
- `ORDER_PAID`
- `ORDER_SHIPPED`
- `ORDER_DELIVERED`
- `ORDER_CANCELLED`
- `ORDER_REFUNDED`

### Notification Events

- `EMAIL`
- `SMS`
- `PUSH`
- `IN_APP`

## Configuration

The application can be configured via `application.properties`:

```properties
# Kafka Configuration
kafka.bootstrap-servers=localhost:9092
# Topic Configuration
kafka.user-events.topic=user-events
kafka.user-events.group-id=user-events-consumer-group
kafka.order-events.topic=order-events
kafka.order-events.group-id=order-events-consumer-group
kafka.notification-events.topic=notification-events
kafka.notification-events.group-id=notification-events-consumer-group
```

## Architecture

### Components

1. **Kafka Controllers**: Handle message consumption using reactive streams
2. **Configuration Providers**: Provide Kafka receiver configurations
3. **Producer Service**: Sends messages to Kafka topics
4. **REST Controller**: Provides HTTP endpoints for testing
5. **Data Models**: Define the structure of events

### Key Features

- **Reactive Processing**: Uses Reactor for non-blocking message processing
- **Error Handling**: Comprehensive error handling and logging
- **Type Safety**: Strongly typed event models
- **Configurable**: Easy configuration via properties
- **Monitoring**: Built-in health checks and metrics
- **Testing**: REST API for easy testing and demonstration

## Monitoring

The application includes:

- **Health Checks**: `/api/samples/health`
- **Actuator Endpoints**: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
- **Structured Logging**: JSON-formatted logs with correlation IDs
- **Consumer Metrics**: Kafka consumer lag and throughput metrics

## Development

### Building

```bash
./gradlew :spring-reactor-kafka-samples:build
```

### Testing

```bash
./gradlew :spring-reactor-kafka-samples:test
```

### Running Tests with Kafka

The application can be tested with an embedded Kafka instance or a local Kafka cluster.

## Troubleshooting

### Common Issues

1. **Kafka Connection Issues**: Ensure Kafka is running on the configured bootstrap servers
2. **Topic Not Found**: Topics are auto-created, but you can create them manually
3. **Consumer Group Issues**: Each consumer group processes messages independently
4. **Serialization Issues**: Ensure message formats match the expected models

### Logs

Check the application logs for detailed information about:

- Consumer startup and configuration
- Message processing
- Error handling
- Performance metrics

