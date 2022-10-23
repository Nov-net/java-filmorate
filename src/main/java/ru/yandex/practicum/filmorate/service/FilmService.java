package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private final LocalDate oldDate = LocalDate.of(1895, 12, 28);

    public Film create(Film film) {

        if(filmStorage.getListFilmsId().contains(film.getId())) {
            log.info("Попытка добавить фильм с уже существующим id");
            throw new FilmAlreadyExistException(String.format("id %d уже существует", film.getId()));
            // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("id %d уже существует", film.getId()));
        }

        if(filmStorage.findAll().contains(film)) {
            log.info("Попытка добавить уже существующий фильм");
            throw new FilmAlreadyExistException("Фильм уже существует");
        }

        if(film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.info("Попытка добавить фильм без названия");
            throw new InvalidNameException("Отсутствует название фильма");
        }

        if(film.getDescription().length() > 200) {
            log.info("Попытка добавить фильм с описанием более 200 символов");
            throw new InvalidDescriptionException("Описание фильма не может быть длиннее 200 символов");
        }

        if(film.getReleaseDate().isBefore(oldDate)) {
            log.info("Попытка добавить фильм с датой релиза ранее 1895-12-28");
            throw new InvalidReleaseDateException("Дата релиза фильма ранее 1895-12-28");
        }

        if(film.getDuration() <= 0) {
            log.info("Попытка добавить фильм продолжительностью <= 0");
            throw new InvalidDurationException(String.format(
                    "Некорректная продолжительность фильма %d",
                    film.getDuration()));
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if(film.getId() == null || !filmStorage.getListFilmsId().contains(film.getId())) {
            log.info("Попытка обновить фильм с несуществующим или пустым id: {}", film.getId());
            throw new InvalidIdException(String.format("Пустой или несуществующий id: %d", film.getId()));
        }
        if(film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.info("Попытка обновить фильм без указания названия, id фильма: {}", film.getId());
            throw new InvalidNameException("Отсутствует название фильма");
        }
        if(film.getDescription().length() > 200) {
            log.info("Попытка добавить фильм с описанием более 200 символов");
            throw new InvalidDescriptionException("Описание фильма не может быть длиннее 200 символов");
        }
        if(film.getReleaseDate().isBefore(oldDate)) {
            log.info("Попытка обновить фильм с датой релиза ранее 1895-12-28, id фильма: {}", film.getId());
            throw new InvalidReleaseDateException("Дата релиза фильма ранее 1895-12-28");
        }
        if(film.getDuration() <= 0) {
            log.info("Попытка обновить фильм с продолжительностью <= 0: {}", film.getDuration());
            throw new InvalidDurationException(String.format(
                    "Некорректная продолжительность фильма %d",
                    film.getDuration()));
        }

        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    // поиск фильма по id
    public Film findFilmById(Long id) {
        log.info("Получение фильма по id {} ", id);
        if(id == null || !filmStorage.getListFilmsId().contains(id)) {
            log.info("Попытка получить фильм с несуществующим или пустым id: {}", id);
            throw new InvalidIdException(String.format("Пустой или несуществующий id: %d", id));
        }

        return filmStorage.findAll().stream()
                    .filter(f -> f.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден", id)));
    }

    // поиск пользователя по id
    public User findUserById(Long id) {
        log.info("Получение пользователя по id {} ", id);
        return userStorage.findAll().stream()
                          .filter(u -> u.getId().equals(id))
                          .findFirst()
                          .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", id)));
    }

    // поставить лайк фильму
    public String addLike(Long id, Long userId) {
        checkId(id, userId);
        if(userStorage.getListUsersId().contains(userId)) {
            if (filmStorage.getListFilmsId().contains(id)) {
                findFilmById(id).addLike(userId);
                log.info("Фильму с id {} поставлен лайк пользователем {}", id, userId);
                return String.format("Фильму с id %d поставлен лайк пользователем с id %d", id, userId);
            } else {
                log.info("Попытка поставить лайк фильму с несуществующим id {}", id);
                throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));

            }
        } else {
            log.info("Попытка поставить лайк от пользователя с несуществующим id {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }

    // удалить лайк
    public String deleteLike(Long id, Long userId) {
        checkId(id, userId);
        if(userStorage.getListUsersId().contains(userId)) {
            if (filmStorage.getListFilmsId().contains(id)) {
                findFilmById(id).deleteLike(userId);
                log.info("У фильма с id {} удален лайк пользователем {}", id, userId);
                return String.format("У фильма с id %d удален лайк пользователем с id %d", id, userId);
            } else {
                log.info("Попытка удалить лайк у фильма с несуществующим id {}", id);
                throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
            }
        } else {
            log.info("Попытка удалить лайк пользователя с несуществующим id {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    private void checkId(Long id, Long userId) {
        if (id == null || id < 1) {
            log.info("Фильм с пустым или отрицательным id {}");
            throw new InvalidIdException("Фильм с пустым или отрицательным id");
        }

        if (userId == null || userId < 1) {
            log.info("Пользователь с пустым или отрицательным id {}");
            throw new InvalidIdException("Пользователь с пустым или отрицательным id");
        }
    }

    // список из первых {count} фильмов по количеству лайков
    public List<Film> findPopularFilms(Integer count) {
        if (count <= 0) {
            throw new IncorrectCountException("count");
        }

        if (findAll() != null) {
            return findAll().stream()
                    .sorted((f0, f1) -> compare(f0, f1))
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return null;
        }

    }

    private int compare(Film f0, Film f1) {
        int a = 0;
        int b = 0;
        if(f1.getLikes() != null) {
                a = f1.getLikes()
                           .size();
        } else if (f0.getLikes() != null) {
            b =  f0.getLikes()
                     .size();
        }
        return a - b;
    }

}
