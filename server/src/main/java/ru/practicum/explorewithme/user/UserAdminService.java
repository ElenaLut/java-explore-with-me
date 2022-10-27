package ru.practicum.explorewithme.user;

import ru.practicum.explorewithme.user.dto.NewUserDto;
import ru.practicum.explorewithme.user.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserAdminService {

    List<UserDto> getUsers(Set<Long> id, int from, int size);

    UserDto createUser(NewUserDto newUserDto);

    void deleteUser(Long id);
}
