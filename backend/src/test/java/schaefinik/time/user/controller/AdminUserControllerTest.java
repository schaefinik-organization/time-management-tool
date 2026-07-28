package schaefinik.time.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import schaefinik.time.auth.controller.AuthController;
import schaefinik.time.user.requestData.UserRequest;
import schaefinik.time.user.responseData.UserResponse;
import schaefinik.time.user.service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@WebMvcTest(AdminUserController.class)
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

}