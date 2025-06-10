package mate.academy.bookstore.mapper;

import java.util.List;
import java.util.stream.Collectors;
import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto bookDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        List<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        bookDto.setCategoryIds(categoryIds);
    }
}
