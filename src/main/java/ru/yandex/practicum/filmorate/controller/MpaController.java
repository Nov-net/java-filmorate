package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    MpaService mpaService;
    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    // GET /mpa — получение списка mpa
    @GetMapping
    public List<Mpa> findAll() {
        log.info("Получен запрос GET/mpa - получение списка mpa");
        return mpaService.findAll();
    }

    // GET /mpa/{id} — поиск mpa по id
    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable(value = "id", required = false) Long id) {
        log.info("Получен запрос GET/mpa/{id} - получение mpa по id");
        return mpaService.findMpaById(id);
    }

}
