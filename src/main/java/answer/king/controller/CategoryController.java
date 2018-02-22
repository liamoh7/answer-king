package answer.king.controller;

import answer.king.dto.CategoryDto;
import answer.king.error.NotFoundException;
import answer.king.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public ResponseEntity<CategoryDto> get(@PathVariable(value = "id") long id) throws NotFoundException {
        return ResponseEntity.ok(categoryService.getMapped(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto category) {
        System.out.println("Category Controller: " + category.getName());
        return ResponseEntity.ok(categoryService.save(category));
    }
}
