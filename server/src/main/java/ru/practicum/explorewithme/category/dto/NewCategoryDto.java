package ru.practicum.explorewithme.category.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank
    @Size(min = 3, max = 120)
    private String name;
}