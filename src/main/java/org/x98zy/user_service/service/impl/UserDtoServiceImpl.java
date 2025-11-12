package org.x98zy.user_service.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.x98zy.user_service.dto.UserDTO;
import org.x98zy.user_service.entity.User;
import org.x98zy.user_service.mapper.UserMapper;
import org.x98zy.user_service.repository.UserRepository;
import org.x98zy.user_service.service.UserDtoService;
import org.x98zy.user_service.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDtoServiceImpl implements UserDtoService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDtoServiceImpl(UserService userService, UserMapper userMapper, UserRepository userRepository) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "usersWithCards", allEntries = true)
    })
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userService.createUser(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public Optional<UserDTO> getUserById(Long id) {
        return userService.getUserById(id)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'filter_' + #firstName + '_' + #lastName + '_' + #active + '_page_' + #pageable.pageNumber")
    public Page<UserDTO> getUsersByFilter(String firstName, String lastName, Boolean active, Pageable pageable) {
        // Временная реализация
        return userService.getAllUsers(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "users", key = "#id")
    }, evict = {
            @CacheEvict(value = "usersWithCards", allEntries = true)
    })
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.updateUser(id, user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "usersWithCards", allEntries = true)
    })
    public void activateUser(Long id) {
        userService.activateUser(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "usersWithCards", allEntries = true)
    })
    public void deactivateUser(Long id) {
        userService.deactivateUser(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "usersWithCards", allEntries = true)
    })
    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "usersWithCards")
    public List<UserDTO> getUsersWithActiveCards() {
        return userService.getUsersWithActiveCards()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}