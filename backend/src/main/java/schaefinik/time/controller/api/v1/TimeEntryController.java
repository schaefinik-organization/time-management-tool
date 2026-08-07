package schaefinik.time.controller.api.v1;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.request.TimeEntryRequest;
import schaefinik.time.response.TimeEntryResponse;
import schaefinik.time.service.TimeEntryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-entries")
public class TimeEntryController {

	private final TimeEntryService timeEntryService;

	public TimeEntryController(TimeEntryService timeEntryService) {
		this.timeEntryService = timeEntryService;
	}

	@GetMapping
	public ResponseEntity<List<TimeEntryResponse>> getMyTimeEntries() {
		return ResponseEntity.ok(timeEntryService.getMyTimeEntries());
	}

	@PostMapping
	public ResponseEntity<TimeEntryResponse> createEntry(@Valid @RequestBody TimeEntryRequest request) {
		TimeEntryResponse created = timeEntryService.createEntry(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<TimeEntryResponse> updateEntry(@PathVariable Long id, @Valid @RequestBody TimeEntryRequest request) {
		TimeEntryResponse updated = timeEntryService.updateEntry(id, request);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
		timeEntryService.deleteEntry(id);
		return ResponseEntity.noContent().build();
	}
}