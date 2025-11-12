package org.x98zy.user_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.x98zy.user_service.entity.User;
import org.x98zy.user_service.exception.BusinessRuleException;
import org.x98zy.user_service.exception.DuplicateResourceException;
import org.x98zy.user_service.exception.ResourceNotFoundException;
import org.x98zy.user_service.repository.UserRepository;
import org.x98zy.user_service.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        // Проверка на уникальность email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsersByFilter(String firstName, String lastName, Boolean active, Pageable pageable) {
        // Временная реализация - потом добавим Specifications
        return userRepository.findAll(pageable);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Проверяем, что email не занят другим пользователем
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email " + userDetails.getEmail() + " is already taken");
        }

        user.setName(userDetails.getName());
        user.setSurname(userDetails.getSurname());
        user.setBirthDate(userDetails.getBirthDate());
        user.setEmail(userDetails.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersWithActiveCards() {
        return userRepository.findUsersWithActiveCards();
    }
}