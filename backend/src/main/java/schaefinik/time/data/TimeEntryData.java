package schaefinik.time.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TimeEntryData {
	private Long id;
	private Long projectId;
	private String projectName;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String description;

	public TimeEntryData(Long id, Long projectId, String projectName, LocalDateTime startTime, LocalDateTime endTime, String description) {
		this.id = id;
		this.projectId = projectId;
		this.projectName = projectName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}
}
