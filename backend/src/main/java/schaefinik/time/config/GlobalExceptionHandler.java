package schaefinik.time.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import schaefinik.time.exception.InvalidTimeRangeException;
import schaefinik.time.exception.OverlappingTimeException;
import schaefinik.time.exception.ResourceNotFoundException;
import schaefinik.time.response.ApiErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OverlappingTimeException.class)
	public ResponseEntity<ApiErrorResponse> handleOverlappingTime(OverlappingTimeException ex, HttpServletRequest request) {
		ApiErrorResponse response = ApiErrorResponse.builder()
				.status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		ApiErrorResponse response = ApiErrorResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(InvalidTimeRangeException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidTimeRange(InvalidTimeRangeException ex, HttpServletRequest request) {
		ApiErrorResponse response = ApiErrorResponse.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		ApiErrorResponse response = ApiErrorResponse.builder()
				.status(HttpStatus.FORBIDDEN.value())
				.error("Forbidden")
				.message("Du hast keine Berechtigung für diese Aktion.")
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		ApiErrorResponse response = ApiErrorResponse.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message("Validierungsfehler bei der Eingabe.")
				.validationErrors(errors)
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
		ex.printStackTrace();

		ApiErrorResponse response = ApiErrorResponse.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.message("Ein unerwarteter Fehler ist aufgetreten. Bitte versuche es später erneut.")
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}