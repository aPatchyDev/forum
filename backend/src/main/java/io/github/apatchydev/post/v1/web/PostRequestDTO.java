package io.github.apatchydev.post.v1.web;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostRequestDTO {
    // Post management related
    public record CreatePost(
        @NotBlank String title,
        @NotBlank String body
    ) {}

    public record EditPost(
        @NotBlank String body
    ) {}

    public record CreateComment(
        @NotBlank String body
    ) {}
}
