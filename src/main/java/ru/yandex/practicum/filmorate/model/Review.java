package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "reviewId")
public class Review {
    private Integer reviewId;
    @NotBlank
    private String content;
    @NotNull
    @JsonProperty("isPositive")
    private Boolean isPositive;
    @NotNull
    private Integer filmId;
    @NotNull
    private Integer userId;
    private int useful;
}
