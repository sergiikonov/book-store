package mate.academy.bookstore.repository.cart;

import java.util.Optional;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCart(Long id, ShoppingCart cart);

    Optional<CartItem> findByShoppingCartAndBook(ShoppingCart shoppingCart, Book book);
}
