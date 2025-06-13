package mate.academy.bookstore.repository.cart;

import java.util.Optional;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
