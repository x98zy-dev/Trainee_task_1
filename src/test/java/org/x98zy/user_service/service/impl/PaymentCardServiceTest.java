package org.x98zy.user_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.x98zy.user_service.entity.PaymentCard;
import org.x98zy.user_service.entity.User;
import org.x98zy.user_service.exception.BusinessRuleException;
import org.x98zy.user_service.exception.DuplicateResourceException;
import org.x98zy.user_service.exception.ResourceNotFoundException;
import org.x98zy.user_service.repository.PaymentCardRepository;
import org.x98zy.user_service.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Test
    void createCard_Success() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        user.setId(1L);

        PaymentCard card = new PaymentCard("1234567890123456", "JOHN DOE", LocalDate.now().plusYears(2));
        card.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(2L);
        when(paymentCardRepository.findByNumber("1234567890123456")).thenReturn(Optional.empty());
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(card);

        // When
        PaymentCard result = paymentCardService.createCard(card);

        // Then
        assertNotNull(result);
        assertEquals("1234567890123456", result.getNumber());
        assertEquals("JOHN DOE", result.getHolder());

        verify(userRepository).findById(1L);
        verify(paymentCardRepository).countByUserId(1L);
        verify(paymentCardRepository).findByNumber("1234567890123456");
        verify(paymentCardRepository).save(card);
    }

    @Test
    void createCard_UserNotFound_ThrowsException() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        user.setId(1L);

        PaymentCard card = new PaymentCard("1234567890123456", "JOHN DOE", LocalDate.now().plusYears(2));
        card.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.createCard(card));
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void createCard_TooManyCards_ThrowsException() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        user.setId(1L);

        PaymentCard card = new PaymentCard("1234567890123456", "JOHN DOE", LocalDate.now().plusYears(2));
        card.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(5L);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> paymentCardService.createCard(card));
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void createCard_DuplicateCardNumber_ThrowsException() {
        // Given
        User user = new User("John", "Doe", "john@test.com");
        user.setId(1L);

        PaymentCard existingCard = new PaymentCard("1234567890123456", "JOHN DOE", LocalDate.now().plusYears(2));
        PaymentCard newCard = new PaymentCard("1234567890123456", "JANE SMITH", LocalDate.now().plusYears(3));

        newCard.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(2L);
        when(paymentCardRepository.findByNumber("1234567890123456")).thenReturn(Optional.of(existingCard));

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> paymentCardService.createCard(newCard));
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void getCardById_CardExists_ReturnsCard() {
        // Given
        PaymentCard card = new PaymentCard("1234567890123456", "JOHN DOE", LocalDate.now().plusYears(2));
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));

        // When
        Optional<PaymentCard> result = paymentCardService.getCardById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("1234567890123456", result.get().getNumber());
    }

    @Test
    void getCardsByUserId_UserExists_ReturnsCards() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        paymentCardService.getCardsByUserId(1L);

        // Then
        verify(paymentCardRepository).findByUserId(1L);
    }

    @Test
    void getCardsByUserId_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.getCardsByUserId(1L));
        verify(paymentCardRepository, never()).findByUserId(1L);
    }

    @Test
    void updateCard_Success() {
        // Given
        PaymentCard existingCard = new PaymentCard("1234567890123456", "JOHN DOE", LocalDate.now().plusYears(2));
        PaymentCard updateData = new PaymentCard("6543210987654321", "JANE SMITH", LocalDate.now().plusYears(3));

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(existingCard));
        when(paymentCardRepository.findByNumber("6543210987654321")).thenReturn(Optional.empty());
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(updateData);

        // When
        PaymentCard result = paymentCardService.updateCard(1L, updateData);

        // Then
        assertEquals("6543210987654321", result.getNumber());
        assertEquals("JANE SMITH", result.getHolder());
    }

    @Test
    void activateCard_Success() {
        // Given
        PaymentCard card = new PaymentCard("1234567890123456", "JOHN DOE", LocalDate.now().plusYears(2));
        card.setActive(false);

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(card);

        // When
        paymentCardService.activateCard(1L);

        // Then
        assertTrue(card.getActive());
        verify(paymentCardRepository).save(card);
    }
}