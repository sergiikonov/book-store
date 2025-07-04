package mate.academy.bookstore.util;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.dto.cart.AddToCartRequestDto;
import mate.academy.bookstore.dto.cart.CartItemDto;
import mate.academy.bookstore.dto.cart.ShoppingCartDto;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CategoryRequestDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;

public class TestUtil {
    public static final Long ID = 1L;
    public static final String AUTHOR = "Author";
    public static final String ISBN = "01010101";
    public static final String TITLE = "Title";
    public static final String COVER_IMAGE = "Some image";
    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final int QUANTITY = 10;
    public static final Long USER_ID = 999L;

    public static BookDto buildBookDto(Long id, String title, String author,
                                 String isbn, String description, String cover) {
        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle(title);
        bookDto.setAuthor(author);
        bookDto.setIsbn(isbn);
        bookDto.setPrice(BigDecimal.TEN);
        bookDto.setCoverImage(cover);
        bookDto.setDescription(description);
        return bookDto;
    }

    public static CreateBookRequestDto buildCreateBookRequest() {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setCategoryIds(List.of(1L));
        createBookRequestDto.setTitle(TITLE);
        createBookRequestDto.setAuthor(AUTHOR);
        createBookRequestDto.setPrice(BigDecimal.TEN);
        createBookRequestDto.setIsbn(ISBN);
        createBookRequestDto.setDescription(DESCRIPTION);
        createBookRequestDto.setCoverImage(COVER_IMAGE);
        return createBookRequestDto;
    }

    public static CategoryRequestDto buildCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName(NAME);
        requestDto.setDescription(DESCRIPTION);
        return requestDto;
    }

    public static CategoryDto buildCategoryDto(Long categoryId, String name, String description) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName(name);
        categoryDto.setDescription(description);
        return categoryDto;
    }

    public static CreateBookRequestDto buildValidCreateRequestDto() {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setAuthor(AUTHOR);
        dto.setIsbn(ISBN);
        dto.setPrice(BigDecimal.TEN);
        dto.setTitle(TITLE);
        dto.setCoverImage(COVER_IMAGE);
        dto.setCategoryIds(List.of(ID));
        return dto;
    }

    public static Book buildValidBook() {
        Book book = new Book();
        book.setAuthor(AUTHOR);
        book.setIsbn(ISBN);
        book.setPrice(BigDecimal.TEN);
        book.setTitle(TITLE);
        book.setCoverImage(COVER_IMAGE);
        return book;
    }

    public static BookDto buildValidBookDto() {
        BookDto dto = new BookDto();
        dto.setAuthor(AUTHOR);
        dto.setIsbn(ISBN);
        dto.setPrice(BigDecimal.TEN);
        dto.setTitle(TITLE);
        dto.setCoverImage(COVER_IMAGE);
        dto.setCategoryIds(List.of(ID));
        return dto;
    }

    public static Category buildValidCategory() {
        Category category = new Category();
        category.setDescription(DESCRIPTION);
        category.setName(NAME);
        category.setId(ID);
        return category;
    }

    public static CategoryDto buildValidCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setDescription(DESCRIPTION);
        categoryDto.setName(NAME);
        categoryDto.setId(ID);
        return categoryDto;
    }

    public static CategoryRequestDto buildValidCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setDescription(DESCRIPTION);
        requestDto.setName(NAME);
        return requestDto;
    }

    public static CartItemDto buildCartItemDto() {
        return new CartItemDto(ID, ID, TITLE, QUANTITY);
    }

    public static ShoppingCartDto buildShoppingCartDto(CartItemDto cartItem) {
        return new ShoppingCartDto(ID, ID, Set.of(cartItem));
    }

    public static Book buildBook() {
        Book book = new Book();
        book.setId(ID);
        return book;
    }

    public static AddToCartRequestDto buildCartRequestDto(Long bookId) {
        AddToCartRequestDto addToCartRequestDto = new AddToCartRequestDto();
        addToCartRequestDto.setBookId(bookId);
        addToCartRequestDto.setQuantity(QUANTITY);
        return addToCartRequestDto;
    }

    public static ShoppingCartDto buildValidCartDto() {
        return new ShoppingCartDto(
                USER_ID, USER_ID, new HashSet<>()
        );
    }

    public static ShoppingCartDto buildValidCartDtoWithBook() {
        CartItemDto cartItem = new CartItemDto(ID, ID, TITLE, QUANTITY);
        Set<CartItemDto> items = new HashSet<>();
        items.add(cartItem);

        return new ShoppingCartDto(ID, USER_ID, items);
    }

    public static AddToCartRequestDto buildValidCartRequestDto() {
        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setBookId(ID);
        requestDto.setQuantity(QUANTITY);
        return requestDto;
    }
}
