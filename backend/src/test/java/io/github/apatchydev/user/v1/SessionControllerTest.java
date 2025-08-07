package io.github.apatchydev.user.v1;

import io.github.apatchydev.WebApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.*;

@WebMvcTest(SessionController.class)
class SessionControllerTest extends WebApiTest {
    @MockitoBean
    private SessionService sessionService;

    private static final String baseUrl = "/v1/session";

    @Test
    void loginProper() throws Exception {
        // Given
        // When
        // Then
    }

    @Test
    void logoutProper() throws Exception {
        // Given
        // When
        // Then
    }
}