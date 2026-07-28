package schaefinik.time.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(Map.of(
				"status", 400,
				"error", "Bad Request",
				"message", ex.getMessage(),
				"timestamp", LocalDateTime.now()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
						error -> error.getField(),
						error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
						(a, b) -> a,
						LinkedHashMap::new));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
				"status", 400,
				"error", "Validation Error",
				"message", "Request validation failed",
				"timestamp", LocalDateTime.now(),
				"fieldErrors", fieldErrors));
	}
}
