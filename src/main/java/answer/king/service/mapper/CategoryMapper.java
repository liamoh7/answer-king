package answer.king.service.mapper;

import answer.king.dto.CategoryDto;
import answer.king.entity.Category;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper implements Mapper<CategoryDto, Category> {

    private final DozerBeanMapper mapper;

    public CategoryMapper(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CategoryDto mapToDto(Category entity) {
        return mapper.map(entity, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> mapToDto(List<Category> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Category mapToEntity(CategoryDto dto) {
        return mapper.map(dto, Category.class);
    }

    @Override
    public List<Category> mapToEntity(List<CategoryDto> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }
}
