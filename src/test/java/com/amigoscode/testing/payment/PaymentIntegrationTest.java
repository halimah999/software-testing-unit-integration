package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {

        //given -- customer registration  request
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer(customerId,"sara","000000000");

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
        //when register customer
        ResultActions customerRegResultActions= mockMvc
                .perform(put("/api/v1/customer-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));
        //-------------------------------------------------------
        //given -- payment  request
        long paymentId = 1L;

        Payment payment = new Payment(
                paymentId,
                customerId,
                new BigDecimal("200.0"),
                Currency.USD,
                "45049405",
                "Zakat");

        PaymentRequest paymentRequest = new PaymentRequest(payment);
        //when payment is sent
        ResultActions paymentResultActions= mockMvc
                .perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        //payment should stored in DB
        //this way is not a good way cuz we use repository  (should create retrieve endpoint)

        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p->assertThat(p.getCustomerId()).isEqualTo(customerId));



        //check if our APIs semantic are working(both the registration and payment requests 200)
        customerRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
