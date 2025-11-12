package org.x98zy.user_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.x98zy.user_service.entity.PaymentCard;
import org.x98zy.user_service.entity.User;
import org.x98zy.user_service.exception.BusinessRuleException;
import org.x98zy.user_service.exception.DuplicateResourceException;
import org.x98zy.user_service.exception.ResourceNotFoundException;
import org.x98zy.user_service.repository.PaymentCardRepository;
import org.x98zy.user_service.repository.UserRepository;
import org.x98zy.user_service.service.PaymentCardService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public PaymentCardServiceImpl(PaymentCardRepository paymentCardRepository,
                                  UserRepository userRepository) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PaymentCard createCard(PaymentCard card) {
        // Проверяем существование пользователя
        User user = userRepository.findById(card.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + card.getUser().getId()));

        // Проверяем что у пользователя не больше 5 карт
        Long userId = card.getUser().getId();
        long cardCount = paymentCardRepository.countByUserId(userId);
        if (cardCount >= 5) {
            throw new BusinessRuleException("User cannot have more than 5 payment cards");
        }

        // Проверяем что номер карты уникален
        if (paymentCardRepository.findByNumber(card.getNumber()).isPresent()) {
            throw new DuplicateResourceException("Card with number " + card.getNumber() + " already exists");
        }

        card.setUser(user);
        return paymentCardRepository.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentCard> getCardById(Long id) {
        return paymentCardRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentCard> getAllCards(Pageable pageable) {
        return paymentCardRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCard> getCardsByUserId(Long userId) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return paymentCardRepository.findByUserId(userId);
    }

    @Override
    public PaymentCard updateCard(Long id, PaymentCard cardDetails) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        // Проверяем уникальность номера карты (если изменился)
        if (!card.getNumber().equals(cardDetails.getNumber()) &&
                paymentCardRepository.findByNumber(cardDetails.getNumber()).isPresent()) {
            throw new DuplicateResourceException("Card with number " + cardDetails.getNumber() + " already exists");
        }

        card.setNumber(cardDetails.getNumber());
        card.setHolder(cardDetails.getHolder());
        card.setExpirationDate(cardDetails.getExpirationDate());

        return paymentCardRepository.save(card);
    }

    @Override
    public void activateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        card.setActive(true);
        paymentCardRepository.save(card);
    }

    @Override
    public void deactivateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        card.setActive(false);
        paymentCardRepository.save(card);
    }

    @Override
    public void deleteCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        paymentCardRepository.delete(card);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCardsByUserId(Long userId) {
        return paymentCardRepository.countByUserId(userId);
    }
}