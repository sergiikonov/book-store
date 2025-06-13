package mate.academy.bookstore.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddToCartRequestDto {
    @NotNull
    private Long bookId;
    @Positive
    private int quantity;
}
