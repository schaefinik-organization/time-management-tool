package schaefinik.time.requestData;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TimeEntryRequest(
		@NotNull(message = "projectId required") Long projectId,
		@NotNull(message = "startTime required") LocalDateTime startTime,
		@NotNull(message = "endTime required") LocalDateTime endTime,
		@Size(max = 500, message = "note required max 500") String note) {
}
