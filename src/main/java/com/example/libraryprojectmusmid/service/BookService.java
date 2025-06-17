package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.book.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private static BookService instance;
    private final List<Book> bookCatalog;
    private final List<BookItem> bookInventory;

    private BookService() {
        this.bookCatalog = new ArrayList<>();
        this.bookInventory = new ArrayList<>();
        loadBooksFromCsv("data/books.csv");
    }

    public static BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }

    private void loadBooksFromCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 6) continue;

                String isbn = values[0].trim();
                String title = values[1].trim();
                String author = values[2].trim();
                String publisher = values[3].trim();
                String category = values[4].trim();
                int quantity = Integer.parseInt(values[5].trim());

                Book book = findBookByIsbn(isbn);
                if (book == null) {
                    if ("Fiction".equalsIgnoreCase(category)) {
                        book = new FictionBook(title, author, publisher, isbn, "General Fiction", "");
                    } else {
                        // Menggunakan nama kelas yang benar (f kecil)
                        book = new NonFictionBook(title, author, publisher, isbn, "General Nonfiction", "");
                    }
                    bookCatalog.add(book);
                }

                addBookItems(book, quantity);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading books from CSV: " + e.getMessage());
        }
    }

    // Metode bantuan lainnya...
    public Book findBookByIsbn(String isbn) {
        for (Book b : bookCatalog) {
            if (b.getISBN().equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    private void addBookItems(Book book, int quantity) {
        for (int i = 0; i < quantity; i++) {
            long currentCopies = bookInventory.stream().filter(item -> item.getBook().getISBN().equals(book.getISBN())).count();
            String barcode = book.getISBN() + "-" + (currentCopies + 1);
            bookInventory.add(new BookItem(barcode, book));
        }
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(bookCatalog);
        }
        String lowerCaseKeyword = keyword.toLowerCase();
        return bookCatalog.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseKeyword) ||
                        book.getISBN().equalsIgnoreCase(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    public BookItem findAvailableBookItem(String isbn) {
        return bookInventory.stream()
                .filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE)
                .findFirst()
                .orElse(null);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(bookCatalog);
    }
}