package mate.academy.bookstore.dto.cart;

import java.util.Set;

public record ShoppingCartDto(Long id,
                              Long userId,
                              Set<CartItemDto> cartItems) {
}
