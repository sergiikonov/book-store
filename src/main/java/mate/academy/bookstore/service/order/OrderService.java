package mate.academy.bookstore.service.order;

import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderItemDto;
import mate.academy.bookstore.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderDto placeOrder(Authentication authentication, CreateOrderRequestDto requestDto);

    Page<OrderDto> getUserOrders(Authentication authentication, Pageable pageable);

    Page<OrderItemDto> getOrderItems(Long orderId, Authentication authentication,
                                     Pageable pageable);

    OrderItemDto getOrderItem(Authentication authentication, Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long orderId, Status status);
}
