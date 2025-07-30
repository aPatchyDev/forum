package io.github.apatchydev.user.v1;

import io.github.apatchydev.user.v1.UserRequestDTO.ChangePassword;
import io.github.apatchydev.user.v1.UserRequestDTO.CreateUser;
import io.github.apatchydev.user.v1.UserRequestDTO.DeleteUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@NullMarked
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUser createRequest) {
        var uri = userService.todo("register a new user: " + createRequest);
        return ResponseEntity.created(URI.create(uri)).build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotBlank String username, @Valid @RequestBody DeleteUser deleteRequest) {
        userService.todo("delete user " + username + " after checking password " + deleteRequest.password());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(@PathVariable @NotBlank String username, @Valid @RequestBody ChangePassword passwordChangeRequest) {
        userService.todo("change password " + username + " to "  + passwordChangeRequest.newPassword());
        return ResponseEntity.noContent().build();
    }
}