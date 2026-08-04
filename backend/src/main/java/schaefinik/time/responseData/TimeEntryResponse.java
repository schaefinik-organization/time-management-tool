package schaefinik.time.responseData;

import java.time.LocalDateTime;

public record TimeEntryResponse(
		Long id,
		Long projectId,
		String projectName,
		LocalDateTime startTime,
		LocalDateTime endTime,
		String note) {
}
