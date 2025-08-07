package io.github.apatchydev.user.v1;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.apatchydev.WebApiTest;
import io.github.apatchydev.user.v1.UserRequestDTO.ChangePassword;
import io.github.apatchydev.user.v1.UserRequestDTO.CreateUser;
import io.github.apatchydev.user.v1.UserRequestDTO.DeleteUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest extends WebApiTest {
    @MockitoBean
    private UserService userService;

    private static final String baseUrl = "/v1/users";

    @Test
    void createUserProper() throws Exception {
        // Given
        var username = "alice";
        var password = "secret";
        var expected = baseUrl + "/alice";
        Mockito.when(userService.todo(Mockito.anyString()))
                .thenReturn(expected);

        // When
        var request = json(new CreateUser(username, password));
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
    void createUserMissingBody() throws Exception {
        // Given
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("missing body"));

        // When
        var response = mockMvc.perform(
            post(baseUrl)
        ).andDo(print());

        // Then
        response.andExpect(
            status().isBadRequest()
        );
    }

    @ParameterizedTest
    @MethodSource(MapperConfig)
    void createUserMissingField(Include mapperOption) throws Exception {
        mapper.setSerializationInclusion(mapperOption);

        // Given
        var username = "alice";
        String password = null;
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("missing password"));

        // When
        var request = json(new CreateUser(username, password));
        var response = mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpect(
            status().isBadRequest()
        );
    }

    @Test
    void deleteUserProper() throws Exception {
        // Given
        var username = "alice";
        var password = "secret";
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenReturn("success");

        // When
        var request = json(new DeleteUser(password));
        var response = mockMvc.perform(
            delete(baseUrl + "/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNoContent()
        );
    }

    @Test
    void deleteUserWrongPassword() throws Exception {
        // Given
        var username = "alice";
        var password = "wrongPassword";
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("wrong password"));

        // When
        var request = json(new DeleteUser(password));
        var response = mockMvc.perform(
            delete(baseUrl + "/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isUnauthorized()
        );
    }

    @ParameterizedTest
    @MethodSource(MapperConfig)
    void deleteUserNoPassword(Include mapperOption) throws Exception {
        mapper.setSerializationInclusion(mapperOption);

        // Given
        var username = "alice";
        String password = null;
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("wrong password"));

        // When
        var request = json(new DeleteUser(password));
        var response = mockMvc.perform(
            delete(baseUrl + "/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void deleteUserInvalidUser() throws Exception {
        // Given
        var username = "bob";
        var password = "secret";
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("user does not exist"));

        // When
        var request = json(new DeleteUser(password));
        var response = mockMvc.perform(
            delete(baseUrl + "/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void changePasswordProper() throws Exception {
        // Given
        var username = "alice";
        var oldPassword = "secret";
        var newPassword = "supersecret";
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenReturn("success");

        // When
        var request = json(new ChangePassword(oldPassword, newPassword));
        var response = mockMvc.perform(
            post(baseUrl + "/{username}/password", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNoContent()
        );
    }

    @Test
    void changePasswordInvalidUser() throws Exception {
        // Given
        var username = "bob";
        var oldPassword = "secret";
        var newPassword = "supersecret";
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("user does not exist"));

        // When
        var request = json(new ChangePassword(oldPassword, newPassword));
        var response = mockMvc.perform(
            post(baseUrl + "/{username}/password", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void changePasswordWrongPassword() throws Exception {
        // Given
        var username = "bob";
        var oldPassword = "wrongPassword";
        var newPassword = "supersecret";
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("wrong password"));

        // When
        var request = json(new ChangePassword(oldPassword, newPassword));
        var response = mockMvc.perform(
            post(baseUrl + "/{username}/password", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isUnauthorized()
        );
    }

    @ParameterizedTest
    @MethodSource(MapperConfig)
    void changePasswordNoOldPassword(Include mapperOption) throws Exception {
        mapper.setSerializationInclusion(mapperOption);

        // Given
        var username = "bob";
        String oldPassword = null;
        var newPassword = "supersecret";
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("wrong password"));

        // When
        var request = json(new ChangePassword(oldPassword, newPassword));
        var response = mockMvc.perform(
            post(baseUrl + "/{username}/password", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }

    @ParameterizedTest
    @MethodSource(MapperConfig)
    void changePasswordNoNewPassword(Include mapperOption) throws Exception {
        mapper.setSerializationInclusion(mapperOption);

        // Given
        var username = "bob";
        var oldPassword = "secret";
        String newPassword = null;
        Mockito.when(userService.todo(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("wrong password"));

        // When
        var request = json(new ChangePassword(oldPassword, newPassword));
        var response = mockMvc.perform(
            post(baseUrl + "/{username}/password", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        ).andDo(print());

        // Then
        response.andExpectAll(
            status().isBadRequest()
        );
    }
}