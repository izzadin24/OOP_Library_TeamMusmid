package com.example.libraryprojectmusmid.model.book;

public class FictionBook extends Book {
    private String genre;
    private String seriesName;

    /**
     * Konstruktor untuk FictionBook.
     * @param title Judul buku
     * @param author Penulis buku
     * @param publisher Penerbit buku
     * @param ISBN Nomor ISBN buku
     * @param genre Genre dari buku fiksi
     * @parae Nama seri jika buku ini adalah bagian dari seri
     */
    public FictionBook(String title, String author, String publisher,
                       String ISBN, String coverImagePath,
                       String genre) {
        super(title, author, publisher, ISBN, coverImagePath);
        this.genre = genre;

    }

    /**
     * Implementasi konkret dari metode abstrak showBookInfo.
     * Menampilkan semua informasi buku ditambah detail fiksi.
     */
    @Override
    public void showBookInfo() {
        System.out.println("--- Fiction Book Info ---");
        System.out.println("Title: " + getTitle());
        System.out.println("Author: " + getAuthor());
        System.out.println("Publisher: " + getPublisher());
        System.out.println("ISBN: " + getISBN());
        System.out.println("Genre: " + this.genre);
        System.out.println("Series: " + this.seriesName);
        System.out.println("-------------------------");
    }

    // --- Getters and Setters untuk atribut baru ---

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
}
