package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.TestUtil.buildBookDto;
import static mate.academy.bookstore.util.TestUtil.buildCreateBookRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return page of books")
    @Sql(scripts = "classpath:database/books/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_whenCalled_shouldReturnPageOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        BookDto[] actual = objectMapper.readValue(content.toString(), BookDto[].class);
        assertEquals(3, actual.length);

        List<BookDto> expected = List.of(
                buildBookDto(1L, "Title1", "Author1", "10101011", "Description1", "Cover1"),
                buildBookDto(2L, "Title2", "Author2", "10101012", "Description2", "Cover2"),
                buildBookDto(3L, "Title3", "Author3", "10101013", "Description3", "Cover3")
        );

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getTitle(), actual[i].getTitle());
            assertEquals(expected.get(i).getAuthor(), actual[i].getAuthor());
            assertEquals(expected.get(i).getIsbn(), actual[i].getIsbn());
            assertEquals(expected.get(i).getCoverImage(), actual[i].getCoverImage());
            assertEquals(expected.get(i).getDescription(), actual[i].getDescription());
        }
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return BookDto for existing ID")
    @Sql(scripts = "classpath:database/books/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_whenIdExists_shouldReturnBookDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto.class);

        BookDto expected = buildBookDto(
                1L, "Title", "Author", "1010101", "Description", "Cover"
        );
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "categoryIds"),
                "Expected and actual BookDto are not equal"
        );
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw 404 status for invalid ID")
    @Sql(scripts = "classpath:database/books/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_whenInvalidId_shouldReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/books/995")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Should create book with valid params and return saved BookDto")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_whenRequestIsValid_shouldReturnSavedBookDto() throws Exception {
        CreateBookRequestDto request = buildCreateBookRequest();
        String jsonRequest = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);
        BookDto expected = buildBookDto(
                1L, "Title", "Author", "01010101", "Description", "Some image"
        );
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "categoryIds", "id"),
                "Expected and actual BookDto are not equal"
        );
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Should return 400 status when title is missing")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_whenTitleIsMissing_shouldReturnBadRequest() throws Exception {
        CreateBookRequestDto request = buildCreateBookRequest();
        request.setTitle(null);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
