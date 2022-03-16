package com.demo.library.controller;

import com.demo.library.model.Book;
import com.demo.library.service.LibraryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BooksControllerTest {

    @InjectMocks
    BooksController booksController;

    @Mock
    LibraryService service;

    public String TITLE1 = "Dune";
    public String TITLE2 = "The Process";
    public String AUTHOR1 = "Frank Herbert";
    public String AUTHOR2 = "Franz Kafka";
    public String ISBN1 = "CIP-98117-ISJ";
    public String ISBN2 = "ISBN-00099887";

    @Test
    public void testSaveBook()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(service.saveBook(any(Book.class))).thenReturn(TITLE1);

        Book book = new Book();
        book.setId(1L);
        book.setTitle(TITLE1);
        book.setAuthor(AUTHOR1);
        book.setIsbn(ISBN1);

        ResponseEntity<String> responseEntity = booksController.saveBook(book);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isEqualTo("Book saved: " + TITLE1);
    }

    @Test
    public void testFindByTitle()
    {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle(TITLE1);
        book1.setAuthor(AUTHOR1);
        book1.setIsbn(ISBN1);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle(TITLE2);
        book2.setAuthor(AUTHOR2);
        book2.setIsbn(ISBN2);

        when(service.getBook(TITLE2)).thenReturn(book2);
        ResponseEntity<Book> result1 = booksController.getBook(TITLE2);

        Assertions.assertAll(
            () -> Assertions.assertEquals(TITLE2, result1.getBody().getTitle(), "Title does not match"),
            () -> Assertions.assertEquals(AUTHOR2, result1.getBody().getAuthor(), "Author does not match"),
            () -> Assertions.assertEquals(ISBN2, result1.getBody().getIsbn(), "ISBN does not match")
        );
        assertThat(result1.getStatusCodeValue()).isEqualTo(200);

        when(service.getBook(TITLE1)).thenReturn(book1);
        ResponseEntity<Book> result2 = booksController.getBook(TITLE1);
        assertThat(result2.getBody().getAuthor()).isNotEqualTo(AUTHOR2);
    }
}