package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "reviewId")
public class Review {
    private Integer reviewId;
    private String content;
    private boolean isPositive;
    private int filmId;
    private int userId;
    private int useful;
}
