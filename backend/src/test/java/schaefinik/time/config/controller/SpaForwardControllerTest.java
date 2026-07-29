package schaefinik.time.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SpaForwardController.class)
@EnableMethodSecurity
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class SpaForwardControllerTest {

	@Autowired
	private MockMvc mockMvc;

}
