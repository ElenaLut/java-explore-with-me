package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.comment.CommentAdminService;
import ru.practicum.explorewithme.comment.CommentMapper;
import ru.practicum.explorewithme.comment.CommentPrivateService;
import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.dto.ShortCommentDto;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentState;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentAdminServiceTest {

    @Autowired
    private final CommentAdminService service;

    @Autowired
    private final CommentPrivateService servicePrivate;

    @Autowired
    private final CommentMapper mapper;

    @Autowired
    private final TestEntityManager testEntityManager;

    private static AtomicLong userIdHolder;
    private static AtomicLong eventIdHolder;
    private static AtomicLong commentIdHolder;

    @BeforeAll
    public static void init() {
        userIdHolder = new AtomicLong();
        eventIdHolder = new AtomicLong();
        commentIdHolder = new AtomicLong();
    }

    @BeforeEach
    public void beforeEachCommentServiceTests() {
        testEntityManager.clear();
    }

    @Test
    public void getCommentsOkTest() {
        Event event = generateEvent();
        User user = generateUser();
        ShortCommentDto newComment = ShortCommentDto.builder()
                .description("Comment")
                .event(event.getId())
                .user(user.getId())
                .build();
        CommentDto commentDto = servicePrivate.createComment(newComment, user.getId());
        List<CommentDto> commentsOfUser = List.of(commentDto);
        List<CommentDto> commentDtoList = service.getAllComments(0, 10);
        assertEquals(commentsOfUser, commentDtoList);
    }

    @Test
    public void updateCommentOkTest() {
        Comment comment = generateComment();
        CommentDto commentDto = mapper.toCommentDto(comment);
        CommentDto updated = commentDto;
        updated.setStatus(CommentState.PUBLISHED);
        CommentDto actual = service.updateStateComment(updated.getId(), updated);
        assertEquals(updated, actual);
    }

    @Test
    public void updateCommentWithWrongIdTest() {
        Comment comment = generateComment();
        CommentDto commentDto = mapper.toCommentDto(comment);
        CommentDto updated = commentDto;
        updated.setStatus(CommentState.PUBLISHED);
        assertThrows(NotFoundException.class,
                () -> service.updateStateComment(89L, updated));
    }

    private User generateUser() {
        User user = User.builder()
                .name(String.format("user-%s", userIdHolder.incrementAndGet()))
                .email(String.format("email-%s@yandex.ru", userIdHolder.get()))
                .build();
        return testEntityManager.persist(user);
    }

    private Event generateEvent() {
        Event event = Event.builder()
                .title(String.format("title-%s", eventIdHolder.incrementAndGet()))
                .annotation(String.format("annotation-%s", eventIdHolder.get()))
                .description(String.format("description-%s", eventIdHolder.get()))
                .category(generateCategory(String.format("category-%s", eventIdHolder.get())))
                .createdOn(LocalDateTime.now())
                .eventDate(LocalDateTime.now().plusMonths(1))
                .paid(true)
                .initiator(generateUser())
                .participantLimit(800)
                .requestModeration(false)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .state(EventState.PUBLISHED)
                .build();
        return testEntityManager.persist(event);
    }

    private Comment generateComment() {
        Comment comment = Comment.builder()
                .description(String.format("comment-%s", commentIdHolder.incrementAndGet()))
                .event(generateEvent())
                .user(generateUser())
                .created(LocalDateTime.now())
                .status(CommentState.PENDING)
                .build();
        return testEntityManager.persist(comment);
    }

    private Category generateCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .build();
        return testEntityManager.persistAndFlush(category);
    }
}

