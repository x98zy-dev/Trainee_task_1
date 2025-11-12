package org.x98zy.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.x98zy.user_service.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserDtoService {
    UserDTO createUser(UserDTO userDTO);
    Optional<UserDTO> getUserById(Long id);
    Page<UserDTO> getAllUsers(Pageable pageable);
    Page<UserDTO> getUsersByFilter(String firstName, String lastName, Boolean active, Pageable pageable);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void activateUser(Long id);
    void deactivateUser(Long id);
    void deleteUser(Long id);
    List<UserDTO> getUsersWithActiveCards();
}