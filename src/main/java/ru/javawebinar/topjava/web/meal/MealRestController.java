package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealWithExceed;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Controller
public class MealRestController {

    private static final Logger log = getLogger(MealRestController.class);

    private MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealWithExceed> getAllMyMeals() {

        log.debug("Getting all authorized meals.");

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

        log.debug("Getting filtered meals.");

        return getAllMyMeals().stream()
                .filter((mealWithExceed -> DateTimeUtil.isBetween(mealWithExceed.getDateTime(), after, before)))
                .collect(Collectors.toList());
    }

    public Meal create(Meal meal) {
        checkNew(meal);
        log.debug("Creating meal " + meal);
        return service.create(meal);
    }

    public void delete(int id) throws NotFoundException {
        if (checkNotFoundWithId(service.get(id), id).getUserId() == AuthorizedUser.id()) {
            log.debug("Deleting meal with id " + id);
            service.delete(id);
        } else {
            log.debug("Trying to delete not Authorized id");
            throw new NotFoundException("Not valid id");
        }
    }

    public Meal get(int id) throws NotFoundException {
        if (service.get(id).getUserId() == AuthorizedUser.id()) {
            log.debug("Getting meal with id - " + id);
            return service.get(id);
        } else {
            log.debug("Trying to get not Authorized id");
            throw new NotFoundException("Not valid id");
        }

    }

    public void update(Meal meal) {
        if (service.get(meal.getUserId()).getUserId() == AuthorizedUser.id()) {
            log.debug("Updating meal - " + meal);
            service.update(meal);
        } else {
            log.debug("Trying to update not Authorized id");
            throw new NotFoundException("Not valid id");
        }
    }

    public void createOrUpdate(String id, LocalDateTime dateTime, String description, Integer calories) {
        Meal meal;
        if (id.isEmpty()) {
            meal = new Meal(AuthorizedUser.id(), dateTime, description, calories);
        } else {
            meal = new Meal(Integer.parseInt(id), AuthorizedUser.id(), dateTime, description, calories);
        }

        if (meal.isNew()) {
            create(meal);
        } else {
            update(meal);
        }
    }
}