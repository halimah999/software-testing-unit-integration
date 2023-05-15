package com.amigoscode.testing.customer;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {
    //under test
    @Autowired
    private CustomerRepository underTest;
    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "+966 505519446";
        Customer customer = new Customer(id,"sara",phoneNumber);
        underTest.save(customer);
        // When
        Optional<Customer> customerOptional = underTest.findByPhoneNumber(phoneNumber);

        // Then
        AssertionsForClassTypes.assertThat(customerOptional).isPresent()
                .hasValueSatisfying(c->{
                    AssertionsForClassTypes.assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberWhenNumberDoesNotExists() {
        // Given
        String phoneNumber = "+966 505519446";

        // When
        Optional<Customer> customerOptional = underTest.findByPhoneNumber(phoneNumber);

        // Then
        AssertionsForClassTypes.assertThat(customerOptional).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        UUID id = UUID.randomUUID();
        // Given -> given input
        Customer sara = new Customer(id,"sara","+966 505519446");
        // When -> when we call under test
        underTest.save(sara);
        // Then -> perform some assertion
        Optional<Customer>  customer = underTest.findById(id);
        AssertionsForClassTypes.assertThat(customer)
                .isPresent()
                .hasValueSatisfying(c->{
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo("sara");
//                    assertThat(c.getPhoneNumber()).isEqualTo("+966 505519446");
                    AssertionsForClassTypes.assertThat(c).isEqualToComparingFieldByField(sara);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given -> given input
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id,null,"+966 505519446");
        // When -> when we call under test
        //Then
        AssertionsForClassTypes.assertThatThrownBy(()->
                        underTest.save(customer))
                .hasMessageContaining("com.amigoscode.testing.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given -> given input
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id,"sara",null);
        // When -> when we call under test
        //Then
        AssertionsForClassTypes.assertThatThrownBy(()->
                        underTest.save(customer))
                .hasMessageContaining("com.amigoscode.testing.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

}