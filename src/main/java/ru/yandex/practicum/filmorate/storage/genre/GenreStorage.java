package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    public List<Genre> findAll();
    public Genre findGenreById(Long id);
    public List<Genre> getGenre(Long id);
    public void addGenre(Film film);
    public void updateGenre(Film film);

}
