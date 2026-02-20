package com.supermarket.discounts.service;

import com.supermarket.discounts.dto.DiscountApplicationRequest;
import com.supermarket.discounts.dto.DiscountRequest;
import com.supermarket.discounts.entity.Discount;
import com.supermarket.discounts.exception.InvalidDiscountException;
import com.supermarket.discounts.exception.ResourceNotFoundException;
import com.supermarket.discounts.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiscountService Unit Tests")
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    private DiscountRequest validDiscountRequest;

    @BeforeEach
    void setUp() {
        validDiscountRequest = DiscountRequest.builder()
                .code("SAVE10")
                .description("10% off")
                .type(Discount.DiscountType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .minPurchaseAmount(new BigDecimal("50"))
                .validFrom(LocalDate.now().minusDays(1))
                .validTo(LocalDate.now().plusDays(30))
                .active(true)
                .build();
    }

    @Nested
    @DisplayName("createDiscount")
    class CreateDiscountTests {

        @Test
        void shouldCreateDiscountSuccessfully() {
            var discount = Discount.builder()
                    .code("SAVE10")
                    .type(Discount.DiscountType.PERCENTAGE)
                    .value(new BigDecimal("10"))
                    .active(true)
                    .build();
            when(discountRepository.save(any())).thenReturn(discount);

            var response = discountService.createDiscount(validDiscountRequest);

            assertThat(response.getCode()).isEqualTo("SAVE10");
            assertThat(response.getType()).isEqualTo(Discount.DiscountType.PERCENTAGE);
        }
    }

    @Nested
    @DisplayName("getDiscount")
    class GetDiscountTests {

        @Test
        void shouldThrowWhenDiscountNotFound() {
            when(discountRepository.findById("INVALID")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> discountService.getDiscount("INVALID"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("INVALID");
        }
    }

    @Nested
    @DisplayName("applyDiscount")
    class ApplyDiscountTests {

        @Test
        void shouldApplyPercentageDiscount() {
            var discount = Discount.builder()
                    .code("SAVE10")
                    .type(Discount.DiscountType.PERCENTAGE)
                    .value(new BigDecimal("10"))
                    .minPurchaseAmount(new BigDecimal("50"))
                    .validFrom(LocalDate.now().minusDays(1))
                    .validTo(LocalDate.now().plusDays(30))
                    .active(true)
                    .build();
            when(discountRepository.findById("SAVE10")).thenReturn(Optional.of(discount));

            var request = DiscountApplicationRequest.builder()
                    .items(Map.of("SKU001", 5))
                    .itemPrices(Map.of("SKU001", new BigDecimal("100")))
                    .subtotal(new BigDecimal("100"))
                    .discountCode("SAVE10")
                    .build();

            var response = discountService.applyDiscount(request);

            assertThat(response.getDiscountAmount()).isEqualByComparingTo("10.00");
            assertThat(response.getTotalAfterDiscount()).isEqualByComparingTo("90.00");
            assertThat(response.getAppliedDiscountCode()).isEqualTo("SAVE10");
        }

        @Test
        void shouldApplyFixedAmountDiscount() {
            var discount = Discount.builder()
                    .code("FLAT5")
                    .type(Discount.DiscountType.FIXED_AMOUNT)
                    .value(new BigDecimal("5"))
                    .validFrom(LocalDate.now().minusDays(1))
                    .validTo(LocalDate.now().plusDays(30))
                    .active(true)
                    .build();
            when(discountRepository.findById("FLAT5")).thenReturn(Optional.of(discount));

            var request = DiscountApplicationRequest.builder()
                    .items(Map.of("SKU001", 1))
                    .itemPrices(Map.of("SKU001", new BigDecimal("30")))
                    .subtotal(new BigDecimal("30"))
                    .discountCode("FLAT5")
                    .build();

            var response = discountService.applyDiscount(request);

            assertThat(response.getDiscountAmount()).isEqualByComparingTo("5.00");
            assertThat(response.getTotalAfterDiscount()).isEqualByComparingTo("25.00");
        }

        @Test
        void shouldThrowWhenDiscountCodeNotFound() {
            when(discountRepository.findById("INVALID")).thenReturn(Optional.empty());

            var request = DiscountApplicationRequest.builder()
                    .items(Map.of("SKU001", 1))
                    .itemPrices(Map.of("SKU001", new BigDecimal("100")))
                    .subtotal(new BigDecimal("100"))
                    .discountCode("INVALID")
                    .build();

            assertThatThrownBy(() -> discountService.applyDiscount(request))
                    .isInstanceOf(InvalidDiscountException.class)
                    .hasMessageContaining("INVALID");
        }
    }

    @Nested
    @DisplayName("deleteDiscount")
    class DeleteDiscountTests {

        @Test
        void shouldDeleteDiscount() {
            var discount = Discount.builder()
                    .code("SAVE10")
                    .type(Discount.DiscountType.PERCENTAGE)
                    .value(BigDecimal.ONE)
                    .build();
            when(discountRepository.findById("SAVE10")).thenReturn(Optional.of(discount));

            discountService.deleteDiscount("SAVE10");

            verify(discountRepository).delete(any());
        }
    }
}
