package schaefinik.time.response.manager;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMemberProjectSummaryDTO {
	private Long projectId;
	private String projectName;
	private double totalHours;
	private double billableHours;
	private double nonBillableHours;
	private long entryCount;

	public TeamMemberProjectSummaryDTO(Long projectId, String projectName, double totalHours, double billableHours, long entryCount) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.totalHours = totalHours;
		this.billableHours = billableHours;
		this.nonBillableHours = totalHours - billableHours;
		this.entryCount = entryCount;
	}
}