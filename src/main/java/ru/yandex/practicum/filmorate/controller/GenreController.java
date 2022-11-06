package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    GenreService genreService;
    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // GET /genres — получение списка жанров
    @GetMapping
    public List<Genre> findAll() {
        log.info("Получен запрос GET/genres - получение списка жанров");
        return genreService.findAll();
    }

    // GET /genres/{id} — поиск жанра по id
    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable(value = "id", required = false) Long id) {
        log.info("Получен запрос GET/genres/{id} - получение жанра по id");
        return genreService.findGenreById(id);
    }

}
