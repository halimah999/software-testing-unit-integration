package com.amigoscode.testing.customer;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerRegistrationService underTest;
    @Captor
    private ArgumentCaptor <Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest= new CustomerRegistrationService(customerRepository);

    }


    @Test
    void itShouldSaveNewCustomer() {
        /* Given*/
        //1. a phone number and customer
        String phoneNumber = "09873458";
        Customer customer = new Customer(UUID.randomUUID(),"sara",phoneNumber);
        //2. a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        //3. no customer with phone number is passed
        // (we tell our mock if u invoked please return something(empty optional))
        given(customerRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);
        //we want to check mock receive correct argument in save; so we need to Argument capture

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer argumentCaptorCustomerValue = customerArgumentCaptor.getValue();
        //compare between value captured of request to created customer, are they equal?
        //we can us isEqualTo method

        AssertionsForClassTypes.assertThat(argumentCaptorCustomerValue).isEqualToComparingFieldByField(customer);

    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        /* Given*/
        //1. a phone number and customer
        String phoneNumber = "09873458";
        Customer customer = new Customer(null,"sara",phoneNumber);
        //2. a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        //3. no customer with phone number is passed
        // (we tell our mock if u invoked please return something(empty optional))
        given(customerRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);
        //we want to check mock receive correct argument in save; so we need to Argument capture

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer argumentCaptorCustomerValue = customerArgumentCaptor.getValue();
        //compare between value captured of request to created customer, are they equal?
        //we can us isEqualTo method
        AssertionsForClassTypes.assertThat(argumentCaptorCustomerValue).isEqualToComparingFieldByField(customer);
        //we check if id become not null
        AssertionsForClassTypes.assertThat(argumentCaptorCustomerValue.getId()).isNotNull();



    }
    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given
        //1. a phone number and customer
        String phoneNumber = "09873458";
        Customer customer = new Customer(UUID.randomUUID(),"sara",phoneNumber);
        //2. a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        //3. no customer with phone number is passed
        // (we tell our mock if u invoked please return something(customer optional))
        given(customerRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrownPhoneNumberIsTaken() {
        // Given
        //1. a phone number and customer
        String phoneNumber = "09873458";
        Customer customer1 = new Customer(UUID.randomUUID(),"sara",phoneNumber);
        Customer customer2 = new Customer(UUID.randomUUID(),"noura",phoneNumber);
        //2. a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer1);

        //3. a customer is returned
        // (we tell our mock if u invoked please return something(customer optional))
        given(customerRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer2));

        // When
        // Then
        AssertionsForClassTypes.assertThatThrownBy(()-> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        //Finally
        then(customerRepository).should(never()).save(any(Customer.class));



    }
}