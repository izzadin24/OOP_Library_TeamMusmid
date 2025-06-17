package com.example.libraryprojectmusmid.model.book;

public class BookItem {
    private final String barcode; // Final karena barcode buku fisik tidak pernah berubah
    private final Book book; // Referensi ke data konseptual buku
    private BookStatus status;

    /**
     * Konstruktor untuk membuat item buku baru.
     * @param barcode ID unik (barcode) untuk copy buku ini.
     * @param book Referensi ke objek Book (untuk mendapatkan title, author, dll).
     */
    public BookItem(String barcode, Book book) {
        this.barcode = barcode;
        this.book = book;
        this.status = BookStatus.AVAILABLE; // Status awal pasti tersedia
    }

    // --- Getters ---
    public String getBarcode() {
        return barcode;
    }

    public Book getBook() {
        return book;
    }

    public BookStatus getStatus() {
        return status;
    }

    // --- Setter ---
    // Hanya status yang bisa diubah (saat dipinjam atau dikembalikan)
    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
