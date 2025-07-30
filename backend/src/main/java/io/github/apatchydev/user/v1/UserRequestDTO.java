package io.github.apatchydev.user.v1;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserRequestDTO {
    // Account management related
    public record CreateUser(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record DeleteUser(
            @NotBlank String password
    ) {}

    public record ChangePassword(
            @NotBlank String oldPassword,
            @NotBlank String newPassword
    ) {}

    // Authentication related
    public record Login(
            @NotBlank String username,
            @NotBlank String password
    ) {}
}
