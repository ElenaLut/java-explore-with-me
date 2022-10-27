package ru.practicum.explorewithme.category;

import ru.practicum.explorewithme.category.dto.CategoryDto;

import java.util.List;

public interface CategoryPublicService {

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryInfo(Long id);
}