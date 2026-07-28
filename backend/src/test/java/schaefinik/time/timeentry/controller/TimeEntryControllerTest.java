package schaefinik.time.timeentry.controller;

import jakarta.validation.Valid;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import schaefinik.time.auth.controller.AuthController;
import schaefinik.time.timeentry.requestData.TimeEntryRequest;
import schaefinik.time.timeentry.responseData.TimeEntryResponse;
import schaefinik.time.timeentry.service.TimeEntryService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(TimeEntryController.class)
public class TimeEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TimeEntryService timeEntryService;

}
