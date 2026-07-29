<<<<<<<< HEAD:backend/src/main/java/schaefinik/time/controller/api/v1/admin/AdminUserController.java
package schaefinik.time.controller.api.v1.admin;
========
package schaefinik.time.admin.controller;
>>>>>>>> 2e964ff (refactor: reorganize user-related classes and update package structure for admin functionality):backend/src/main/java/schaefinik/time/admin/controller/AdminUserController.java

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
<<<<<<<< HEAD:backend/src/main/java/schaefinik/time/controller/api/v1/admin/AdminUserController.java
import schaefinik.time.request.user.UserChangeRequest;
import schaefinik.time.request.user.UserCreateRequest;
import schaefinik.time.response.user.TimeUserDTO;
import schaefinik.time.service.UserService;
========
import schaefinik.time.admin.responseData.UserResponse;
import schaefinik.time.user.requestData.UserRequest;
import schaefinik.time.user.service.UserService;
>>>>>>>> 2e964ff (refactor: reorganize user-related classes and update package structure for admin functionality):backend/src/main/java/schaefinik/time/admin/controller/AdminUserController.java

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final UserService userService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<TimeUserDTO>> getAllUsers() {
		return ResponseEntity.ok(userService.findAllUsers());
	}

	@PostMapping(produces = "application/json")
	public ResponseEntity<Boolean> createUser(@Valid @RequestBody UserCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
	}

	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Boolean> updateUser(@PathVariable Long id, @Valid @RequestBody UserChangeRequest request) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateUser(id, request));
	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}