package mate.academy.bookstore.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.exception.OrderProcessingException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.Status;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.cart.CartItemRepository;
import mate.academy.bookstore.repository.cart.ShoppingCartRepository;
import mate.academy.bookstore.repository.order.OrderItemRepository;
import mate.academy.bookstore.repository.order.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    public OrderDto placeOrder(Authentication authentication, CreateOrderRequestDto requestDto) {
        User user = getUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + user.getId()));
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProcessingException("Can't process empty order");
        }
        Order order = orderMapper.toModel(requestDto);
        order.setUser(user);
        order.setOrderItems(mapShoppingCartToOrderItems(shoppingCart, order));
        order.setTotal(calculateTotal(order.getOrderItems()));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.PENDING);

        orderRepository.save(order);
        cartItemRepository.deleteAll(shoppingCart.getCartItems());
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getUserOrders(Authentication authentication, Pageable pageable) {
        User user = getUser(authentication);
        return orderRepository.findOrderByUserId(user.getId(), pageable).map(orderMapper::toDto);
    }

    @Override
    public Page<OrderItemDto> getOrderItems(Long orderId, Authentication authentication,
                                            Pageable pageable) {
        User user = getUser(authentication);
        return orderItemRepository.findByOrderIdAndOrderUserId(
                orderId,
                user.getId(),
                pageable).map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto getOrderItem(Authentication authentication, Long orderId, Long itemId) {
        User user = getUser(authentication);
        return orderItemMapper.toDto(
                orderItemRepository.findByIdAndOrderIdAndOrderUserId(itemId, orderId, user.getId())
                        .orElseThrow(
                                () -> new EntityNotFoundException("Can't find order item with id: "
                                        + itemId))
        );
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find Order with id: "
                        + orderId));
        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private Set<OrderItem> mapShoppingCartToOrderItems(ShoppingCart shoppingCart,
                                                       Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal itemPrice = cartItem.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setPrice(itemPrice);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
