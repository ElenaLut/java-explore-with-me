package ru.practicum.explorewithme.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.comment.dto.CommentDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @GetMapping
    public List<CommentDto> getAllComments(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {
        return commentAdminService.getAllComments(from, size);
    }

    @PutMapping("{commentId}")
    public CommentDto updateStateComment(@PathVariable Long commentId,
                                         @RequestBody CommentDto commentDto) {
        return commentAdminService.updateStateComment(commentId, commentDto);
    }
}
