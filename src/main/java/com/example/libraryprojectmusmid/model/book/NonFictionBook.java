package com.example.libraryprojectmusmid.model.book;

public class NonFictionBook extends Book {
    private String subject;
    private String deweyDecimal;

    /**
     * Konstruktor untuk NonfictionBook.
     * @param title Judul buku
     * @param author Penulis buku
     * @param publisher Penerbit buku
     * @param ISBN Nomor ISBN buku
     * @param subject Subjek atau topik utama buku
     * @param deweyDecimal Nomor Klasifikasi Desimal Dewey
     */
    public NonFictionBook(String title, String author, String publisher, String ISBN, String subject, String deweyDecimal) {
        super(title, author, publisher, ISBN); // Memanggil konstruktor kelas induk
        this.subject = subject;
        this.deweyDecimal = deweyDecimal;
    }

    /**
     * Implementasi konkret dari metode abstrak showBookInfo.
     * Menampilkan semua informasi buku ditambah detail non-fiksi.
     */
    @Override
    public void showBookInfo() {
        System.out.println("--- Nonfiction Book Info ---");
        System.out.println("Title: " + getTitle());
        System.out.println("Author: " + getAuthor());
        System.out.println("Publisher: " + getPublisher());
        System.out.println("ISBN: " + getISBN());
        System.out.println("Subject: " + this.subject);
        System.out.println("Dewey Decimal: " + this.deweyDecimal);
        System.out.println("----------------------------");
    }

    // --- Getters and Setters untuk atribut baru ---

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDeweyDecimal() {
        return deweyDecimal;
    }

    public void setDeweyDecimal(String deweyDecimal) {
        this.deweyDecimal = deweyDecimal;
    }
}