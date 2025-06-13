package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cart.AddToCartRequestDto;
import mate.academy.bookstore.dto.cart.ShoppingCartDto;
import mate.academy.bookstore.dto.cart.UpdateCartItemRequestDto;
import mate.academy.bookstore.service.cart.ShoppingCartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@Tag(name = "Shopping Cart controller",
        description = "All endpoints of a Shopping Cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get users shopping cart method",
            description = "Get Shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getCurrentUserCart(authentication);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Add book to cart method",
            description = "Add book to cart")
    public ShoppingCartDto addBookToCart(Authentication authentication,
                                         @RequestBody @Valid AddToCartRequestDto request) {
        return shoppingCartService.addBookToCart(authentication, request);
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update quantity method",
            description = "Update cart items quantity in shopping cart")
    public ShoppingCartDto updateCartItemsQuantity(@PathVariable Long id,
                                                   Authentication authentication,
                                                   @RequestBody @Valid
                                                       UpdateCartItemRequestDto request) {
        return shoppingCartService.updateCartItemQuantity(authentication, id, request);
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Remove cart item method",
            description = "Remove cart item from shopping cart")
    public ShoppingCartDto deleteCartItemFromShoppingCart(@PathVariable Long id,
                                                          Authentication authentication) {
        return shoppingCartService.removeCartItem(authentication, id);
    }
}
