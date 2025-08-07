package io.github.apatchydev;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.*;

import java.util.stream.Stream;

@WebMvcTest
public abstract class WebApiTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @BeforeEach
    void resetMapper() {
        mapper = new ObjectMapper();
    }

    protected static final String MapperConfig = "mapperConfigProvider";
    static Stream<Include>  mapperConfigProvider() {
        return Stream.of(Include.NON_EMPTY, Include.ALWAYS);
    }

    protected String json(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }
}