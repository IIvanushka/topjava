package ru.javawebinar.topjava.web;

import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class ResourceControllerTest extends AbstractControllerTest {

    @Test
    public void testStyle() throws Exception {
        mockMvc.perform(get("/meals"))
                .andDo(print());
//                .andExpect(status())
    }
}
