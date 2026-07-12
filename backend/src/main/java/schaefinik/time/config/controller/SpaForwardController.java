package schaefinik.time.config.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({ "/projects", "/time-entries", "/login" })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}
