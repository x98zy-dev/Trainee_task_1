package org.x98zy.user_service.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.x98zy.user_service.dto.PaymentCardDTO;
import org.x98zy.user_service.entity.PaymentCard;
import org.x98zy.user_service.mapper.PaymentCardMapper;
import org.x98zy.user_service.repository.PaymentCardRepository;
import org.x98zy.user_service.service.PaymentCardDtoService;
import org.x98zy.user_service.service.PaymentCardService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentCardDtoServiceImpl implements PaymentCardDtoService {

    private final PaymentCardService paymentCardService;
    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;

    public PaymentCardDtoServiceImpl(PaymentCardService paymentCardService,
                                     PaymentCardMapper paymentCardMapper,
                                     PaymentCardRepository paymentCardRepository) {
        this.paymentCardService = paymentCardService;
        this.paymentCardMapper = paymentCardMapper;
        this.paymentCardRepository = paymentCardRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cards", allEntries = true),
            @CacheEvict(value = "userCards", key = "#cardDTO.userId")
    })
    public PaymentCardDTO createCard(PaymentCardDTO cardDTO) {
        PaymentCard card = paymentCardMapper.toEntity(cardDTO);
        PaymentCard savedCard = paymentCardService.createCard(card);
        return paymentCardMapper.toDTO(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cards", key = "#id")
    public Optional<PaymentCardDTO> getCardById(Long id) {
        return paymentCardService.getCardById(id)
                .map(paymentCardMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cards", key = "'page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize")
    public Page<PaymentCardDTO> getAllCards(Pageable pageable) {
        return paymentCardService.getAllCards(pageable)
                .map(paymentCardMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userCards", key = "#userId")
    public List<PaymentCardDTO> getCardsByUserId(Long userId) {
        return paymentCardService.getCardsByUserId(userId)
                .stream()
                .map(paymentCardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(put = {
            @CachePut(value = "cards", key = "#id")
    }, evict = {
            @CacheEvict(value = "userCards", allEntries = true)
    })
    public PaymentCardDTO updateCard(Long id, PaymentCardDTO cardDTO) {
        PaymentCard card = paymentCardMapper.toEntity(cardDTO);
        PaymentCard updatedCard = paymentCardService.updateCard(id, card);
        return paymentCardMapper.toDTO(updatedCard);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cards", key = "#id"),
            @CacheEvict(value = "userCards", allEntries = true)
    })
    public void activateCard(Long id) {
        paymentCardService.activateCard(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cards", key = "#id"),
            @CacheEvict(value = "userCards", allEntries = true)
    })
    public void deactivateCard(Long id) {
        paymentCardService.deactivateCard(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cards", key = "#id"),
            @CacheEvict(value = "cards", allEntries = true),
            @CacheEvict(value = "userCards", allEntries = true)
    })
    public void deleteCard(Long id) {
        paymentCardService.deleteCard(id);
    }
}