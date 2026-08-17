package schaefinik.time.response.manager;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlatManagerReportDTO {
	private Long userId;
	private String username;

	private Long projectId;
	private String projectName;

	private double totalHours;
	private double billableHours;
	private long entryCount;

	public FlatManagerReportDTO(Long userId, String username, Long projectId, String projectName, Double totalHours, Double billableHours, Long entryCount) {
		this.userId = userId;
		this.username = username;
		this.projectId = projectId;
		this.projectName = projectName;
		this.totalHours = totalHours != null ? totalHours : 0.0;
		this.billableHours = billableHours != null ? billableHours : 0.0;
		this.entryCount = entryCount != null ? entryCount : 0L;
	}
}