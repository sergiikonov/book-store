package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderItemDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.bookstore.service.order.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order controller", description = "All endpoints for managing Orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get order history",
            description = "Get paginated list of orders placed by the authenticated user")
    public Page<OrderDto> getUserOrderHistory(Authentication authentication, Pageable pageable) {
        return orderService.getUserOrders(authentication, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Place a new order",
            description = "Create and place a new order from the shopping cart")
    public OrderDto placeOrder(Authentication authentication,
                                @RequestBody @Valid CreateOrderRequestDto requestDto) {
        return orderService.placeOrder(authentication, requestDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status",
            description = "Update the status of a specific order (admin only)")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid UpdateOrderRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto.status());
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get items of specific order",
            description = "Retrieve all order items for a given order by the authenticated user")
    public Page<OrderItemDto> getAllItemsForSpecificOrder(@PathVariable Long orderId,
                                                          Authentication authentication,
                                                          Pageable pageable) {
        return orderService.getOrderItems(orderId, authentication, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get specific item from order",
            description = "Retrieve a specific item from a user's order")
    public OrderItemDto getSpecificOrderItemFromOrder(@PathVariable Long orderId,
                                                            Authentication authentication,
                                                            @PathVariable Long itemId) {
        return orderService.getOrderItem(authentication, orderId, itemId);
    }
}
