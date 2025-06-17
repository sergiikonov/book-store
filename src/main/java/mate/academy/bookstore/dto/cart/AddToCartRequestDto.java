package mate.academy.bookstore.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddToCartRequestDto {
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}
