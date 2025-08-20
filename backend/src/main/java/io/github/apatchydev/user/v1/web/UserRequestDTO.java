package io.github.apatchydev.user.v1.web;

import io.github.apatchydev.global.Constraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserRequestDTO {
    // Account management related
    public record CreateUser(
        @NotBlank
        @Size(min = 1, max = 20)
        String username,

        @NotBlank
        @Pattern(regexp = Constraints.PASSWORD)
        String password
    ) {}

    public record DeleteUser(
        @NotBlank
        @Pattern(regexp = Constraints.PASSWORD)
        String password
    ) {}

    public record ChangePassword(
        @NotBlank
        @Pattern(regexp = Constraints.PASSWORD)
        String oldPassword,

        @NotBlank
        @Pattern(regexp = Constraints.PASSWORD)
        String newPassword
    ) {}
}
