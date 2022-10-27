package ru.practicum.explorewithme.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.dto.NewCategoryDto;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto changeCategory(CategoryDto categoryDto) {
        Category categoryUpdate = categoryMapper.toUpdate(categoryDto);
        Category category = getCategoryById(categoryUpdate.getId());
        if (categoryUpdate.getName().equals(category.getName())) {
            log.error("Указанное имя уже используется в категории");
            throw new ForbiddenException("Имя категории должно отличаться от текущего");
        }
        category.setName(categoryUpdate.getName());
        log.info("Для категории с id {} установлено имя {}", category.getId(), category.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category categoryNew = categoryMapper.toModel(newCategoryDto);
        Category category = categoryRepository.save(categoryNew);
        CategoryDto categoryDto = categoryMapper.toDto(category);
        log.info("Создана категория {}", categoryNew.getName());
        return categoryDto;
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            log.error("Категории {} не существует", id);
            throw new NotFoundException("Категории не существует");
        }
        categoryRepository.deleteById(id);
        log.info("Категория {} удалена", id);
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена категория с id " + id));
    }
}