package com.demo.library.controller;

import com.demo.library.model.Customer;
import com.demo.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomersController {

    @Autowired
    LibraryService service;

    @ResponseBody
    @RequestMapping(value = "/smoke", method = RequestMethod.GET)
    public ResponseEntity<String> smokeTest() {
        return new ResponseEntity<String>(service.smokeTest(), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/saveCustomer", method = RequestMethod.POST)
    //Adding @Valid here blocks directly the request if the email pattern is invalid!
    public ResponseEntity<String> saveCustomer(/*@Valid*/ @RequestBody Customer customer) {
        try {
            return new ResponseEntity<String>("Customer saved, for email: " + service.saveCustomer(customer), HttpStatus.OK);
        } catch (DataIntegrityViolationException e){
            return new ResponseEntity<String>("Email already used by another account", HttpStatus.BAD_REQUEST);
        } catch (ConstraintViolationException e){
            return new ResponseEntity<String>("Email does not match regex pattern!", HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getCustomer/{email}", method = RequestMethod.GET)
    public ResponseEntity<Customer> getCustomer(@PathVariable String email) {
        return new ResponseEntity<Customer>(service.getCustomer(email), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteCustomer/{email}", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCustomer(@PathVariable String email) {
        try {
            return new ResponseEntity<String>(service.deleteCustomer(email), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<String>("Error occured when deleting customer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody Customer customer) {
        try {
            return new ResponseEntity<String>(service.login(customer), HttpStatus.OK);
        } catch(AuthenticationServiceException e) {
            return new ResponseEntity<String>("Incorrect email or password", HttpStatus.BAD_REQUEST);
        }
    }
}
