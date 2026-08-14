package schaefinik.time.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

	@GetMapping({"/time-entries", "/login", "/manager/**", "/admin/**"})
	public String forwardSpaRoutes() {
		return "forward:/index.html";
	}
}
