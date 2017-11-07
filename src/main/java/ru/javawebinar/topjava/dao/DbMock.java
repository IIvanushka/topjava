package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

public class DbMock {

    private static final Logger log = getLogger(DbMock.class);

    private Map<Long, MealWithExceed> map = new ConcurrentHashMap<>();

    private static DbMock instance;

    private DbMock() {
        putHardCodeData();
    }

    public static synchronized DbMock getInstance() {
        if (instance == null) {
            instance = new DbMock();
        }
        return instance;
    }

    public MealWithExceed getMealbyId(Long id){
        return map.get(id);
    }

    public void addMeal(MealWithExceed meal){
        map.put(meal.getId(),meal);
    }

    public void deleteMealbyId(Long id){
        map.remove(id);
    }

    public List<MealWithExceed> getAllMeals(){
        List<MealWithExceed> result = new ArrayList<>();
        map.forEach((key, value) -> {
            result.add(value);
        });
        return result;
    }

    private void putHardCodeData() {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        List<MealWithExceed> mealsWE = MealsUtil.getFilteredWithExceeded(meals, LocalTime.of(0, 0), LocalTime.of(23, 59), 2000);

        mealsWE.forEach(meal -> {
            addMeal(meal);
            log.debug("Adding to DB "+meal);
        });
    }
}
