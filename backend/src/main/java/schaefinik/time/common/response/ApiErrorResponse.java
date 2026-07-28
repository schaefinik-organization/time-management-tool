package schaefinik.time.common.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
		int status,
		String error,
		String message,
		LocalDateTime timestamp,
		Map<String, String> validationErrors) {
}
