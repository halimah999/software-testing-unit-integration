package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD,Currency.GBP);
    private  final CardPaymentCharger cardPaymentCharger;

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(CardPaymentCharger cardPaymentCharger, CustomerRepository customerRepository, PaymentRepository paymentRepository) {
        this.cardPaymentCharger = cardPaymentCharger;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
    }


    //check weather customer exist
    //think how we are going to autowired the logic perform payment into our service
    void chargeCard(UUID customerId, PaymentRequest paymentRequest){
        Currency currency = paymentRequest.getPayment().getCurrency();

        //1. Does customer exist if not throw
        boolean isCustomerFound =  customerRepository.findById(customerId).isPresent();
        if(!isCustomerFound){
            throw new IllegalStateException(String.format("Customer with id [%s] not found",customerId));}

        //2. Do we support the currency if not throw
        boolean isCurrencySupported = ACCEPTED_CURRENCIES
                .stream()
                .anyMatch(c->c.equals(currency));
        if(!isCurrencySupported){
            String message =String.format("Currency [%s] not supported",currency) ;
            throw new IllegalStateException(message);
        }


        //3. Charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        //4. If not debited throw
        if(!cardPaymentCharge.isCardDebited()){
            throw new IllegalStateException(
                    String.format("Card not debited for customer %s ",customerId)
            );
        }

        //5. Insert payment
        paymentRequest.getPayment().setCustomerId(customerId);
        paymentRepository.save(paymentRequest.getPayment());
        //6. TODO: send sms
    }



}
