package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealWithExceed;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MealRestController {

    @Autowired
    private MealService service;

    public List<MealWithExceed> getAllMyMeals() {

        return MealsUtil.getWithExceeded(service.getAll()
                .stream()
                .filter((meal -> meal.getUserId() == AuthorizedUser.id()))
                .collect(Collectors.toList()), AuthorizedUser.getCaloriesPerDay());
    }

    public List<MealWithExceed> getDateTimeFilteredMeals(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        if (startDate == null) {
            startDate = LocalDate.MIN;
        }
        if (startTime == null) {
            startTime = LocalTime.MIN;
        }
        if (endDate == null) {
            endDate = LocalDate.MAX;
        }
        if (endTime == null) {
            endTime = LocalTime.MAX;
        }
        LocalDateTime after = LocalDateTime.of(startDate, startTime);
        LocalDateTime before = LocalDateTime.of(endDate, endTime);

        return getAllMyMeals().stream()
                .filter((mealWithExceed -> DateTimeUtil.isBetween(mealWithExceed.getDateTime(),after,before)))
                .collect(Collectors.toList());
    }

}