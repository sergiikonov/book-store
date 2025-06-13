package mate.academy.bookstore.dto.cart;

public record CartItemDto(Long id,
                          Long bookId,
                          String bookTitle,
                          int quantity) {
}
