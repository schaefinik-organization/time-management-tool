package schaefinik.time.requestData;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeEntryRequest(
		@NotNull(message = "projectId required") Long projectId,
		@NotNull(message = "entryDate required") LocalDate entryDate,
		@NotNull(message = "startTime required") LocalTime startTime,
		@NotNull(message = "endTime required") LocalTime endTime,
		@Size(max = 500, message = "note required max 500") String note) {
}
