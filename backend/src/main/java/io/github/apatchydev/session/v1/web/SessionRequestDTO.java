package io.github.apatchydev.session.v1.web;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionRequestDTO {
    public record Login(
        @NotBlank String username,
        @NotBlank String password
    ) {}
}
