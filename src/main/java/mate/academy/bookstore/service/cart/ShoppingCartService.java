package mate.academy.bookstore.service.cart;

import mate.academy.bookstore.dto.cart.AddToCartRequestDto;
import mate.academy.bookstore.dto.cart.ShoppingCartDto;
import mate.academy.bookstore.dto.cart.UpdateCartItemRequestDto;
import mate.academy.bookstore.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto getCurrentUserCart(Authentication authentication);

    ShoppingCartDto addBookToCart(Authentication authentication, AddToCartRequestDto request);

    ShoppingCartDto updateCartItemQuantity(Authentication authentication,
                                           Long cartItemId, UpdateCartItemRequestDto request);

    ShoppingCartDto removeCartItem(Authentication authentication, Long cartItemId);

    void createShoppingCartForUser(User user);
}

