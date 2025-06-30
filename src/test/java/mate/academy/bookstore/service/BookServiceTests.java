package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.TestUtil.buildValidBook;
import static mate.academy.bookstore.util.TestUtil.buildValidBookDto;
import static mate.academy.bookstore.util.TestUtil.buildValidCreateRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.category.CategoryRepository;
import mate.academy.bookstore.service.book.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {
    private static final Long ID = 1L;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Should save book with valid params and return BookDto")
    public void save_whenBookIsValid_shouldReturnBookDto() {
        CreateBookRequestDto requestDto = buildValidCreateRequestDto();
        Book book = buildValidBook();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        List<Category> categories = List.of(new Category());
        when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(categories);
        when(bookRepository.save(book)).thenReturn(book);

        BookDto bookDto = buildValidBookDto();

        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto actual = bookService.save(requestDto);
        assertEquals(actual, bookDto);
        verify(bookMapper).toModel(requestDto);
        verify(categoryRepository).findAllById(requestDto.getCategoryIds());
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should return page of all books")
    public void findAll_whenCalled_shouldReturnPageOfBookDto() {
        Book book = buildValidBook();
        Pageable pageable = PageRequest.of(0, 2);
        List<Book> books = List.of(book);
        PageImpl<Book> bookPage = new PageImpl<>(books);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        BookDto bookDto = buildValidBookDto();
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        Page<BookDto> result = bookService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(bookDto, result.getContent().get(0));
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should return book by ID")
    public void findById_whenBookExists_shouldReturnBookDto() {
        Book book = buildValidBook();
        BookDto bookDto = buildValidBookDto();

        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto actual = bookService.findById(ID);
        assertEquals(bookDto, actual);

        verify(bookRepository).findById(ID);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    public void findById_whenBookNotFound_shouldThrowException() {
        Long id = 995L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class, () -> bookService.findById(id));
    }

    @Test
    @DisplayName("Should delete book by ID")
    public void deleteById_whenIdIsValid_shouldCallRepository() {
        doNothing().when(bookRepository).deleteById(ID);
        bookService.deleteById(ID);
        verify(bookRepository).deleteById(ID);
    }
}
