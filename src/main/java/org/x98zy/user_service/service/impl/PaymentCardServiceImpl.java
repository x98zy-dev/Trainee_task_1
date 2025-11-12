package org.x98zy.user_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.x98zy.user_service.entity.PaymentCard;
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
        // Проверяем что у пользователя не больше 5 карт
        Long userId = card.getUser().getId();
        long cardCount = paymentCardRepository.countByUserId(userId);
        if (cardCount >= 5) {
            throw new RuntimeException("User cannot have more than 5 payment cards");
        }

        // Проверяем что номер карты уникален
        if (paymentCardRepository.findByNumber(card.getNumber()).isPresent()) {
            throw new RuntimeException("Card with number " + card.getNumber() + " already exists");
        }

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
        return paymentCardRepository.findByUserId(userId);
    }

    @Override
    public PaymentCard updateCard(Long id, PaymentCard cardDetails) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));

        card.setNumber(cardDetails.getNumber());
        card.setHolder(cardDetails.getHolder());
        card.setExpirationDate(cardDetails.getExpirationDate());

        return paymentCardRepository.save(card);
    }

    @Override
    public void activateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        card.setActive(true);
        paymentCardRepository.save(card);
    }

    @Override
    public void deactivateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        card.setActive(false);
        paymentCardRepository.save(card);
    }

    @Override
    public void deleteCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        paymentCardRepository.delete(card);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCardsByUserId(Long userId) {
        return paymentCardRepository.countByUserId(userId);
    }
}