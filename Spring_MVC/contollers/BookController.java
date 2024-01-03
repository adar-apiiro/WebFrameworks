// src/main/java/com/example/controller/BookController.java

package com.example.controller;

import com.example.model.Book;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private List<Book> books = new ArrayList<>();

    @GetMapping
    public String getAllBooks(Model model) {
        model.addAttribute("books", books);
        return "bookList";
    }

    @GetMapping("/{id}")
    public String getBookById(@PathVariable Long id, Model model) {
        Book book = findBookById(id);
        model.addAttribute("book", book);
        return "bookDetails";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("newBook", new Book());
        return "addBookForm";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("newBook") Book newBook) {
        books.add(newBook);
        return "redirect:/books";
    }

    // Utility method to find a book by ID
    private Book findBookById(Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
