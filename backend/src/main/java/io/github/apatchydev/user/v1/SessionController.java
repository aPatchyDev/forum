package io.github.apatchydev.user.v1;

import io.github.apatchydev.user.v1.UserRequestDTO.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@NullMarked
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/session")
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<Void> login(@Valid @RequestBody Login login) {
        sessionService.todo("login " + login);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/current")
    public ResponseEntity<Void> logout() {
        sessionService.todo("logout");
        return ResponseEntity.noContent().build();
    }
}
