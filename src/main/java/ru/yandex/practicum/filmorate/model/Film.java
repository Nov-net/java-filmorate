package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;
import java.util.TreeSet;

@Data
public class Film {
    @NotEmpty
    @NotBlank
    String name;
    Long id;
    String description;
    LocalDate releaseDate;
    @Positive
    long duration;

    TreeSet<Long> likes;

    public Film(String name, Long id, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public boolean addLike(Long userId) {
        if (likes == null) {
            likes = new TreeSet<>();
            likes.add(userId);
            return true;
        } else if (!likes.contains(userId)){
            likes.add(userId);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteLike (Long userId) {
        if (likes.contains(userId)) {
            likes.remove(userId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return duration == film.duration && name.equals(film.name) && description.equals(film.description) && releaseDate.equals(film.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, releaseDate, duration);
    }

}
