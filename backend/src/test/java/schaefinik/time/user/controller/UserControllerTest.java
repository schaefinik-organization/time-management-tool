package schaefinik.time.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import schaefinik.time.auth.controller.AuthController;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.user.requestData.AccountRequest;
import schaefinik.time.user.requestData.UserRequest;
import schaefinik.time.user.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private UserService userService;

}
