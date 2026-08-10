package schaefinik.time.response.entry;

import lombok.Getter;
import lombok.Setter;
import schaefinik.time.response.project.ProjectSummaryDTO;
import schaefinik.time.response.user.UserSummaryDTO;

import java.time.LocalDateTime;

@Getter
@Setter
public class TimeEntryDTO {
	private Long id;
	private UserSummaryDTO user;
	private ProjectSummaryDTO project;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String description;
	private LocalDateTime creationDate;
}
