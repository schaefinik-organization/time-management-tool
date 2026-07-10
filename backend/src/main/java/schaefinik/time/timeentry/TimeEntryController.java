package schaefinik.time.timeentry;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/time-entries")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    @GetMapping
    public List<TimeEntryResponse> findByDate(@RequestParam LocalDate date) {
        return timeEntryService.findByDate(date);
    }

    @PostMapping
    public TimeEntryResponse create(@Valid @RequestBody CreateTimeEntryRequest request) {
        return timeEntryService.create(request);
    }
}
