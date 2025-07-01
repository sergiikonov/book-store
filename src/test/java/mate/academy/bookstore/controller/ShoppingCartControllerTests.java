package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.TestUtil.buildValidCartDto;
import static mate.academy.bookstore.util.TestUtil.buildValidCartDtoWithBook;
import static mate.academy.bookstore.util.TestUtil.buildValidCartRequestDto;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.bookstore.dto.cart.AddToCartRequestDto;
import mate.academy.bookstore.dto.cart.ShoppingCartDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTests {
    protected static MockMvc mockMvc;
    private static final String EMAIL = "test@example.com";

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

    @WithUserDetails(value = EMAIL)
    @Test
    @DisplayName("Should return user's ShoppingCartDto")
    @Sql(scripts = "classpath:database/shopping-carts/init-shopping-cart-data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCart_whenShoppingCartHasUser_shouldReturnShoppingCartDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );
        ShoppingCartDto expected = buildValidCartDto();
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual),
                "Expected and actual ShoppingCartDto are not equal"
        );
    }

    @WithUserDetails(value = EMAIL)
    @Test
    @DisplayName("Should add book to cart")
    @Sql(scripts = {"classpath:database/shopping-carts/init-shopping-cart-data.sql",
                    "classpath:database/books/add-one-book.sql",
                    "classpath:database/categories/add-one-category.sql",
                    "classpath:database/add-book-category.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addBookToCart_whenValidRequest_shouldReturnShoppingCartDto() throws Exception {
        AddToCartRequestDto requestDto = buildValidCartRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );
        ShoppingCartDto expected = buildValidCartDtoWithBook();
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id"),
                "Expected and actual ShoppingCartDto are not equal"
        );
    }

    @WithUserDetails(value = EMAIL)
    @Test
    @DisplayName("Should return 400 status when bookId is missing")
    @Sql(scripts = {"classpath:database/shopping-carts/init-shopping-cart-data.sql",
            "classpath:database/books/add-one-book.sql",
            "classpath:database/categories/add-one-category.sql",
            "classpath:database/add-book-category.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addBookToCart_whenBookIdIsNull_shouldReturnBadRequest() throws Exception {
        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setBookId(null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
