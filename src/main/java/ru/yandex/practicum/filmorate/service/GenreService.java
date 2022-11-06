package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    // получение списка жанров
    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    // поиск жанра по id
    public Genre findGenreById(Long id) {
        if (genreStorage.findGenreById(id) != null) {
            return genreStorage.findGenreById(id);
        } else {
            throw new NotFoundException(String.format("Жанр с id %d не найден", id));
        }
    }
}
