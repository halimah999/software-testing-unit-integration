package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

//        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);

class PaymentServiceTest {

    @Mock
    private   CardPaymentCharger cardPaymentCharger;
    @Mock
    private  CustomerRepository customerRepository;
    @Mock
    private  PaymentRepository paymentRepository;
    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(cardPaymentCharger,customerRepository,paymentRepository);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //Given

        // 1. customer id
        UUID customerId = UUID.randomUUID();

        // 2. mock customer with this id and return mock customer
        given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));

        // 3. payment request
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                null,
                null,
                new BigDecimal("100.0"),
                Currency.USD,
                "card12xxx",
                "Donation"));

        // return CardPaymentCharge with true debited
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())
        ).willReturn(new CardPaymentCharge(true));

        //When
        underTest.chargeCard(customerId,paymentRequest);

        //Then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentValue = paymentArgumentCaptor.getValue();
        AssertionsForClassTypes.assertThat(paymentArgumentValue).isEqualToIgnoringGivenFields(paymentRequest.getPayment(),"customerId");
        AssertionsForClassTypes.assertThat(paymentArgumentValue.getCustomerId()).isEqualTo(customerId);


    }

    @Test
    void itShouldThrownCardIsNotFound() {
        //Given

        // 1. customer id
        UUID customerId = UUID.randomUUID();

        // 2. mock customer with this id and return mock customer
        given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));

        // 3. payment request
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                null,
                null,
                new BigDecimal("100.0"),
                Currency.USD,
                "card12xxx",
                "Donation"));

        // return CardPaymentCharge with true debited
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())
        ).willReturn(new CardPaymentCharge(false));

        //When
        //Then
        AssertionsForClassTypes.assertThatThrownBy(()->
                        underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer %s ",customerId));
        //finally
        then(paymentRepository).should(never()).save(any(Payment.class));


    }

    @Test
    void itShouldNotChargeCardAndThrownCurrencyNotSupported() {
        //Given

        // 1. customer id
        UUID customerId = UUID.randomUUID();

        // 2. mock customer with this id and return mock customer
        given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));
        //Euros
        Currency currency = Currency.EUR;
        // 3. payment request
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                null,
                null,
                new BigDecimal("100.0"),
                currency,
                "card12xxx",
                "Donation"));



        //When
        AssertionsForClassTypes.assertThatThrownBy(()->
                        underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency [%s] not supported",currency));
        //then
        //no interaction with card payment
        then(cardPaymentCharger).shouldHaveNoInteractions();
        //finally
        then(paymentRepository).should(never()).save(any(Payment.class));

    }
    //Customer with id [%s] not found
    @Test
    void itShouldNotChargeCardAndThrownCustomerWithIdNotFound() {
        //Given

        // 1. customer id
        UUID customerId = UUID.randomUUID();

        // 2. return empty
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // 3. payment request
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                null,
                null,
                new BigDecimal("100.0"),
                Currency.USD,
                "card12xxx",
                "Donation"));



        //When
        AssertionsForClassTypes.assertThatThrownBy(()->
                        underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Customer with id [%s] not found",customerId));
        //then
        //no interaction with card payment
        then(cardPaymentCharger).shouldHaveNoInteractions();
        //finally
        then(paymentRepository).should(never()).save(any(Payment.class));

    }

}