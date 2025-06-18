package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.book.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BookService adalah kelas Singleton yang bertanggung jawab untuk mengelola
 * semua data buku di perpustakaan. Ini menangani operasi CRUD (Create, Read,
 * Update, Delete) untuk buku.
 */
public class BookService {

    private static BookService instance;
    private final List<Book> bookCatalog;     // Daftar informasi unik setiap buku (metadata)
    private final List<BookItem> bookInventory; // Daftar setiap item/eksemplar fisik buku

    private BookService() {
        this.bookCatalog = new ArrayList<>();
        this.bookInventory = new ArrayList<>();
        // Anda bisa memindahkan path file ke file konfigurasi untuk fleksibilitas
        loadBooksFromCsv("data/books.csv");
    }

    /**
     * Mendapatkan satu-satunya instance dari BookService (Singleton Pattern).
     * @return instance BookService.
     */
    public static synchronized BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }

    // =================================================================
    // METODE CRUD (CREATE, READ, UPDATE, DELETE)
    // =================================================================


    //CREATE
    public boolean addBook(String isbn, String title, String author, String publisher, String category, int quantity) {
        // Cek apakah buku dengan ISBN ini sudah ada di katalog
        if (findBookByIsbn(isbn) != null) {
            System.err.println("Error: Buku dengan ISBN " + isbn + " sudah ada.");
            return false;
        }

        Book newBook;
        if ("Fiction".equalsIgnoreCase(category)) {
            // Parameter genre dan subgenre bisa dibuat lebih dinamis
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

 //READ
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(bookCatalog); // Kembalikan salinan untuk mencegah modifikasi eksternal
        }
        String lowerCaseKeyword = keyword.toLowerCase();
        return bookCatalog.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseKeyword) ||
                        book.getISBN().equalsIgnoreCase(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    //UPDATE
    public boolean updateBook(String isbn, String newTitle, String newAuthor, String newPublisher) {
        Book bookToUpdate = findBookByIsbn(isbn);

        if (bookToUpdate == null) {
            System.err.println("Error: Buku dengan ISBN " + isbn + " tidak ditemukan.");
            return false;
        }

        // Update informasi pada objek Book di katalog
        bookToUpdate.setTitle(newTitle);
        bookToUpdate.setAuthor(newAuthor);
        bookToUpdate.setPublisher(newPublisher);

        // Karena bookInventory menyimpan REFERENSI ke objek Book, semua BookItem
        // yang terkait akan secara otomatis merefleksikan perubahan ini.
        // Tidak perlu loop melalui bookInventory.

        System.out.println("Informasi buku dengan ISBN " + isbn + " berhasil diperbarui.");
        return true;
    }

    //DELETE
    public boolean deleteBook(String isbn) {
        Book bookToDelete = findBookByIsbn(isbn);
        if (bookToDelete == null) {
            System.err.println("Error: Buku dengan ISBN " + isbn + " tidak ditemukan.");
            return false;
        }

        // Langkah 1: Hapus semua item buku dari inventaris yang merujuk ke buku ini.
        // Ini penting untuk mencegah memory leak dan referensi yang tidak valid.
        bookInventory.removeIf(bookItem -> bookItem.getBook().getISBN().equals(isbn));

        // Langkah 2: Hapus buku dari katalog.
        bookCatalog.remove(bookToDelete);

        System.out.println("Buku '" + bookToDelete.getTitle() + "' dan semua eksemplarnya berhasil dihapus.");
        return true;
    }


    // =================================================================
    // METODE PEMBANTU (HELPER METHODS)
    // =================================================================

    /**
     * READ (Helper): Mendapatkan semua buku di katalog.
     * @return Salinan dari daftar buku di katalog.
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookCatalog);
    }

    /**
     * READ (Helper): Mencari objek buku di katalog berdasarkan ISBN.
     * @param isbn ISBN yang dicari.
     * @return Objek Book jika ditemukan, null jika tidak.
     */
    public Book findBookByIsbn(String isbn) {
        // Menggunakan stream untuk cara yang lebih modern
        return bookCatalog.stream()
                .filter(b -> b.getISBN().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    /**
     * READ (Helper): Mencari item buku yang tersedia (bisa dipinjam) berdasarkan ISBN.
     * @param isbn ISBN buku.
     * @return BookItem yang tersedia jika ada, null jika semua sedang dipinjam atau tidak ada.
     */
    public BookItem findAvailableBookItem(String isbn) {
        return bookInventory.stream()
                .filter(item -> item.getBook().getISBN().equals(isbn) && item.getStatus() == BookStatus.AVAILABLE)
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper untuk memuat data buku dari file CSV saat inisialisasi.
     * @param filePath Path menuju file CSV.
     */
    private void loadBooksFromCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Lewati baris header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 6) continue;

                String isbn = values[0].trim();
                String title = values[1].trim();
                String author = values[2].trim();
                String publisher = values[3].trim();
                String category = values[4].trim();
                int quantity = Integer.parseInt(values[5].trim());

                // Gunakan metode addBook yang sudah dibuat agar logikanya terpusat
                // Cek dulu agar tidak duplikat saat inisialisasi
                if(findBookByIsbn(isbn) == null) {
                    addBook(isbn, title, author, publisher, category, quantity);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error saat memuat buku dari CSV: " + e.getMessage());
        }
    }

    /**
     * Helper untuk menambahkan sejumlah item/eksemplar buku ke inventaris.
     * @param book Objek buku yang itemnya akan ditambahkan.
     * @param quantity Jumlah item yang ditambahkan.
     */
    private void addBookItems(Book book, int quantity) {
        for (int i = 0; i < quantity; i++) {
            // Menghitung jumlah eksemplar yang sudah ada untuk membuat barcode unik
            long currentCopies = bookInventory.stream().filter(item -> item.getBook().getISBN().equals(book.getISBN())).count();
            String barcode = book.getISBN() + "-" + (currentCopies + 1);
            // Anda perlu memastikan kelas BookItem dan BookStatus sudah ada
            // bookInventory.add(new BookItem(barcode, book, BookStatus.AVAILABLE));
        }
    }
}