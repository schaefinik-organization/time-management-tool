package schaefinik.time.controller.api.v1.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.request.user.UserChangeRequest;
import schaefinik.time.request.user.UserCreateRequest;
import schaefinik.time.response.user.TimeUserDTO;
import schaefinik.time.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
public class ManagerUserController {
	private final UserService userService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<TimeUserDTO>> getAllSubordinates() {
		return ResponseEntity.ok(userService.findAllSubordinatesByCurrentUser());
	}

	@PostMapping(produces = "application/json")
	public ResponseEntity<Boolean> createEmployee(@Valid @RequestBody UserCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createEmployee(request));
	}

	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Boolean> updateEmployee(@PathVariable Long id, @Valid @RequestBody UserChangeRequest request) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateUser(id, request));
	}
}