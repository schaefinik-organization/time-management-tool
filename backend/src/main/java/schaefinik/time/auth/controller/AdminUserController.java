package schaefinik.time.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import schaefinik.time.auth.requestData.UserRequest;
import schaefinik.time.auth.responseData.UserResponse;
import schaefinik.time.auth.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// --- AdminUserController.java ---
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Ganzer Controller für Admins
public class AdminUserController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(authService.findAllUsers()); // NEU: Übersicht für Admins
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id); // NEU: User löschen
        return ResponseEntity.noContent().build();
    }
}