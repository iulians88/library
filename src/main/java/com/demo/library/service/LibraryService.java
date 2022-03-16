package com.demo.library.service;

import com.demo.library.model.Book;
import com.demo.library.model.Customer;
import com.demo.library.repository.BooksRepository;
import com.demo.library.repository.CustomerRepository;
import com.demo.library.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationServiceException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    @Value("${app.secret}")
    private String secret;

    @Value("${app.jwtid}")
    private String jwtid;

    private final CustomerRepository customerRepository;
    private final BooksRepository booksRepository;

    @Autowired
    public LibraryService(CustomerRepository customerRepository, BooksRepository booksRepository) {
        this.customerRepository = customerRepository;
        this.booksRepository = booksRepository;
    }

    public String smokeTest() {
        return "Success! LibraryService Endpoint is accessible.";
    }

    /*
        CUSTOMERS
    */
    public String saveCustomer(Customer customer) {
        /*
            On update we don't want to change password encryption so we check if the password matches.
            If it doesn't it means that the password is new and we encrypt it.
        */
        Customer cust = customerRepository.findByEmail(customer.getEmail());
        if(cust == null)
            PasswordUtils.encryptUserData(customer);
        else if (!PasswordUtils.verifyUserPassword(customer.getPassword(), cust.getPassword(), cust.getSalt()))
            PasswordUtils.encryptUserData(customer);
        else{
            customer.setPassword(cust.getPassword());
            customer.setSalt(cust.getSalt());
        }
        customerRepository.save(customer);
        return customer.getEmail();
    }

    public Customer getCustomer(String email) {
        return customerRepository.findByEmail(email);
    }

    public String deleteCustomer(String email){
        Customer cust = customerRepository.findByEmail(email);
        if(cust!=null){
            customerRepository.delete(cust);
            return "Success! Customer deleted.";
        } else {
            return "Customer does not exist!";
        }
    }

    public String login(Customer customer) {
        Customer cust = customerRepository.findByEmail(customer.getEmail());

        if(!PasswordUtils.verifyUserPassword(customer.getPassword(), cust.getPassword(), cust.getSalt()))
            throw new AuthenticationServiceException("Incorrect email or password!");

        return getJWTToken(customer.getEmail());
    }

    private String getJWTToken(String email) {
        String secretKey = secret;
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
            .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
            .builder()
            .setId(jwtid)
            .setSubject(email)
            .claim("authorities",
                grantedAuthorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            //.setExpiration(new Date(System.currentTimeMillis() + 3600000)) -> I don't want the token to expire for testing purposes
            .signWith(SignatureAlgorithm.HS512,
                secretKey.getBytes()).compact();

        return "Bearer " + token;
    }

    /*
        BOOKS
    */
    public String saveBook(Book book) {
        return booksRepository.save(book).getTitle();
    }

    public Book getBook(String title) {
        return booksRepository.findByTitle(title);
    }

    public String deleteBook(String title){
        Book book = booksRepository.findByTitle(title);
        if(book!=null){
            booksRepository.delete(book);
            return "Success! Book deleted.";
        } else {
            return "Book does not exist!";
        }
    }
}
