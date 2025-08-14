package io.github.apatchydev.post.v1.web;

import io.github.apatchydev.post.v1.web.PostRequestDTO.CreateComment;
import io.github.apatchydev.post.v1.web.PostRequestDTO.CreatePost;
import io.github.apatchydev.post.v1.web.PostRequestDTO.EditPost;
import io.github.apatchydev.post.v1.web.PostResponseDTO.Comment;
import io.github.apatchydev.post.v1.web.PostResponseDTO.Post;
import io.github.apatchydev.post.v1.web.PostResponseDTO.PostInfo;
import io.github.apatchydev.post.v1.web.PostResponseDTO.PostView;
import io.github.apatchydev.post.v1.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@NullMarked
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/posts")
public class PostController {
    private final PostService postService;

    // Start of Post CRUD

    @GetMapping
    public List<PostInfo> queryPosts(@RequestParam(required = false) @Nullable String search) {
        var posts = (search == null || search.isEmpty())
                ? postService.todo("get all posts")
                : postService.todo("get posts with title containing the search query");

        var tmp1 = Instant.now();
        var tmp2 = new PostInfo(1,"test", posts, tmp1, tmp1, 0, tmp1);

        return List.of(tmp2);
    }

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody CreatePost post) {
        var postUri = postService.todo("create post " + post);
        return ResponseEntity.created(URI.create(postUri)).build();
    }

    @GetMapping("/{postId}")
    public PostView getPost(@PathVariable @Min(1) int postId) {
        var post = postService.todo("get post body of " + postId);
        var tmp1  = Instant.now();
        var tmp2 = new Post(postId, "test", post, "example", tmp1, tmp1);
        List<Comment> tmp3 = Collections.emptyList();
        return new PostView(tmp2, tmp3);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable @Min(1) int postId, @Valid @RequestBody EditPost post) {
        var success = postService.todo("update post of id " + postId + " to " + post);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable @Min(1) int postId) {
        var post = postService.todo("delete post " + postId);
        return ResponseEntity.noContent().build();
    }

    // End of Post CRUD
    // Start of Comment CRUD

    @GetMapping("/{postId}/comments")
    public List<Comment> getAllComments(@PathVariable @Min(1) int postId) {
        var comments = postService.todo("get all comments of post " + postId);
        var tmp = new Comment(1, "test1", comments, Instant.now());
        return List.of(tmp);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable @Min(1) int postId, @Valid @RequestBody CreateComment comment) {
        var commentUri = postService.todo("create comment of post " + postId + " with " + comment);
        return ResponseEntity.created(URI.create(commentUri)).build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @Min(1) int postId, @PathVariable @Min(1) int commentId) {
        postService.todo("delete comment " + commentId + " in post " + postId);
        return ResponseEntity.noContent().build();
    }

    // End of Comment CRUD
}
