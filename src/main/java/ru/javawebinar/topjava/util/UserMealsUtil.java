package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GKislin
 * 31.05.2015.
 */
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);

        getFilteredWithExceededByStream(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);

//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExceed> userMealWithExceeds = new ArrayList<>();
        Map<LocalDate, Integer> daysCalories = new HashMap<>(); // Сумма калорий по дням
        LocalDate currentDate;
        LocalTime currentTime;
        List<UserMeal> filteredMealList = new ArrayList<>(); // Отфильтрованный mealList от startTime до endTime

        for (UserMeal userMeal : mealList) {
            currentDate = userMeal.getDateTime().toLocalDate();
            currentTime = userMeal.getDateTime().toLocalTime();

            if (TimeUtil.isBetween(currentTime, startTime, endTime)) {
                filteredMealList.add(userMeal);
            }

            daysCalories.merge(currentDate, userMeal.getCalories(), Integer::sum);
        }

        filteredMealList.forEach((filteredMeal) -> {
            if (daysCalories.get(filteredMeal.getDateTime().toLocalDate()) > caloriesPerDay) {
                userMealWithExceeds.add(new UserMealWithExceed(filteredMeal.getDateTime(), filteredMeal.getDescription(), filteredMeal.getCalories(), true));
            } else {
                userMealWithExceeds.add(new UserMealWithExceed(filteredMeal.getDateTime(), filteredMeal.getDescription(), filteredMeal.getCalories(), false));
            }
        });

        return userMealWithExceeds;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededByStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExceed> userMealWithExceeds = new ArrayList<>();
        Map<LocalDate, Integer> daysCalories; // Сумма калорий по дням
        List<UserMeal> filteredMealList = new ArrayList<>(); // Отфильтрованный mealList от startTime до endTime

        daysCalories = mealList.stream()
                .peek(userMeal -> {
                    if (TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                        filteredMealList.add(userMeal);
                    }
                })
                .collect(Collectors.toMap(
                        o -> o.getDateTime().toLocalDate(),
                        o -> o.getCalories(),
                        (cal1, cal2) -> cal1 + cal2));

        Map<LocalDate, Integer> finalDaysCalories = daysCalories;
        filteredMealList.forEach((filteredMeal) -> {
            if (finalDaysCalories.get(filteredMeal.getDateTime().toLocalDate()) > caloriesPerDay) {
                userMealWithExceeds.add(new UserMealWithExceed(filteredMeal.getDateTime(), filteredMeal.getDescription(), filteredMeal.getCalories(), true));
            } else {
                userMealWithExceeds.add(new UserMealWithExceed(filteredMeal.getDateTime(), filteredMeal.getDescription(), filteredMeal.getCalories(), false));
            }
        });

        return userMealWithExceeds;
    }
}