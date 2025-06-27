package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.TestUtil.buildCategoryDto;
import static mate.academy.bookstore.util.TestUtil.buildCategoryRequestDto;
import static mate.academy.bookstore.util.TestUtil.buildValidCategoryDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CategoryRequestDto;
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
public class CategoryControllerTests {
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

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Should create category with valid request and return CategoryDto")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_whenRequestIsValid_shouldReturnSavedCategoryDto() throws Exception {
        CategoryRequestDto requestDto = buildCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );
        CategoryDto expected = buildValidCategoryDto();
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual CategoryDto are not equal"
        );
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Should return 400 status when name is missing")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_whenNameIsMissing_shouldReturnBadRequest() throws Exception {
        CategoryRequestDto request = buildCategoryRequestDto();
        request.setName(null);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return page of categories for valid request")
    @Sql(scripts = "classpath:database/categories/add-three-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_whenCalled_shouldReturnPageOfCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");
        List<CategoryDto> actual = objectMapper.readerForListOf(CategoryDto.class)
                .readValue(content);
        assertEquals(3, actual.size());
        List<CategoryDto> expected = List.of(
                buildCategoryDto(1L, "Name1", "Description1"),
                buildCategoryDto(2L, "Name2", "Description2"),
                buildCategoryDto(3L, "Name3", "Description3")
        );

        assertIterableEquals(
                List.of(expected),
                List.of(actual),
                "List of categories are not equals"
        );
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return CategoryDto for existing ID")
    @Sql(scripts = "classpath:database/categories/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCategoryById_whenIdExists_shouldReturnCategoryDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );
        CategoryDto expected = buildValidCategoryDto();
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual),
                "Expected and actual CategoryDto are not equal"
        );
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw 404 status for invalid ID")
    @Sql(scripts = "classpath:database/categories/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCategoryById_whenInvalidId_shouldReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/categories/995")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Should update category and return updated CategoryDto")
    @Sql(scripts = "classpath:database/categories/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_whenCategoryIsValid_shouldReturnUpdatedCategoryDto() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("New name");
        requestDto.setDescription("New description");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/categories/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        CategoryDto expected = new CategoryDto();
        expected.setName("New name");
        expected.setDescription("New description");

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual CategoryDto are not equal"
        );
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return 500 status cause of bad role")
    @Sql(scripts = "classpath:database/categories/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_whenCategoryIsValid_shouldReturnInternalServerError() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("New name");
        requestDto.setDescription("New description");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(put("/categories/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }
}
