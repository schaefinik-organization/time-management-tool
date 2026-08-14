package schaefinik.time.response.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import schaefinik.time.response.user.UserSummaryDTO;

@Data
@NoArgsConstructor
public class ProjectUserTimeEntryDTO {

	private UserSummaryDTO assignedUser;
	private double totalHours;
	private double billableHours;
	private double nonBillableHours;
	private long entryCount;

	public ProjectUserTimeEntryDTO(Long userId, String username, Double totalHours, Double billableHours, Long entryCount) {
		this.assignedUser = new UserSummaryDTO(userId, username);
		this.totalHours = totalHours != null ? totalHours : 0.0;
		this.billableHours = billableHours != null ? billableHours : 0.0;
		this.nonBillableHours = this.totalHours - this.billableHours;
		this.entryCount = entryCount != null ? entryCount : 0;
	}
}