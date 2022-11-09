package ru.practicum.explorewithme.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortCommentDto {
    @NotBlank
    private String description;
    @NotNull
    private Long event;
    @NotNull
    private Long user;
}
