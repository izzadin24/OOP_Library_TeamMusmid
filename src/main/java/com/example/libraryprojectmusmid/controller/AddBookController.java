package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.book.Book;
import com.example.libraryprojectmusmid.model.book.FictionBook;
import com.example.libraryprojectmusmid.model.book.NonFictionBook;
import com.example.libraryprojectmusmid.service.BookService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddBookController {

    @FXML private TextField judulField;
    @FXML private TextField penulisField;
    @FXML private TextField penerbitField;
    @FXML private TextField isbnField;
    @FXML private TextField kategoriField;
    @FXML private TextField genreBidangField;
    @FXML private TextField stokField;
    @FXML private Button tambahkanButton;
    @FXML private Button kembaliButton;

    private final BookService bookService = BookService.getInstance();

    @FXML
    private void handleTambahkan() {
        // 1. Ambil semua data dari form
        String judul = judulField.getText().trim();
        String penulis = penulisField.getText().trim();
        String penerbit = penerbitField.getText().trim();
        String isbn = isbnField.getText().trim();
        String kategori = kategoriField.getText().trim();
        String genreAtauBidang = genreBidangField.getText().trim();
        String stokText = stokField.getText().trim();

        // 2. Validasi input
        if (judul.isEmpty() || penulis.isEmpty() || isbn.isEmpty() || kategori.isEmpty() || stokText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Judul, Penulis, ISBN, Kategori, dan Stok wajib diisi.");
            return;
        }

        int stok;
        try {
            stok = Integer.parseInt(stokText);
            if (stok <= 0) {
                showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Stok harus berupa angka positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Stok harus berupa angka.");
            return;
        }

        // 3. Buat objek Book berdasarkan kategori
        Book newBook;
        String coverPath = "/images/blank-cover.png"; // Path cover default

        if ("Fiction".equalsIgnoreCase(kategori)) {
            newBook = new FictionBook(judul, penulis, penerbit, isbn, coverPath, genreAtauBidang);
        } else if ("NonFiction".equalsIgnoreCase(kategori)) {
            newBook = new NonFictionBook(judul, penulis, penerbit, isbn, coverPath, genreAtauBidang, "");
        } else {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Kategori harus diisi dengan 'Fiction' atau 'NonFiction'.");
            return;
        }

        // 4. Panggil service untuk menambahkan buku
        // Note: Kita perlu metode addBook di BookService yang menerima Book dan quantity
        boolean success = bookService.addBook(newBook, stok);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku '" + judul + "' berhasil ditambahkan dengan stok " + stok + ".");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Buku dengan ISBN '" + isbn + "' mungkin sudah ada.");
        }
    }

    @FXML
    private void handleKembali() {
        // Kembali ke halaman Kelola Buku
        NavigationUtil.navigateTo(kembaliButton, "/com/example/libraryprojectmusmid/view/ManageBookView.fxml");
    }

    private void clearFields() {
        judulField.clear();
        penulisField.clear();
        penerbitField.clear();
        isbnField.clear();
        kategoriField.clear();
        genreBidangField.clear();
        stokField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}