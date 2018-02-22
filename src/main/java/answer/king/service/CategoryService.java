package answer.king.service;

import answer.king.dto.CategoryDto;
import answer.king.entity.Category;
import answer.king.error.NotFoundException;
import answer.king.repo.CategoryRepository;
import answer.king.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static answer.king.util.Models.throwNotFoundIfNull;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repository;
    private final Mapper<CategoryDto, Category> mapper;

    @Autowired
    public CategoryService(CategoryRepository repository, Mapper<CategoryDto, Category> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Category get(long id) throws NotFoundException {
        final Category category = repository.findOne(id);
        return throwNotFoundIfNull(category);
    }

    public CategoryDto getMapped(long id) throws NotFoundException {
        return mapper.mapToDto(get(id));
    }

    public List<CategoryDto> getAll() {
        List<Category> categories = repository.findAll();
        if (categories == null) categories = new ArrayList<>();
        return mapper.mapToDto(categories);
    }

    public CategoryDto save(CategoryDto category) {
        final Category entity = mapper.mapToEntity(category);
        category = mapper.mapToDto(repository.save(entity));
        return category;
    }
}
