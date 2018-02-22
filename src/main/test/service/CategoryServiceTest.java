package service;

import answer.king.dto.CategoryDto;
import answer.king.entity.Category;
import answer.king.error.NotFoundException;
import answer.king.repo.CategoryRepository;
import answer.king.service.CategoryService;
import answer.king.service.mapper.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository mockRepository;
    @Mock
    private Mapper<CategoryDto, Category> mockMapper;
    private CategoryService service;

    @Before
    public void setUp() {
        service = new CategoryService(mockRepository, mockMapper);
    }

    public void creatingCategorySuccessfully() {
        final CategoryDto expected = new CategoryDto("Cat 1");

        when(mockMapper.mapToEntity(any(CategoryDto.class))).thenReturn(new Category());
        when(mockRepository.save(any(Category.class))).thenReturn(new Category("Cat 1"));
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(expected);

        final CategoryDto actual = service.save(expected);

        assertEquals(expected, actual);
        verify(mockMapper, times(1)).mapToEntity(any(CategoryDto.class));
        verify(mockRepository, times(1)).save(any(Category.class));
        verify(mockMapper, times(1)).mapToDto(any(Category.class));
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test
    public void getAllCategoriesReturnsListOfCategoriesWhenAvailable() {
        final List<Category> categories = Arrays.asList(
                new Category("Cat 1"),
                new Category("Cat 2"));

        final List<CategoryDto> expected = Arrays.asList(
                new CategoryDto("Cat 1"),
                new CategoryDto("Cat 2"));

        when(mockRepository.findAll()).thenReturn(categories);
        when(mockMapper.mapToDto(anyList())).thenReturn(expected);

        final List<CategoryDto> actual = service.getAll();

        assertEquals(expected, actual);
        verify(mockRepository, times(1)).findAll();
        verify(mockMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test
    public void getAllCategoriesReturnsEmptyListWhenNoneAreAvailable() {
        final List<Category> entities = Collections.emptyList();
        final List<CategoryDto> expectedDtos = Collections.emptyList();

        when(mockRepository.findAll()).thenReturn(entities);
        when(mockMapper.mapToDto(anyList())).thenReturn(expectedDtos);

        final List<CategoryDto> actualItems = service.getAll();

        assertEquals(expectedDtos, actualItems);
        verify(mockRepository, times(1)).findAll();
        verify(mockMapper, times(1)).mapToDto(anyList());
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test
    public void getCategoryWithValidIdReturnsCategory() throws NotFoundException {
        final Category category = new Category("Drinks");
        final CategoryDto categoryDto = new CategoryDto("Drinks");

        when(mockRepository.findOne(anyLong())).thenReturn(category);
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(categoryDto);

        final CategoryDto actual = service.getMapped(0L);

        assertEquals(categoryDto, actual);
        verify(mockRepository, times(1)).findOne(0L);
        verify(mockMapper, times(1)).mapToDto(any(Category.class));
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockMapper);
    }

    @Test(expected = NotFoundException.class)
    public void getCategoryWithInvalidIdThrowsException() throws NotFoundException {
        when(mockRepository.findOne(anyLong())).thenReturn(null);

        service.get(0L);
    }
}
