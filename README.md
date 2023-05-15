# software-testing-unit-integration

## Introduction
In this project we have two basic use cases one for user and another for payment , the user send a request to make registration in this system after that he can make payment process.then ,  the payment process save into data base with id of users.

**The purpose** in this project is to learn how to properly test this system

## Testing system:
we start with **unit testing** in this test we Isolation each unit from any external service or from each others
. The units we test *user unit* , 
*payment unit* , *stripe unit* which is a part of payment. 
after that we made the integration testing to check if the registration customer and payment  work correctly and the status APIs in this process requests 200

`note: ` stripe : is a special API for payment process. It is a payment infrastructure that allow us to accept payment and send payout in our websites

### 1. Unit Testing
#### 1.1 User registration testing
- ```CustomerRepositoryTest```
  Here we applied a bunch of testing to check if it save or retrieve customer correctly with some condition e.g. the system should not save customer if name is null. The method we applied in this repository as following :
  - itShouldSelectCustomerByPhoneNumber
  - itShouldNotSelectCustomerByPhoneNumberWhenNumberDoesNotExists
  - itShouldSaveCustomer
  - itShouldNotSaveCustomerWhenNameIsNull
  - itShouldNotSaveCustomerWhenPhoneNumberIsNull





## References 
1. [amigoscode](https://amigoscode.com/p/software-testing)
2. [spring mvc test framework](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)