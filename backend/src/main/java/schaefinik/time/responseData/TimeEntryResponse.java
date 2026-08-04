package schaefinik.time.responseData;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeEntryResponse(
		Long id,
		Long projectId,
		String projectName,
		LocalDate entryDate,
		LocalTime startTime,
		LocalTime endTime,
		String note) {
}
