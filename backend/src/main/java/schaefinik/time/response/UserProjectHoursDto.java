package schaefinik.time.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProjectHoursDto {

	private Long userId;
	private String username;
	private double totalHours;

	public UserProjectHoursDto(Long userId, String username, Number totalMinutes) {
		this.userId = userId;
		this.username = username;

		if (totalMinutes != null) {
			double hours = totalMinutes.doubleValue() / 60.0;
			this.totalHours = Math.round(hours * 100.0) / 100.0;
		} else {
			this.totalHours = 0.0;
		}
	}
}