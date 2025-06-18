package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.book.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookService {

    private static BookService instance;
    private final List<Book> bookCatalog;
    private final List<BookItem> bookInventory;

    private BookService() {
        this.bookCatalog = new ArrayList<>();
        this.bookInventory = new ArrayList<>();

        // =================================================================================
        // BAGIAN YANG DIPERBAIKI: Menggunakan kelas turunan, bukan kelas abstract Book
        // Kita isi parameter tambahan (genre, subject, dll) dengan data dummy.
        // =================================================================================

        // Laskar Pelangi, Bumi Manusia, Cantik Itu Luka adalah Fiksi
        Book book1 = new FictionBook("Laskar Pelangi", "Andrea Hirata", "Bentang Pustaka", "978-979-1227-2-7", "Novel", "Tetralogi Laskar Pelangi"); // <-- DIUBAH
        Book book2 = new FictionBook("Bumi Manusia", "Pramoedya Ananta Toer", "Hasta Mitra", "978-979-97312-3-4", "Historical Fiction", "Buru Quartet"); // <-- DIUBAH
        Book book3 = new FictionBook("Cantik Itu Luka", "Eka Kurniawan", "Gramedia", "978-602-03-1258-0", "Magical Realism", ""); // <-- DIUBAH
        // Filosofi Teras adalah Non-Fiksi
        Book book4 = new NonFictionBook("Filosofi Teras", "Henry Manampiring", "Kompas", "978-602-412-518-9", "Self-Help / Philosophy", "158.1"); // <-- DIUBAH

        // Menambahkan buku dan itemnya ke dalam list
        bookCatalog.add(book1); addBookItems(book1, 5); // Contoh: tambah 5 eksemplar
        bookCatalog.add(book2); addBookItems(book2, 3); // Contoh: tambah 3 eksemplar
        bookCatalog.add(book3); addBookItems(book3, 4);
        bookCatalog.add(book4); addBookItems(book4, 6);

        // Memuat data tambahan dari CSV
        loadBooksFromCsv("data/books.csv");
    }

    public static synchronized BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }

    public long getTotalStockByIsbn(String isbn) {
        return bookInventory.stream()
                .filter(item -> item.getBook().getISBN().equals(isbn))
                .count();
    }

    public long getAvailableStockByIsbn(String isbn) {
        return bookInventory.stream()
                .filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE)
                .count();
    }

    // CREATE
    public boolean addBook(String isbn, String title, String author, String publisher, String category, int quantity) {
        if (findBookByIsbn(isbn) != null) {
            System.err.println("Error: Buku dengan ISBN " + isbn + " sudah ada.");
            return false;
        }

        Book newBook;
        if ("Fiction".equalsIgnoreCase(category)) {
            newBook = new FictionBook(title, author, publisher, isbn, "General Fiction", "");
        } else if ("NonFiction".equalsIgnoreCase(category)) {
            newBook = new NonFictionBook(title, author, publisher, isbn, "General Nonfiction", "");
        } else {
            System.err.println("Error: Kategori '" + category + "' tidak valid.");
            return false;
        }

        bookCatalog.add(newBook);
        addBookItems(newBook, quantity);
        System.out.println("Buku '" + title + "' berhasil ditambahkan.");
        return true;
    }

    // ... (Metode searchBooks, updateBook, deleteBook, getAllBooks, findBookByIsbn, findAvailableBookItem, loadBooksFromCsv biarkan sama) ...

    /**
     * Helper untuk menambahkan sejumlah item/eksemplar buku ke inventaris.
     * @param book Objek buku yang itemnya akan ditambahkan.
     * @param quantity Jumlah item yang ditambahkan.
     */
    private void addBookItems(Book book, int quantity) {
        for (int i = 0; i < quantity; i++) {
            long currentCopies = bookInventory.stream().filter(item -> item.getBook().getISBN().equals(book.getISBN())).count();
            String barcode = book.getISBN() + "-" + (currentCopies + 1);

            // =================================================================================
            // PERBAIKAN PENTING KEDUA: Baris ini sebelumnya di-comment dan salah.
            // Konstruktor BookItem hanya butuh barcode dan book.
            // =================================================================================
            bookInventory.add(new BookItem(barcode, book)); // <-- DIUBAH & DIAKTIFKAN
        }
    }

    // Sisa metode lainnya sama, tidak perlu diubah...
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
    public boolean updateBook(String isbn, String newTitle, String newAuthor, String newPublisher) {
        Book bookToUpdate = findBookByIsbn(isbn);
        if (bookToUpdate == null) {
            return false;
        }
        bookToUpdate.setTitle(newTitle);
        bookToUpdate.setAuthor(newAuthor);
        bookToUpdate.setPublisher(newPublisher);
        return true;
    }
    public boolean deleteBook(String isbn) {
        Book bookToDelete = findBookByIsbn(isbn);
        if (bookToDelete == null) {
            return false;
        }
        bookInventory.removeIf(bookItem -> bookItem.getBook().getISBN().equals(isbn));
        bookCatalog.remove(bookToDelete);
        return true;
    }
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookCatalog);
    }
    public Book findBookByIsbn(String isbn) {
        return bookCatalog.stream()
                .filter(b -> b.getISBN().equals(isbn))
                .findFirst()
                .orElse(null);
    }
    public BookItem findAvailableBookItem(String isbn) {
        return bookInventory.stream()
                .filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE)
                .findFirst()
                .orElse(null);
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
                if(findBookByIsbn(isbn) == null) {
                    addBook(isbn, title, author, publisher, category, quantity);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error saat memuat buku dari CSV: " + e.getMessage());
        }


    }
}