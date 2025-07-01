package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.TestUtil.buildBook;
import static mate.academy.bookstore.util.TestUtil.buildCartItemDto;
import static mate.academy.bookstore.util.TestUtil.buildCartRequestDto;
import static mate.academy.bookstore.util.TestUtil.buildShoppingCartDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import mate.academy.bookstore.dto.cart.AddToCartRequestDto;
import mate.academy.bookstore.dto.cart.CartItemDto;
import mate.academy.bookstore.dto.cart.ShoppingCartDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.cart.CartItemRepository;
import mate.academy.bookstore.repository.cart.ShoppingCartRepository;
import mate.academy.bookstore.service.cart.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTests {
    private static final Long ID = 1L;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Should return user's shopping cart")
    public void getCurrentUserCart_whenUserIsValid_shouldReturnCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        User user = new User();
        user.setShoppingCart(shoppingCart);

        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));

        CartItemDto cartItem = buildCartItemDto();
        ShoppingCartDto expectedDto = buildShoppingCartDto(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedDto);

        ShoppingCartDto actual = shoppingCartService.getCurrentUserCart(authentication);
        assertEquals(expectedDto, actual);
        verify(authentication).getPrincipal();
        verify(shoppingCartRepository).findByUser(user);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Returns error if shopping cart for authenticated user does not exist")
    public void getCurrentUserCart_whenCartNotFound_shouldThrowEntityNotFoundException() {
        ShoppingCart shoppingCart = new ShoppingCart();
        User user = new User();
        user.setShoppingCart(shoppingCart);

        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.getCurrentUserCart(authentication)
        );
    }

    @Test
    @DisplayName("Should add new CartItem to shopping cart and return updated ShoppingCartDto")
    public void addBookToCart_whenCartItemNotExists_shouldCreateAndReturnUpdatedDto() {
        ShoppingCart shoppingCart = new ShoppingCart();
        User user = new User();
        user.setShoppingCart(shoppingCart);
        Book book = buildBook();
        ShoppingCart updatedCart = new ShoppingCart();
        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(cartItemRepository.findByShoppingCartAndBook(shoppingCart, book))
                .thenReturn(Optional.empty());
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(updatedCart);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(ID, ID, new HashSet<>());
        when(shoppingCartMapper.toDto(updatedCart)).thenReturn(shoppingCartDto);
        AddToCartRequestDto addToCartRequestDto = buildCartRequestDto(book.getId());
        ShoppingCartDto result = shoppingCartService
                .addBookToCart(authentication, addToCartRequestDto);
        assertEquals(shoppingCartDto, result);
        verify(authentication).getPrincipal();
        verify(bookRepository).findById(book.getId());
        verify(cartItemRepository).findByShoppingCartAndBook(shoppingCart, book);
        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartMapper).toDto(updatedCart);
        assertEquals(1, shoppingCart.getCartItems().size());
        CartItem addedItem = shoppingCart.getCartItems().iterator().next();
        assertEquals(book, addedItem.getBook());
        assertEquals(10, addedItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when book ID not found")
    public void addBookToCart_whenBookDoesNotExist_shouldThrowEntityNotFoundException() {
        ShoppingCart shoppingCart = new ShoppingCart();
        User user = new User();
        shoppingCart.setUser(user);
        Long id = 995L;

        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        AddToCartRequestDto addToCartRequestDto = buildCartRequestDto(id);
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToCart(authentication, addToCartRequestDto));
        verify(cartItemRepository, never()).findByShoppingCartAndBook(any(), any());
        verify(shoppingCartRepository, never()).save(any());
    }
}
