package schaefinik.time.user.controller;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import schaefinik.time.admin.controller.AdminUserController;
import schaefinik.time.user.service.UserService;

@WebMvcTest(AdminUserController.class)
public class AdminUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private UserService userService;

}