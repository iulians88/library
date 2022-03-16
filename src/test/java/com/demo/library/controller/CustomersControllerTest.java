package com.demo.library.controller;

import com.demo.library.model.Customer;
import com.demo.library.service.LibraryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomersControllerTest {

    @InjectMocks
    CustomersController customerController;

    @Mock
    LibraryService service;

    public String EMAIL1 = "isa@yahoo.com";
    public String EMAIL2 = "yanshaw@gmail@com";
    public String PASSWORD1 = "test1234";
    public String PASSWORD2 = "testless";
    public String NAME1 = "Iulian S.";
    public String NAME2 = "IAN Shaw";
    public String TOKEN = "JUS89898JHHGGG";

    @Test
    public void testSaveCustomer()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(service.saveCustomer(any(Customer.class))).thenReturn(EMAIL1);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail(EMAIL1);
        customer.setPassword(PASSWORD1);
        customer.setFullName(NAME1);

        ResponseEntity<String> responseEntity = customerController.saveCustomer(customer);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isEqualTo("Customer saved, for email: " + EMAIL1);
    }

    @Test
    public void testFindByEmail()
    {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setEmail(EMAIL1);
        customer1.setPassword(PASSWORD1);
        customer1.setFullName(NAME1);

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setEmail(EMAIL2);
        customer2.setPassword(PASSWORD2);
        customer2.setFullName(NAME2);

        when(service.getCustomer(EMAIL2)).thenReturn(customer2);
        ResponseEntity<Customer> result1 = customerController.getCustomer(EMAIL2);

        Assertions.assertAll(
            () -> Assertions.assertEquals(EMAIL2, result1.getBody().getEmail(), "Email does not match"),
            () -> Assertions.assertEquals(PASSWORD2, result1.getBody().getPassword(), "Password does not match"),
            () -> Assertions.assertEquals(NAME2, result1.getBody().getFullName(), "Name does not match")
        );
        assertThat(result1.getStatusCodeValue()).isEqualTo(200);

        when(service.getCustomer(EMAIL1)).thenReturn(customer1);
        ResponseEntity<Customer> result2 = customerController.getCustomer(EMAIL1);
        assertThat(result2.getBody().getFullName()).isNotEqualTo(NAME2);
    }

    @Test
    public void testLoginCustomer()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(service.login(any(Customer.class))).thenReturn(TOKEN);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail(EMAIL1);
        customer.setPassword(PASSWORD1);
        customer.setFullName(NAME1);

        ResponseEntity<String> responseEntity = customerController.login(customer);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isEqualTo(TOKEN);

        when(service.login(any(Customer.class))).thenThrow(new AuthenticationServiceException("Incorrect email or password!"));
        ResponseEntity<String> result = customerController.login(customer);
        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody()).isNotEqualTo("Incorrect email or password!");
    }
}