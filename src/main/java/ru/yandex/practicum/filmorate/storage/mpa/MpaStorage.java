package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    public List<Mpa> findAll();
    public Mpa findMpaById(Long id);
    public Mpa getMpa(Long id);
    public void addMpa(Film film);
    public void updateMpa(Film film);
}
