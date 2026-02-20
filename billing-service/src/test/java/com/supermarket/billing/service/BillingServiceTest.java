package com.supermarket.billing.service;

import com.supermarket.billing.client.DiscountsClient;
import com.supermarket.billing.client.InventoryClient;
import com.supermarket.billing.client.PricingClient;
import com.supermarket.billing.dto.BillRequest;
import com.supermarket.billing.dto.BillResponse;
import com.supermarket.billing.dto.DiscountApplicationRequest;
import com.supermarket.billing.dto.DiscountApplicationResponse;
import com.supermarket.billing.dto.PriceCalculationRequest;
import com.supermarket.billing.dto.PriceCalculationResponse;
import com.supermarket.billing.dto.StockCheckRequest;
import com.supermarket.billing.dto.StockCheckResponse;
import com.supermarket.billing.dto.StockReservationRequest;
import com.supermarket.billing.exception.BillingException;
import com.supermarket.billing.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService Unit Tests")
class BillingServiceTest {

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PricingClient pricingClient;

    @Mock
    private DiscountsClient discountsClient;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillingService billingService;

    private BillRequest validBillRequest;

    @BeforeEach
    void setUp() {
        validBillRequest = BillRequest.builder()
                .items(Map.of("SKU001", 2, "SKU002", 1))
                .discountCode("SAVE10")
                .build();
    }

    @Nested
    @DisplayName("createBill")
    class CreateBillTests {

        @Test
        void shouldCreateBillSuccessfully() {
            when(inventoryClient.checkStock(any(StockCheckRequest.class)))
                    .thenReturn(StockCheckResponse.builder().available(true).build());

            when(pricingClient.calculatePrices(any(PriceCalculationRequest.class)))
                    .thenReturn(PriceCalculationResponse.builder()
                            .itemPrices(Map.of("SKU001", new BigDecimal("5.98"), "SKU002", new BigDecimal("3.49")))
                            .subtotal(new BigDecimal("9.47"))
                            .currency("USD")
                            .build());

            when(discountsClient.applyDiscount(any(DiscountApplicationRequest.class)))
                    .thenReturn(DiscountApplicationResponse.builder()
                            .discountAmount(new BigDecimal("0.95"))
                            .totalAfterDiscount(new BigDecimal("8.52"))
                            .appliedDiscountCode("SAVE10")
                            .build());

            BillResponse response = billingService.createBill(validBillRequest);

            assertThat(response).isNotNull();
            assertThat(response.getBillId()).isNotNull();
            assertThat(response.getTotal()).isEqualByComparingTo("8.52");
            assertThat(response.getDiscountAmount()).isEqualByComparingTo("0.95");
            assertThat(response.getAppliedDiscountCode()).isEqualTo("SAVE10");
        }

        @Test
        void shouldThrowWhenInsufficientStock() {
            when(inventoryClient.checkStock(any(StockCheckRequest.class)))
                    .thenReturn(StockCheckResponse.builder()
                            .available(false)
                            .insufficientItems(Map.of("SKU001", "Requested: 100, Available: 10"))
                            .build());

            assertThatThrownBy(() -> billingService.createBill(validBillRequest))
                    .isInstanceOf(BillingException.class)
                    .hasMessageContaining("Insufficient stock");
        }

        @Test
        void shouldCreateBillWithoutDiscount() {
            BillRequest request = BillRequest.builder()
                    .items(Map.of("SKU001", 1))
                    .build();

            when(inventoryClient.checkStock(any(StockCheckRequest.class)))
                    .thenReturn(StockCheckResponse.builder().available(true).build());
            when(pricingClient.calculatePrices(any(PriceCalculationRequest.class)))
                    .thenReturn(PriceCalculationResponse.builder()
                            .itemPrices(Map.of("SKU001", new BigDecimal("2.99")))
                            .subtotal(new BigDecimal("2.99"))
                            .currency("USD")
                            .build());

            BillResponse response = billingService.createBill(request);

            assertThat(response.getTotal()).isEqualByComparingTo("2.99");
            assertThat(response.getDiscountAmount()).isEqualByComparingTo("0");
            assertThat(response.getAppliedDiscountCode()).isNull();
            verify(discountsClient, never()).applyDiscount(any());
        }
    }

    @Nested
    @DisplayName("getBill")
    class GetBillTests {

        @Test
        void shouldThrowWhenBillNotFound() {
            when(billRepository.findById("INVALID")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billingService.getBill("INVALID"))
                    .isInstanceOf(BillingException.class)
                    .hasMessageContaining("Bill not found");
        }
    }
}
