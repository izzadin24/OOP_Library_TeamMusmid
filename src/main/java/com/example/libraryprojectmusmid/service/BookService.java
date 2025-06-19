package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.book.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private static BookService instance;
    private final List<Book> bookCatalog;
    private final List<BookItem> bookInventory;
    private final String bookCsvPath = "data/books.csv";

    private BookService() {
        this.bookCatalog = new ArrayList<>();
        this.bookInventory = new ArrayList<>();
        loadBooksFromCsv();
    }

    public static synchronized BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }

    // =================================================================
    // METODE CRUD: CREATE (TAMBAHKAN METODE INI)
    // =================================================================
    /**
     * CREATE: Menambahkan buku baru ke katalog dan inventaris, lalu menyimpan ke CSV.
     * Metode ini yang akan dipanggil oleh AddBookController.
     * @param newBook Objek buku baru yang akan ditambahkan.
     * @param quantity Jumlah stok awal.
     * @return true jika berhasil, false jika ISBN sudah ada.
     */
    public boolean addBook(Book newBook, int quantity) {
        if (findBookByIsbn(newBook.getISBN()) != null) {
            System.err.println("Gagal: Buku dengan ISBN " + newBook.getISBN() + " sudah ada.");
            return false;
        }
        bookCatalog.add(newBook);
        addBookItems(newBook, quantity);
        saveBooksToCsv(); // Simpan semua perubahan ke file CSV
        return true;
    }


    // ... (Metode loadBooksFromCsv dan saveBooksToCsv tidak perlu diubah) ...
    private void loadBooksFromCsv() {
        bookCatalog.clear();
        bookInventory.clear();
        File file = new File(bookCsvPath);
        if (!file.exists()) {
            System.out.println("Info: File " + bookCsvPath + " tidak ditemukan. File baru akan dibuat.");
            try {
                Files.createDirectories(Paths.get("data"));
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookCsvPath))) {
                    writer.write("isbn,title,author,publisher,cover_path,category,genre,subject,quantity\n");
                }
                return;
            } catch (IOException e) {
                System.err.println("KRITIS: Gagal membuat file CSV baru: " + e.getMessage());
                return;
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(bookCsvPath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",", -1);
                if (values.length < 9) continue;
                Book book = createBookFromCsvValues(values);
                if (book != null && findBookByIsbn(book.getISBN()) == null) {
                    bookCatalog.add(book);
                    int quantity = Integer.parseInt(values[8].trim());
                    addBookItems(book, quantity);
                }
            }
            System.out.println(bookCatalog.size() + " jenis buku dan " + bookInventory.size() + " total item fisik berhasil dimuat.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("KRITIS: Error saat memuat buku dari CSV: " + e.getMessage());
        }
    }
    private void saveBooksToCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(bookCsvPath))) {
            bw.write("isbn,title,author,publisher,cover_path,category,genre,subject,quantity\n");
            for (Book book : bookCatalog) {
                String genre = (book instanceof FictionBook) ? ((FictionBook) book).getGenre() : "";
                String subject = (book instanceof NonFictionBook) ? ((NonFictionBook) book).getSubject() : "";
                long quantity = getTotalStockByIsbn(book.getISBN());
                String line = String.join(",",
                        escapeCsv(book.getISBN()),
                        escapeCsv(book.getTitle()),
                        escapeCsv(book.getAuthor()),
                        escapeCsv(book.getPublisher()),
                        escapeCsv(book.getCoverImagePath()),
                        escapeCsv((book instanceof FictionBook) ? "Fiction" : "NonFiction"),
                        escapeCsv(genre),
                        escapeCsv(subject),
                        String.valueOf(quantity)
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("KRITIS: Error saat menyimpan buku ke CSV: " + e.getMessage());
        }
    }


    // =================================================================
    // METODE PEMBANTU (HELPER) YANG DIPERBAIKI
    // =================================================================
    private Book createBookFromCsvValues(String[] values) {
        String isbn = values[0].trim();
        String title = values[1].trim();
        String author = values[2].trim();
        String publisher = values[3].trim();
        String coverPath = values[4].trim();
        String category = values[5].trim();
        String genre = values[6].trim();
        String subject = values[7].trim();

        if ("Fiction".equalsIgnoreCase(category)) {
            // PERBAIKAN: Memanggil konstruktor dengan urutan yang benar
            // urutan: title, author, publisher, isbn, coverPath, genre, seriesName
            return new FictionBook(title, author, publisher, isbn, coverPath, genre);
        } else {
            // PERBAIKAN: Memanggil konstruktor dengan urutan yang benar
            // urutan: title, author, publisher, isbn, coverPath, subject, deweyDecimal
            return new NonFictionBook(title, author, publisher, isbn, coverPath, subject, "");
        }
    }

    // ... (Sisa kode Anda yang lain sudah benar dan tidak perlu diubah) ...
    public List<Book> getAllBooks() { return new ArrayList<>(bookCatalog); }
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) { return getAllBooks(); }
        String lowerCaseKeyword = keyword.toLowerCase();
        return bookCatalog.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }
    public long getTotalStockByIsbn(String isbn) { return bookInventory.stream().filter(item -> item.getBook().getISBN().equals(isbn)).count(); }
    public long getAvailableStockByIsbn(String isbn) { return bookInventory.stream().filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE).count(); }
    public BookItem findAvailableBookItem(String isbn) { return bookInventory.stream().filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE).findFirst().orElse(null); }
    public Book findBookByIsbn(String isbn) { return bookCatalog.stream().filter(b -> b.getISBN().equals(isbn)).findFirst().orElse(null); }
    private void addBookItems(Book book, int quantity) {
        for (int i = 0; i < quantity; i++) {
            String barcode = book.getISBN() + "-" + (i + 1);
            bookInventory.add(new BookItem(barcode, book));
        }
    }
    private String escapeCsv(String data) {
        if (data == null || data.isEmpty()) return "";
        if (data.contains(",")) { return "\"" + data.replace("\"", "\"\"") + "\""; }
        return data;
    }
    public boolean updateBook(String isbn, String newTitle, String newAuthor, String newPublisher) { // <-- Tambahkan newPublisher di sini
        Book bookToUpdate = findBookByIsbn(isbn);
        if (bookToUpdate == null) {
            System.err.println("Error: Buku dengan ISBN " + isbn + " tidak ditemukan untuk di-update.");
            return false;
        }

        // Update semua informasi yang relevan di memori
        bookToUpdate.setTitle(newTitle);
        bookToUpdate.setAuthor(newAuthor);
        bookToUpdate.setPublisher(newPublisher); // <-- Tambahkan logika untuk update penerbit

        // Simpan semua perubahan kembali ke file CSV
        saveBooksToCsv();
        System.out.println("Buku dengan ISBN " + isbn + " berhasil diperbarui.");
        return true;
    }
    public boolean deleteBook(String isbn) {
        bookInventory.removeIf(item -> item.getBook().getISBN().equals(isbn));
        boolean catalogRemoved = bookCatalog.removeIf(book -> book.getISBN().equals(isbn));
        if (catalogRemoved) { saveBooksToCsv(); }
        return catalogRemoved;
    }
}