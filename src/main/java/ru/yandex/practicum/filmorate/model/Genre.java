package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    Long id;
    String name;

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
