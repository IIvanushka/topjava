package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class ResourceController {
    private static final Logger log = getLogger(ResourceController.class);

    @RequestMapping(value = "/style.css", method = RequestMethod.GET)
    public ResponseEntity<Void> style(HttpServletResponse response) throws Exception {
        response.setContentType("text/css");

//        File path = new File( .getServletContext().getRealPath("/style.css"));

//        Files.copy(path.toPath(), response.getOutputStream());

        response.flushBuffer();

        return new ResponseEntity<Void>(HttpStatus.OK);

    }

}
