package schaefinik.time.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import schaefinik.time.auth.controller.AuthController;

@WebMvcTest(SpaForwardController.class)
public class SpaForwardControllerTest {

    @Autowired
    private MockMvc mockMvc;

}
