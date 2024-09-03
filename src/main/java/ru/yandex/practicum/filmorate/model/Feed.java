package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "eventId")
public class Feed {
    private Integer eventId;
    private int userId;
    private int entityId;
    private int timestamp;
    private String eventType;
    private String operation;
}
