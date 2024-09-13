package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "eventId")
public class Feed {
    private Integer eventId;
    private Integer userId;
    private Integer entityId;
    private Long timestamp;
    private EventType eventType;
    private Operation operation;

    public Feed(Integer userId, Integer entityId, EventType eventType, Operation operation) {
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
        timestamp = System.currentTimeMillis();
    }

    public enum EventType { LIKE, REVIEW, FRIEND }

    public enum Operation { REMOVE, ADD, UPDATE }
}
