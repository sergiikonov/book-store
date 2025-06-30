package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.TestUtil.buildValidCategory;
import static mate.academy.bookstore.util.TestUtil.buildValidCategoryDto;
import static mate.academy.bookstore.util.TestUtil.buildValidCategoryRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CategoryRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CategoryMapper;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.category.CategoryRepository;
import mate.academy.bookstore.service.category.CategoryServiceImpl;
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
public class CategoryServiceTests {
    private static final Long ID = 1L;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Should return page of all categories")
    public void findAll_showAllCategories_returnPageOfCategories() {
        Pageable pageable = PageRequest.of(0, 2);
        Category category = buildValidCategory();
        List<Category> categories = List.of(category);
        PageImpl<Category> categoryPage = new PageImpl<>(categories);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        CategoryDto categoryDto = buildValidCategoryDto();
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        assertEquals(1, actual.getTotalElements());
        assertEquals(categoryDto, actual.getContent().get(0));
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should return category by ID")
    public void getById_findCategoryById_returnValidCategoryById() {
        Category category = buildValidCategory();
        CategoryDto categoryDto = buildValidCategoryDto();

        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        CategoryDto actual = categoryService.getById(ID);
        assertEquals(actual, categoryDto);

        verify(categoryRepository).findById(ID);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    public void findById_categoryNotFound_shouldThrowException() {
        Long id = 995L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(id));
    }

    @Test
    @DisplayName("Should return updated category DTO when category exists")
    public void update_existingCategory_shouldReturnUpdatedDto() {
        Category category = buildValidCategory();
        CategoryDto categoryDto = buildValidCategoryDto();
        CategoryRequestDto requestDto = buildValidCategoryRequestDto();

        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateCategoryFromDto(requestDto, category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.update(ID, requestDto);
        assertEquals(categoryDto, actual);

        verify(categoryRepository).findById(ID);
        verify(categoryMapper).updateCategoryFromDto(requestDto, category);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing category")
    public void update_nonExistingCategory_shouldThrowEntityNotFoundException() {
        Long id = 995L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(id, new CategoryRequestDto())
        );
    }
}
