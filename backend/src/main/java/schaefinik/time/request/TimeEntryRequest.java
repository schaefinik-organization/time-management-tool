package schaefinik.time.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TimeEntryRequest {
	@NotNull(message = "projectId required")
	Long projectId;
	@NotNull(message = "startTime required")
	LocalDateTime startTime;
	@NotNull(message = "endTime required")
	LocalDateTime endTime;
	@Size(max = 500, message = "description max 500")
	String description;
}
