package io.github.apatchydev.post.v1.web;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.apatchydev.WebApiTest;
import io.github.apatchydev.post.v1.service.PostService;
import io.github.apatchydev.post.v1.web.PostRequestDTO.CreateComment;
import io.github.apatchydev.post.v1.web.PostRequestDTO.CreatePost;
import io.github.apatchydev.post.v1.web.PostRequestDTO.EditPost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.*;

import java.time.Instant;

import static io.github.apatchydev.ConversionMatcher.convertibleBy;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest extends WebApiTest {
    @MockitoBean
    private PostService postService;

    private static final String baseUrl = "/v1/posts";

    @Test
    void queryPostsAllNoSearch() throws Exception {
        // Given
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("get all posts");

        // When
        var response = mockMvc.perform(
            get(baseUrl)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").isArray(),
            jsonPath("$[*].id").value(everyItem(greaterThan(0))),
            jsonPath("$[*].author").value(everyItem(not(blankOrNullString()))),
            jsonPath("$[*].title").value(everyItem(not(blankOrNullString()))),
            jsonPath("$[*].createdAt").value(everyItem(convertibleBy(Instant::parse, Instant.class))),
            jsonPath("$[*].updatedAt").value(everyItem(convertibleBy(Instant::parse, Instant.class))),
            jsonPath("$[*].commentCount").value(everyItem(greaterThanOrEqualTo(0))),
            jsonPath("$[*].commentedAt").value(everyItem(convertibleBy(Instant::parse, Instant.class)))
        );
    }

    @Test
    void queryPostsAllBlankSearch() throws Exception {
        // Given
        var searchTerm = "";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("get all posts");

        // When
        var response = mockMvc.perform(
            get(baseUrl)
                .param("search", searchTerm)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").isArray(),
            jsonPath("$[*].id").value(everyItem(greaterThan(0))),
            jsonPath("$[*].author").value(everyItem(not(blankOrNullString()))),
            jsonPath("$[*].title").value(everyItem(not(blankOrNullString()))),
            jsonPath("$[*].createdAt").value(everyItem(convertibleBy(Instant::parse, Instant.class))),
            jsonPath("$[*].updatedAt").value(everyItem(convertibleBy(Instant::parse, Instant.class))),
            jsonPath("$[*].commentCount").value(everyItem(greaterThanOrEqualTo(0))),
            jsonPath("$[*].commentedAt").value(everyItem(convertibleBy(Instant::parse, Instant.class)))
        );
    }

    @Test
    void queryPostsSearch() throws Exception {
        // Given
        var searchTerm = "hello";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("get posts with hello in title");

        // When
        var response = mockMvc.perform(
            get(baseUrl)
                .param("search", searchTerm)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").isArray(),
            jsonPath("$[*].id").value(everyItem(greaterThan(0))),
            jsonPath("$[*].author").value(everyItem(not(blankOrNullString()))),
            jsonPath("$[*].title").value(everyItem(containsString(searchTerm))),
            jsonPath("$[*].createdAt").value(everyItem(convertibleBy(Instant::parse, Instant.class))),
            jsonPath("$[*].updatedAt").value(everyItem(convertibleBy(Instant::parse, Instant.class))),
            jsonPath("$[*].commentCount").value(everyItem(greaterThanOrEqualTo(0))),
            jsonPath("$[*].commentedAt").value(everyItem(convertibleBy(Instant::parse, Instant.class)))
        );
    }

    @Test
    void createPostProper() throws Exception {
        // Given
        var title = "Hello World";
        var body = "ABCDEFG";
        var expected = baseUrl + "/1";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn(expected);

        // When
        var request = json(new CreatePost(title, body));
        var response = mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isCreated(),
            header().string(HttpHeaders.LOCATION, expected)
        );
    }

    @Test
    void createPostMissingBody() throws Exception {
        // Given
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("missing body"));

        // When
        var response = mockMvc.perform(
            post(baseUrl)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @ParameterizedTest
    @MethodSource(MapperConfig)
    void createUserMissingField(Include mapperOption) throws Exception {
        mapper.setSerializationInclusion(mapperOption);

        // Given
        var title = "Hello World";
        String body = null;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("missing body"));

        // When
        var request = json(new CreatePost(title, body));
        var response = mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void createPostUnauthorized() throws Exception {
        // Given
        var title = "Hello World";
        var body = "ABCDEFG";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalStateException("unauthorized"));

        // When
        var request = json(new CreatePost(title, body));
        var response = mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isUnauthorized()
        );
    }

    @Test
    void getPostProper() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("title of post 1");

        // When
        var response = mockMvc.perform(
            get(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.post").isNotEmpty(),
            jsonPath("$.post.id").value(greaterThan(0)),
            jsonPath("$.post.author").value(not(blankOrNullString())),
            jsonPath("$.post.body").value(not(blankOrNullString())),
            jsonPath("$.post.createdAt").value(convertibleBy(Instant::parse, Instant.class)),
            jsonPath("$.comments").isArray(),
            jsonPath("$.comments[*].id").value(everyItem(greaterThan(0))),
            jsonPath("$.comments[*].author").value(everyItem(not(blankOrNullString()))),
            jsonPath("$.comments[*].body").value(everyItem(not(blankOrNullString()))),
            jsonPath("$.comments[*].createdAt").value(everyItem(convertibleBy(Instant::parse, Instant.class)))
        );
    }

    @Test
    void getPostInvalidId() throws Exception {
        // Given
        var postId = -1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("invalid post id"));

        // When
        var response = mockMvc.perform(
            get(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void getPostUnusedId() throws Exception {
        // Given
        var postId = 100;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("post does not exist"));

        // When
        var response = mockMvc.perform(
            get(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void updatePostProper() throws Exception {
        // Given
        var postId = 1;
        var body = "QWERTY";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("success");

        // When
        var request = json(new EditPost(body));
        var response = mockMvc.perform(
            patch(baseUrl + "/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isOk()
        );
    }

    @Test
    void updatePostMissingBody() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("missing body"));

        // When
        var response = mockMvc.perform(
            patch(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void updatePostInvalidId() throws Exception {
        // Given
        var postId = -1;
        var body = "QWERTY";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("invalid post id"));

        // When
        var request = json(new EditPost(body));
        var response = mockMvc.perform(
            patch(baseUrl + "/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void updatePostUnusedId() throws Exception {
        // Given
        var postId = 100;
        var body = "QWERTY";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("post does not exist"));

        // When
        var request = json(new EditPost(body));
        var response = mockMvc.perform(
            patch(baseUrl + "/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void updatePostUnauthorized() throws Exception {
        // Given
        var postId = 1;
        var body = "QWERTY";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalStateException("unauthorized"));

        // When
        var request = json(new EditPost(body));
        var response = mockMvc.perform(
            patch(baseUrl + "/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isUnauthorized()
        );
    }

    @Test
    void deletePostProper() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("success");

        // When
        var response = mockMvc.perform(
            delete(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNoContent()
        );
    }

    @Test
    void deletePostInvalidId() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("invalid post id"));

        // When
        var response = mockMvc.perform(
            delete(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void deletePostUnusedId() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("post does not exist"));

        // When
        var response = mockMvc.perform(
            delete(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void deletePostUnauthorized() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalStateException("unauthorized"));

        // When
        var response = mockMvc.perform(
            delete(baseUrl + "/{postId}", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isUnauthorized()
        );
    }

    @Test
    void getAllCommentsProper() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("get all comments of post");

        // When
        var response = mockMvc.perform(
            get(baseUrl + "/{postId}/comments", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").isArray(),
            jsonPath("$[*].id").value(everyItem(greaterThan(0))),
            jsonPath("$[*].author").value(everyItem(not(blankOrNullString()))),
            jsonPath("$[*].body").value(everyItem(not(blankOrNullString()))),
            jsonPath("$[*].createdAt").value(everyItem(convertibleBy(Instant::parse, Instant.class)))
        );
    }

    @Test
    void getAllCommentsInvalidPostId() throws Exception {
        // Given
        var postId = -1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("invalid post id"));

        // When
        var response = mockMvc.perform(
            get(baseUrl + "/{postId}/comments", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void getAllCommentsUnusedPostId() throws Exception {
        // Given
        var postId = 100;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("post does not exist"));

        // When
        var response = mockMvc.perform(
            get(baseUrl + "/{postId}/comments", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void createCommentProper() throws Exception {
        // Given
        var postId = 1;
        var body = "QWERTY";
        var expected = baseUrl + "/1/comments/1";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn(expected);

        // When
        var request = json(new CreateComment(body));
        var response = mockMvc.perform(
            post(baseUrl + "/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isCreated(),
            header().string(HttpHeaders.LOCATION, expected)
        );
    }

    @Test
    void createCommentInvalidPostId() throws Exception {
        // Given
        var postId = -1;
        var body = "QWERTY";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("invalid post id"));

        // When
        var request = json(new CreateComment(body));
        var response = mockMvc.perform(
            post(baseUrl + "/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void createCommentUnusedPostId() throws Exception {
        // Given
        var postId = 100;
        var body = "QWERTY";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("post does not exist"));

        // When
        var request = json(new CreateComment(body));
        var response = mockMvc.perform(
            post(baseUrl + "/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void createCommentUnauthorized() throws Exception {
        // Given
        var postId = 1;
        var body = "QWERTY";
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalStateException("unauthorized"));

        // When
        var request = json(new CreateComment(body));
        var response = mockMvc.perform(
            post(baseUrl + "/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isUnauthorized()
        );
    }

    @Test
    void createCommentMissingBody() throws Exception {
        // Given
        var postId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("missing body"));

        // When
        var response = mockMvc.perform(
            post(baseUrl + "/{postId}/comments", postId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void deleteCommentProper() throws Exception {
        // Given
        var postId = 1;
        var commentId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenReturn("success");

        // When
        var response =  mockMvc.perform(
            delete(baseUrl + "/{postId}/comments/{commentId}", postId, commentId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNoContent()
        );
    }

    @Test
    void deleteCommentInvalidPostId() throws Exception {
        // Given
        var postId = -1;
        var commentId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("invalid post id"));

        // When
        var response =  mockMvc.perform(
            delete(baseUrl + "/{postId}/comments/{commentId}", postId, commentId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void deleteCommentInvalidCommentId() throws Exception {
        // Given
        var postId = 1;
        var commentId = -1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("invalid comment id"));

        // When
        var response =  mockMvc.perform(
            delete(baseUrl + "/{postId}/comments/{commentId}", postId, commentId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void deleteCommentUnusedPostId() throws Exception {
        // Given
        var postId = 100;
        var commentId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("post does not exist"));

        // When
        var response =  mockMvc.perform(
            delete(baseUrl + "/{postId}/comments/{commentId}", postId, commentId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void deleteCommentUnusedCommentId() throws Exception {
        // Given
        var postId = 1;
        var commentId = 100;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("comment does not exist"));

        // When
        var response =  mockMvc.perform(
            delete(baseUrl + "/{postId}/comments/{commentId}", postId, commentId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void deleteCommentUnauthorized() throws Exception {
        // Given
        var postId = 1;
        var commentId = 1;
        Mockito.when(postService.todo(Mockito.anyString()))
            .thenThrow(new IllegalStateException("unauthorized"));

        // When
        var response =  mockMvc.perform(
            delete(baseUrl + "/{postId}/comments/{commentId}", postId, commentId)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isUnauthorized()
        );
    }
}