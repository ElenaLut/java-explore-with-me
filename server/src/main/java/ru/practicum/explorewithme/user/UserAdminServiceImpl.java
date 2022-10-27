package ru.practicum.explorewithme.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.user.dto.NewUserDto;
import ru.practicum.explorewithme.user.dto.UserDto;
import ru.practicum.explorewithme.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(Set<Long> id, int from, int size) {
        List<User> users;
        if (id == null) {
            users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            users = userRepository.findUsersByIdIn(id, PageRequest.of(from / size, size));
        }
        log.info("Получены все пользователи");
        return users
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserDto newUserDto) {
        User user = userMapper.toUser(newUserDto);
        log.info("Пользователь {} сохранен", user.getId());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.error("Пользователь {} не существует", id);
            throw new NotFoundException("Удалить можно только существующих пользователей");
        }
        userRepository.deleteById(id);
        log.info("Удален пользователь {}", id);
    }
}
