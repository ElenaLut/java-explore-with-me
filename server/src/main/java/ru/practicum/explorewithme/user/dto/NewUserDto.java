package ru.practicum.explorewithme.user.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 3, max = 120)
    private String name;
}
