package com.demo.library.controller;

import com.demo.library.model.Book;
import com.demo.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    @Autowired LibraryService service;

    @ResponseBody
    @RequestMapping(value = "/smoke", method = RequestMethod.GET)
    public ResponseEntity<String> smokeTest() {
        return new ResponseEntity<String>(service.smokeTest(), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/saveBook", method = RequestMethod.POST)
    public ResponseEntity<String> saveBook(@RequestBody Book book) {
        try {
            return new ResponseEntity<String>("Book saved: " + service.saveBook(book), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<String>("Error occured when saving book", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBook/{title}", method = RequestMethod.GET)
    public ResponseEntity<Book> getBook(@PathVariable String title) {
        return new ResponseEntity<Book>(service.getBook(title), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteCustomer/{title}", method = RequestMethod.POST)
    public ResponseEntity<String> deleteBook(@PathVariable String title) {
        try {
            return new ResponseEntity<String>(service.deleteBook(title), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<String>("Error occured when deleting book", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
