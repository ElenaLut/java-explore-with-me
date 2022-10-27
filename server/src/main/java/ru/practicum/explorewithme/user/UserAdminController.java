package ru.practicum.explorewithme.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.user.dto.NewUserDto;
import ru.practicum.explorewithme.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                  @Positive @RequestParam(defaultValue = "10") int size) {
        return userAdminService.getUsers(ids, from, size);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        return userAdminService.createUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userAdminService.deleteUser(userId);
    }
}
