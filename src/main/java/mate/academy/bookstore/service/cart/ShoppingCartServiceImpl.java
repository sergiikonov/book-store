package mate.academy.bookstore.service.cart;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cart.AddToCartRequestDto;
import mate.academy.bookstore.dto.cart.ShoppingCartDto;
import mate.academy.bookstore.dto.cart.UpdateCartItemRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.cart.CartItemRepository;
import mate.academy.bookstore.repository.cart.ShoppingCartRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getCurrentUserCart(Authentication authentication) {
        return shoppingCartMapper.toDto(getShoppingCart(authentication));
    }

    @Override
    public ShoppingCartDto addBookToCart(Authentication authentication,
                                         AddToCartRequestDto request) {
        ShoppingCart shoppingCart = getShoppingCart(authentication);
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id:" + request.getBookId())
        );

        Optional<CartItem> existingItem = cartItemRepository
                .findByShoppingCartAndBook(shoppingCart, book);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setQuantity(request.getQuantity());
            shoppingCart.getCartItems().add(cartItem);
        }
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCartDto updateCartItemQuantity(Authentication authentication,
                                                  Long cartItemId,
                                                  UpdateCartItemRequestDto request) {
        ShoppingCart shoppingCart = getShoppingCart(authentication);
        CartItem cartItem = getCartItemByIdAndShoppingCart(cartItemId, shoppingCart);
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Override
    public ShoppingCartDto removeCartItem(Authentication authentication, Long cartItemId) {
        ShoppingCart shoppingCart = getShoppingCart(authentication);
        CartItem cartItem = getCartItemByIdAndShoppingCart(cartItemId, shoppingCart);
        shoppingCart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private ShoppingCart getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartRepository
                        .findByUser(user)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Can't find user's shopping cart")
                        );
    }

    private CartItem getCartItemByIdAndShoppingCart(Long cartItemId,
                                                    ShoppingCart shoppingCart) {
        return cartItemRepository.findByIdAndShoppingCart(cartItemId, shoppingCart)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cart item with id: "
                                + cartItemId)
                );
    }
}
