package io.github.apatchydev.post.v1.web;

import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.List;

@NullMarked
public class PostResponseDTO {
    // Data container
    public record PostInfo(
        int id, String author, String title,
        Instant createdAt, Instant updatedAt,
        int commentCount, Instant commentedAt
    ) {}

    public record Post(
        int id, String author, String title, String body,
        Instant createdAt, Instant updatedAt
    ) {}

    public record Comment(int id, String author, String body, Instant createdAt) {}

    // Post content related
    public record PostView(Post post, List<Comment> comments) {}
}
