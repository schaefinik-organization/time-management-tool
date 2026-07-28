package schaefinik.time.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.security.service.CustomUserDetailsService;
import schaefinik.time.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService service;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void test_login() throws Exception {
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("{\"username\":\"admin\",\"password\":\"password\"}")
                )
                .andExpect(status().isOk());
    }

    @Test
    void test_me() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk());
    }
}