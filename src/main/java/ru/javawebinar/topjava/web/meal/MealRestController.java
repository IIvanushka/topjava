package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealWithExceed;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(MealRestController.REST_URL)
public class MealRestController extends AbstractMealController {

    static final String REST_URL = "/rest/meals";

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MealWithExceed> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Meal get(@PathVariable("id") int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") int id) {
        super.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Meal> createWithNewId(@RequestBody Meal meal) {
        Meal created = super.create(meal);

        URI uriOfCreated = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uriOfCreated).body(created);
    }

    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody Meal meal, @PathVariable("id") int id) {
        super.update(meal, id);
    }

    @GetMapping(value = "/filtered", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public List<MealWithExceed> getBetween(@RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "startTime", required = false) String startTime,
                                           @RequestParam(value = "endDate", required = false) String endDate,
                                           @RequestParam(value = "endTime", required = false) String endTime) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate startDateLD = LocalDate.MIN;
        LocalDate endDateLD = null;
        LocalTime startTimeLT = LocalTime.MIN;
        LocalTime endTimeLT = null;

        if (startDate != null) {
            startDateLD = LocalDate.parse(startDate, dateFormatter);
        }
        if (endDate != null) {
            endDateLD = LocalDate.parse(endDate, dateFormatter);
        }
        if (startTime != null) {
            startTimeLT = LocalTime.parse(startTime, timeFormatter);
        }
        if (endTime != null) {
            endTimeLT = LocalTime.parse(endTime, timeFormatter);
        }
        return super.getBetween(startDateLD, startTimeLT, endDateLD, endTimeLT);
    }
}