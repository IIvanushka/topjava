package ru.javawebinar.topjava.web.meal;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.TestUtil;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;

import static java.time.LocalDateTime.of;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.*;

public class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL;

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonUtil.writeIgnoreProps(MEALS)));
    }

    @Test
    public void testGet() throws Exception {
        mockMvc.perform(get(REST_URL + "/100007"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonUtil.writeIgnoreProps(MEAL6)));
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(delete(REST_URL + "/100007"))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonUtil.writeIgnoreProps(Arrays.asList(MEAL5, MEAL4, MEAL3, MEAL2, MEAL1))));
    }

    @Test
    public void testCreate() throws Exception {
        Meal expected = new Meal(of(2017, Month.MAY, 30, 10, 0), "Завтрак Новый", 1000);

        ResultActions actions = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(expected)))
                .andExpect(status().isCreated());

        Meal returned = TestUtil.readFromJson(actions, Meal.class);
        returned.setId(expected.getId());
        MealTestData.assertMatch(expected, returned);
    }

    @Test
    public void testUpdate() throws Exception {
        Meal updated = new Meal(MEAL1);
        updated.setDescription("Updated desc");
        updated.setCalories(1000);
        updated.setDateTime(of(2017, Month.JANUARY, 25, 12, 0));

        mockMvc.perform(put(REST_URL + "/" + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isOk());

        ResultActions action = mockMvc.perform(get(REST_URL + "/" + MEAL1_ID))
                .andDo(print());

        Meal returned = TestUtil.readFromJson(action, Meal.class);
        returned.setId(MEAL1_ID);
        MealTestData.assertMatch(updated, returned);
    }

    @Test
    public void testGetBetween() throws Exception {
        LocalDate startD = LocalDate.of(2015, Month.MAY, 31);
        LocalDate endD = null; //= LocalDate.of(2015, Month.MAY, 31);
        LocalTime startT = LocalTime.of(12, 00);
        LocalTime endT = null; // = LocalTime.of(20, 23);
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("startDate", startD.toString());
        valueMap.add("startTime", startT.toString());
        valueMap.add("endDate", null);
//        valueMap.add("endTime", null);

        mockMvc.perform(get(REST_URL + "/filtered")
                .contentType(MediaType.APPLICATION_JSON)
                .params(valueMap))
                .andDo(print())
                .andExpect(status().isOk());
    }
}