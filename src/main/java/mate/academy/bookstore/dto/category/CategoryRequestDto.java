package mate.academy.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotBlank
    @Size(min = 3, max = 65)
    private String name;
    @Size(min = 3)
    private String description;
}
