package org.x98zy.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.x98zy.user_service.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Page<User> getAllUsers(Pageable pageable);
    Page<User> getUsersByFilter(String firstName, String lastName, Boolean active, Pageable pageable);
    User updateUser(Long id, User userDetails);
    void activateUser(Long id);
    void deactivateUser(Long id);
    void deleteUser(Long id);
    List<User> getUsersWithActiveCards();
}