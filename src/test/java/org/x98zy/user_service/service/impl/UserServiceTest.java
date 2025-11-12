package org.x98zy.user_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.x98zy.user_service.entity.User;
import org.x98zy.user_service.exception.DuplicateResourceException;
import org.x98zy.user_service.exception.ResourceNotFoundException;
import org.x98zy.user_service.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_Success() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.createUser(user);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals("john@test.com", result.getEmail());

        verify(userRepository).findByEmail("john@test.com");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_EmailExists_ThrowsException() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
    }

    @Test
    void getUserById_UserNotFound_ReturnsEmpty() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void updateUser_UserExists_Success() {
        // Given
        User existingUser = new User("John", "Doe", "john@test.com");
        User updateData = new User("Jane", "Smith", "jane@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updateData);

        // When
        User result = userService.updateUser(1L, updateData);

        // Then
        assertEquals("Jane", result.getName());
        assertEquals("Smith", result.getSurname());
        assertEquals("jane@test.com", result.getEmail());
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Given
        User updateData = new User("Jane", "Smith", "jane@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateData));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void activateUser_Success() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        user.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.activateUser(1L);

        // Then
        assertTrue(user.getActive());
        verify(userRepository).save(user);
    }
}