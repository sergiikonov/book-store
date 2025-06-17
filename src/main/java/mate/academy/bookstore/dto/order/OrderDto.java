package mate.academy.bookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import mate.academy.bookstore.model.Status;

public record OrderDto(
            Long id,
            Long userId,
            List<OrderItemDto> orderItems,
            LocalDateTime orderDate,
            BigDecimal total,
            Status status
) {}
