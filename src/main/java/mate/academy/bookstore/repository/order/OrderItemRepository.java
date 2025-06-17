package mate.academy.bookstore.repository.order;

import java.util.Optional;
import mate.academy.bookstore.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findByOrderIdAndOrderUserId(Long id, Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"book"})
    Optional<OrderItem> findByIdAndOrderIdAndOrderUserId(Long id, Long orderId, Long userId);
}
