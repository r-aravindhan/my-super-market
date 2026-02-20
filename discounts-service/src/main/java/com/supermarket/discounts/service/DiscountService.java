package com.supermarket.discounts.service;

import com.supermarket.discounts.dto.DiscountApplicationRequest;
import com.supermarket.discounts.dto.DiscountApplicationResponse;
import com.supermarket.discounts.dto.DiscountRequest;
import com.supermarket.discounts.dto.DiscountResponse;
import com.supermarket.discounts.entity.Discount;
import com.supermarket.discounts.exception.InvalidDiscountException;
import com.supermarket.discounts.exception.ResourceNotFoundException;
import com.supermarket.discounts.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final DiscountRepository discountRepository;

    @Transactional
    public DiscountResponse createDiscount(DiscountRequest request) {
        Discount discount = toEntity(request);
        return toResponse(discountRepository.save(discount));
    }

    public DiscountResponse getDiscount(String code) {
        return toResponse(findByCode(code));
    }

    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DiscountResponse> getActiveDiscounts() {
        LocalDate today = LocalDate.now();
        return discountRepository
                .findByActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(today, today)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiscountResponse updateDiscount(String code, DiscountRequest request) {
        Discount discount = findByCode(code);
        updateEntity(discount, request);
        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public void deleteDiscount(String code) {
        Discount discount = findByCode(code);
        discountRepository.delete(discount);
    }

    public DiscountApplicationResponse applyDiscount(DiscountApplicationRequest request) {
        BigDecimal discountAmount = BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);
        String appliedCode = null;
        Map<String, BigDecimal> itemDiscounts = new HashMap<>();

        if (request.getDiscountCode() != null && !request.getDiscountCode().isBlank()) {
            Discount discount = discountRepository.findById(request.getDiscountCode())
                    .orElseThrow(() -> new InvalidDiscountException("Discount code not found: " + request.getDiscountCode()));

            validateDiscount(discount, request);

            switch (discount.getType()) {
                case PERCENTAGE -> {
                    BigDecimal percentage = discount.getValue().divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
                    discountAmount = request.getSubtotal().multiply(percentage).setScale(SCALE, ROUNDING_MODE);
                    appliedCode = discount.getCode();
                }
                case FIXED_AMOUNT -> {
                    discountAmount = discount.getValue().min(request.getSubtotal()).setScale(SCALE, ROUNDING_MODE);
                    appliedCode = discount.getCode();
                }
                case BUY_X_GET_Y -> {
                    if (discount.getTargetSku() != null) {
                        Integer quantity = request.getItems().get(discount.getTargetSku());
                        BigDecimal itemPrice = request.getItemPrices().get(discount.getTargetSku());
                        if (quantity != null && quantity >= discount.getMinQuantity() && itemPrice != null) {
                            int freeItems = quantity / discount.getMinQuantity();
                            discountAmount = itemPrice.multiply(BigDecimal.valueOf(freeItems))
                                    .divide(BigDecimal.valueOf(quantity), SCALE, ROUNDING_MODE)
                                    .multiply(BigDecimal.valueOf(quantity))
                                    .setScale(SCALE, ROUNDING_MODE);
                            itemDiscounts.put(discount.getTargetSku(), discountAmount);
                            appliedCode = discount.getCode();
                        }
                    }
                }
            }
        }

        BigDecimal totalAfterDiscount = request.getSubtotal().subtract(discountAmount).setScale(SCALE, ROUNDING_MODE);

        return DiscountApplicationResponse.builder()
                .discountAmount(discountAmount)
                .totalAfterDiscount(totalAfterDiscount.max(BigDecimal.ZERO))
                .appliedDiscountCode(appliedCode)
                .itemDiscounts(itemDiscounts.isEmpty() ? null : itemDiscounts)
                .build();
    }

    private void validateDiscount(Discount discount, DiscountApplicationRequest request) {
        if (!discount.isActive()) {
            throw new InvalidDiscountException("Discount code is not active: " + discount.getCode());
        }
        LocalDate today = LocalDate.now();
        if (discount.getValidFrom() != null && today.isBefore(discount.getValidFrom())) {
            throw new InvalidDiscountException("Discount not yet valid: " + discount.getCode());
        }
        if (discount.getValidTo() != null && today.isAfter(discount.getValidTo())) {
            throw new InvalidDiscountException("Discount has expired: " + discount.getCode());
        }
        if (discount.getMinPurchaseAmount() != null &&
                request.getSubtotal().compareTo(discount.getMinPurchaseAmount()) < 0) {
            throw new InvalidDiscountException(
                    "Minimum purchase of " + discount.getMinPurchaseAmount() + " required for " + discount.getCode());
        }
    }

    private Discount findByCode(String code) {
        return discountRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", code));
    }

    private Discount toEntity(DiscountRequest request) {
        return Discount.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .type(request.getType())
                .value(request.getValue())
                .targetSku(request.getTargetSku())
                .targetCategory(request.getTargetCategory())
                .minQuantity(request.getMinQuantity())
                .minPurchaseAmount(request.getMinPurchaseAmount())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .active(request.isActive())
                .build();
    }

    private void updateEntity(Discount discount, DiscountRequest request) {
        discount.setDescription(request.getDescription());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setTargetSku(request.getTargetSku());
        discount.setTargetCategory(request.getTargetCategory());
        discount.setMinQuantity(request.getMinQuantity());
        discount.setMinPurchaseAmount(request.getMinPurchaseAmount());
        discount.setValidFrom(request.getValidFrom());
        discount.setValidTo(request.getValidTo());
        discount.setActive(request.isActive());
    }

    private DiscountResponse toResponse(Discount discount) {
        return DiscountResponse.builder()
                .code(discount.getCode())
                .description(discount.getDescription())
                .type(discount.getType())
                .value(discount.getValue())
                .targetSku(discount.getTargetSku())
                .targetCategory(discount.getTargetCategory())
                .minQuantity(discount.getMinQuantity())
                .minPurchaseAmount(discount.getMinPurchaseAmount())
                .validFrom(discount.getValidFrom())
                .validTo(discount.getValidTo())
                .active(discount.isActive())
                .build();
    }
}
