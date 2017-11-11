package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealServiceImpl implements MealService {

    @Autowired
    private final MealRepository repository;

    @Autowired
    public MealServiceImpl(MealRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meal create(Meal meal) {
        return repository.save(meal);
    }

    @Override
    public void delete(int id) throws NotFoundException {
        checkNotFoundWithId(repository.delete(id), id);
    }

    @Override
    public Meal get(int id) throws NotFoundException {
        return checkNotFoundWithId(repository.get(id), id);
    }

    @Override
    public List<Meal> getByDates(LocalDateTime before, LocalDateTime after) throws NotFoundException {
        if (before == null) {
            before = LocalDateTime.MIN;
        }
        if (after == null) {
            after = LocalDateTime.MAX;
        }
        LocalDateTime finalBefore = before;
        LocalDateTime finalAfter = after;

        return getAll().stream()
                .filter((meal) -> DateTimeUtil.isBetween(meal.getDateTime(), finalBefore, finalAfter))
                .collect(Collectors.toList());
    }

    @Override
    public void update(Meal meal) {
        repository.save(meal);
    }

    @Override
    public List<Meal> getAll() {
        return repository.getAll();
    }
}