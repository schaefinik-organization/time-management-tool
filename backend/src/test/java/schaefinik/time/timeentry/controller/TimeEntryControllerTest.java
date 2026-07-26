package schaefinik.time.timeentry.controller;

import jakarta.validation.Valid;
import schaefinik.time.timeentry.requestData.TimeEntryRequest;
import schaefinik.time.timeentry.responseData.TimeEntryResponse;
import schaefinik.time.timeentry.service.TimeEntryService;

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

    @GetMapping(produces = "application/json")
    public List<TimeEntryResponse> findByDate(@RequestParam LocalDate date) {
        return timeEntryService.findByDate(date);
    }

    @PostMapping(produces = "application/json")
    public TimeEntryResponse create(@Valid @RequestBody TimeEntryRequest request) {
        return timeEntryService.create(request);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public TimeEntryResponse update(@PathVariable Long id, @Valid @RequestBody TimeEntryRequest request) {
        return timeEntryService.update(id, request);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public void delete(@PathVariable Long id) {
        timeEntryService.delete(id);
    }

}
