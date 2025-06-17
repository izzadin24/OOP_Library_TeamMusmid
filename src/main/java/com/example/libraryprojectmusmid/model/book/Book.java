package com.example.libraryprojectmusmid.model.book;

public abstract class Book {
    private String title;
    private String author;
    private String publisher;
    private String ISBN;

    /**
     * Konstruktor untuk kelas Book.
     * Dipanggil oleh kelas turunannya melalui super().
     * @param title Judul buku
     * @param author Penulis buku
     * @param publisher Penerbit buku
     * @param ISBN Nomor ISBN buku
     */
    public Book(String title, String author, String publisher, String ISBN) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.ISBN = ISBN;
    }

    /**
     * Metode abstrak untuk menampilkan informasi unik dari setiap jenis buku.
     * Metode ini WAJIB di-override oleh setiap kelas turunan.
     */
    public abstract void showBookInfo();

    // --- Getters and Setters ---

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
}

