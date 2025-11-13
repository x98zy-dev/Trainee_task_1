package org.x98zy.user_service.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentCardDTO {
    private Long id;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotBlank(message = "Card number is mandatory")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String number;

    @NotBlank(message = "Card holder is mandatory")
    @Size(min = 2, max = 100, message = "Holder name must be between 2 and 100 characters")
    private String holder;

    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentCardDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getHolder() { return holder; }
    public void setHolder(String holder) { this.holder = holder; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}