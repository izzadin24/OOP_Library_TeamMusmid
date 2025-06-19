package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.book.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service final untuk mengelola data buku yang disimpan dalam file CSV.
 * Menerapkan Singleton Pattern dan operasi CRUD yang andal.
 * File CSV adalah satu-satunya sumber kebenaran (single source of truth).
 */
public class BookService {

    private static BookService instance;
    private final List<Book> bookCatalog; // Bertindak sebagai cache/data dalam memori untuk metadata buku
    private final List<BookItem> bookInventory; // Bertindak sebagai cache/data dalam memori untuk item fisik buku
    private final String bookCsvPath = "data/books.csv"; // Path ke file CSV

    /**
     * Constructor private untuk Singleton Pattern.
     * Hanya memanggil satu metode: memuat semua data dari file CSV saat aplikasi dimulai.
     */
    private BookService() {
        this.bookCatalog = new ArrayList<>();
        this.bookInventory = new ArrayList<>();
        loadBooksFromCsv();
    }

    /**
     * Mendapatkan satu-satunya instance dari BookService.
     */
    public static synchronized BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }

    // =================================================================
    // METODE INTI UNTUK MEMBACA DAN MENULIS KE CSV
    // =================================================================

    /**
     * Membaca seluruh data dari file CSV dan memuatnya ke dalam list di memori.
     * Metode ini juga akan membuat file & folder jika belum ada.
     */
    private void loadBooksFromCsv() {
        bookCatalog.clear();
        bookInventory.clear();
        File file = new File(bookCsvPath);

        // Jika file tidak ada, buat file beserta foldernya
        if (!file.exists()) {
            System.out.println("Info: File " + bookCsvPath + " tidak ditemukan. File baru akan dibuat.");
            try {
                Files.createDirectories(Paths.get("data")); // Membuat folder 'data'
                // Membuat file dengan header
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookCsvPath))) {
                    writer.write("isbn,title,author,publisher,cover_path,category,genre,subject,quantity\n");
                }
                return; // Berhenti karena file baru saja dibuat dan masih kosong
            } catch (IOException e) {
                System.err.println("KRITIS: Gagal membuat file CSV baru: " + e.getMessage());
                return;
            }
        }

        // Jika file ada, lanjutkan membaca
        try (BufferedReader br = new BufferedReader(new FileReader(bookCsvPath))) {
            String line;
            br.readLine(); // Lewati baris header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Lewati baris kosong

                String[] values = line.split(",", -1);
                if (values.length < 9) continue;

                // Buat objek Book dari data CSV
                Book book = createBookFromCsvValues(values);
                if (book != null && findBookByIsbn(book.getISBN()) == null) {
                    bookCatalog.add(book);
                    // Buat item fisik (stok) berdasarkan kuantitas
                    int quantity = Integer.parseInt(values[8].trim());
                    addBookItems(book, quantity);
                }
            }
            System.out.println(bookCatalog.size() + " jenis buku dan " + bookInventory.size() + " total item fisik berhasil dimuat.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("KRITIS: Error saat memuat buku dari CSV: " + e.getMessage());
        }
    }

    /**
     * Menyimpan seluruh data dari memori (bookCatalog & bookInventory) ke file CSV.
     * Metode ini menimpa file lama dengan data terbaru.
     */
    private void saveBooksToCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(bookCsvPath))) {
            bw.write("isbn,title,author,publisher,cover_path,category,genre,subject,quantity\n");
            for (Book book : bookCatalog) {
                String genre = (book instanceof FictionBook) ? ((FictionBook) book).getGenre() : "";
                String subject = (book instanceof NonFictionBook) ? ((NonFictionBook) book).getSubject() : "";
                long quantity = getTotalStockByIsbn(book.getISBN()); // Hitung kuantitas dari inventaris

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
    // METODE PUBLIK UNTUK DIGUNAKAN OLEH CONTROLLER
    // =================================================================

    // Metode READ (seperti search dan get) akan bekerja pada data di memori yang sudah dimuat
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookCatalog);
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        String lowerCaseKeyword = keyword.toLowerCase();
        return bookCatalog.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    // Metode lainnya untuk mendapatkan informasi stok dari inventaris di memori
    public long getTotalStockByIsbn(String isbn) {
        return bookInventory.stream().filter(item -> item.getBook().getISBN().equals(isbn)).count();
    }

    public long getAvailableStockByIsbn(String isbn) {
        return bookInventory.stream().filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE).count();
    }

    public BookItem findAvailableBookItem(String isbn) {
        return bookInventory.stream().filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE).findFirst().orElse(null);
    }

    public Book findBookByIsbn(String isbn) {
        return bookCatalog.stream().filter(b -> b.getISBN().equals(isbn)).findFirst().orElse(null);
    }

    // =================================================================
    // METODE PEMBANTU (HELPER)
    // =================================================================

    private Book createBookFromCsvValues(String[] values) {
        String isbn = values[0].trim();
        String title = values[1].trim();
        String author = values[2].trim();
        String publisher = values[3].trim();
        String coverPath = values[4].trim();  // Kolom ke-5 di CSV (cover_path)
        String category = values[5].trim();  // Kolom ke-6 (category)
        String genre = values[6].trim();     // Kolom ke-7 (genre)
        String subject = values[7].trim();   // Kolom ke-8 (subject)

        if ("Fiction".equalsIgnoreCase(category)) {
            return new FictionBook(title, author, publisher, isbn, coverPath,  // Parameter ke-5: coverImagePathgenre,      // Parameter ke-6: genre
                    ""          // Parameter ke-7: description (kosongkan jika tidak ada)
            );
        } else {
            return new NonFictionBook(
                    title, author, publisher, isbn,
                    coverPath,  // Parameter ke-5: coverImagePath
                    subject,    // Parameter ke-6: subject
                    ""         // Parameter ke-7: description (kosongkan jika tidak ada)
            );
        }
    }

    private void addBookItems(Book book, int quantity) {
        for (int i = 0; i < quantity; i++) {
            String barcode = book.getISBN() + "-" + (i + 1);
            bookInventory.add(new BookItem(barcode, book));
        }
    }

    public boolean updateBook(String isbn, String newTitle, String newAuthor) {
        Book bookToUpdate = findBookByIsbn(isbn);
        if (bookToUpdate == null) {
            return false; // Gagal karena buku tidak ditemukan
        }
        bookToUpdate.setTitle(newTitle);    // <-- UPDATE di memori
        bookToUpdate.setAuthor(newAuthor);  // <-- UPDATE di memori
        saveBooksToCsv();                   // <-- Menyimpan hasil UPDATE ke file
        return true;
    }


    public boolean deleteBook(String isbn) {
        // Menghapus dari inventaris fisik dan katalog metadata
        boolean inventoryRemoved = bookInventory.removeIf(item -> item.getBook().getISBN().equals(isbn));
        boolean catalogRemoved = bookCatalog.removeIf(book -> book.getISBN().equals(isbn)); // <-- DELETE dari memori

        if (catalogRemoved) {
            saveBooksToCsv(); // <-- Menyimpan hasil DELETE ke file
        }
        return catalogRemoved;
    }

    private String escapeCsv(String data) {
        if (data == null || data.isEmpty()) return "";
        if (data.contains(",")) {
            return "\"" + data.replace("\"", "\"\"") + "\"";
        }
        return data;
    }
}