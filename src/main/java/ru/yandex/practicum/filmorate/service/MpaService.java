package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    // получение списка mpa
    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    // поиск mpa по id
    public Mpa findMpaById(Long id) {
        if (mpaStorage.findMpaById(id) != null) {
            return mpaStorage.findMpaById(id);
        } else {
            throw new NotFoundException(String.format("Mpa с id %d не найден", id));
        }
    }
}
