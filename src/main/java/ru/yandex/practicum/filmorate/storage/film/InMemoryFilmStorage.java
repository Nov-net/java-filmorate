package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage  {

    private final Map<Long, Film> films = new HashMap<>();
    private final List <Long> listFilmsId = new ArrayList<>();
    private long filmId = 0;

    @Override
    public Film create(Film film) {
        filmId++;
        film.setId(filmId);
        listFilmsId.add(filmId);
        log.info("Установлен id фильма: {}", filmId);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм обновлен: {}", film);

        return film;
    }

    @Override
    public List<Film> findAll() {
        log.info("Запрос на получение списка фильмов");
        List <Film> list = new ArrayList<>();
        for(Film f : films.values()) {
            list.add(f);
        }
        return list;
    }

    @Override
    public ArrayList<Long> getListFilmsId() {
        return new ArrayList<>(listFilmsId);
    }

    // получение списка фильмов для теста (Спринт 9)
    public Map<Long, Film> findAllForTest() {
        return films;
    }
}
