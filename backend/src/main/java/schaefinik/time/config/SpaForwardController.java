package schaefinik.time.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping("/projects")
    public String forwardProjects() {
        return "forward:/index.html";
    }
}
