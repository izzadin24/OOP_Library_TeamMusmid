package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.book.Book;
import com.example.libraryprojectmusmid.model.book.FictionBook;
import com.example.libraryprojectmusmid.service.BookService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;

public class SearchBookController {

    // Elemen UI
    @FXML private TextField searchField;
    @FXML private StackPane resultContainer;
    @FXML private HBox bookResultPane;
    @FXML private Label notFoundLabel;
    @FXML private Button backButton;

    // Label untuk menampilkan data buku
    @FXML private Label judulLabel;
    @FXML private Label pengarangLabel;
    @FXML private Label isbnLabel;
    @FXML private Label genreLabel;
    @FXML private Label stokLabel;
    @FXML private Label tersediaLabel;

    // Service untuk mengakses data
    private final BookService bookService = BookService.getInstance();

    @FXML
    public void initialize() {
        // Pada awalnya, sembunyikan semua panel hasil
        bookResultPane.setVisible(false);
        notFoundLabel.setVisible(false);
        resultContainer.setStyle("-fx-background-color: transparent;"); // Buat kontainer transparan
    }

    /**
     * Metode ini dipanggil saat pengguna menekan Enter di searchField.
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            // Jika input kosong, sembunyikan semua hasil
            initialize();
            return;
        }

        // Cari buku menggunakan BookService
        List<Book> searchResult = bookService.searchBooks(keyword);
        resultContainer.setStyle("-fx-background-color: #B9D9EB; -fx-background-radius: 20;"); // Tampilkan background

        if (searchResult.isEmpty()) {
            // Jika tidak ada hasil
            bookResultPane.setVisible(false);
            notFoundLabel.setVisible(true);
        } else {
            // Jika ada hasil, ambil buku pertama dan tampilkan
            Book foundBook = searchResult.get(0);
            displayBookDetails(foundBook);
            bookResultPane.setVisible(true);
            notFoundLabel.setVisible(false);
        }
    }

    /**
     * Metode pembantu untuk mengisi label dengan detail buku.
     * @param book Objek buku yang ditemukan.
     */
    private void displayBookDetails(Book book) {
        judulLabel.setText(book.getTitle());
        pengarangLabel.setText(book.getAuthor());
        isbnLabel.setText(book.getISBN());

        // Cek apakah buku adalah fiksi untuk mendapatkan genre
        if (book instanceof FictionBook) {
            genreLabel.setText(((FictionBook) book).getGenre());
        } else {
            genreLabel.setText("Non-Fiksi"); // Atau ambil dari NonFictionBook jika ada
        }

        // Untuk Stok dan Jumlah Tersedia, kita perlu logika tambahan di BookService
        // Mari kita asumsikan metodenya sudah ada untuk saat ini
        long totalStock = bookService.getTotalStockByIsbn(book.getISBN());
        long availableStock = bookService.getAvailableStockByIsbn(book.getISBN());

        stokLabel.setText(String.valueOf(totalStock));
        tersediaLabel.setText(String.valueOf(availableStock));
    }

    @FXML
    private void handleBackButton() {
        NavigationUtil.navigateTo(backButton, "/com/example/libraryprojectmusmid/view/MemberDashboard.fxml");
    }
}