package schaefinik.time.auth.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import schaefinik.time.auth.service.AuthService;
import schaefinik.time.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private AuthService service;

  @Mock
  private UserService userService;

  @Test
  void test_login() throws Exception {
    mockMvc.perform(post("/api/auth/login"))
            .andExpect(status().isOk());
  }

  @Test
  void test_me() throws Exception {
    mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isOk());
  }
}
