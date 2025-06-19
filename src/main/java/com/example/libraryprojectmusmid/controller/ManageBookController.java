package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.book.Book;
import com.example.libraryprojectmusmid.service.BookService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // <-- IMPORT BARU
import javafx.scene.Parent;    // <-- IMPORT BARU
import javafx.scene.Scene;      // <-- IMPORT BARU
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;      // <-- IMPORT BARU

import java.io.IOException;     // <-- IMPORT BARU
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ManageBookController {

    // ... (Deklarasi @FXML lainnya biarkan sama)
    @FXML private Button tambahBukuButton;
    @FXML private Button ubahButton;
    @FXML private Button kembaliButton;
    @FXML private TextField searchField;
    @FXML private VBox resultContainer;
    @FXML private Label notFoundLabel;
    @FXML private ImageView coverImageView;
    @FXML private Label judulLabel;
    @FXML private Label pengarangLabel;
    @FXML private Label stokLabel;
    @FXML private Button hapusButton;


    private final BookService bookService = BookService.getInstance();
    private Book currentBook;

    @FXML
    public void initialize() {
        resultContainer.setVisible(false);
        notFoundLabel.setVisible(false);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            resultContainer.setVisible(false);
            notFoundLabel.setVisible(false);
            currentBook = null;
            return;
        }

        List<Book> searchResult = bookService.searchBooks(keyword);

        if (searchResult.isEmpty()) {
            resultContainer.setVisible(false);
            notFoundLabel.setVisible(true);
            currentBook = null;
        } else {
            currentBook = searchResult.get(0);
            displayBookDetails(currentBook);
            resultContainer.setVisible(true);
            notFoundLabel.setVisible(false);
        }
    }

    @FXML
    private void handleTambahBuku() {
        System.out.println("Tombol 'Tambah Buku' diklik! Menavigasi ke form tambah buku...");
        NavigationUtil.navigateTo(tambahBukuButton, "/com/example/libraryprojectmusmid/view/AddBookView.fxml");
    }

    @FXML
    private void handleHapusBuku() {
        if (currentBook == null) return;
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Konfirmasi Penghapusan");
        confirmationAlert.setHeaderText("Anda akan menghapus buku: " + currentBook.getTitle());
        confirmationAlert.setContentText("Aksi ini tidak dapat dibatalkan. Apakah Anda yakin?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = bookService.deleteBook(currentBook.getISBN());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku '" + currentBook.getTitle() + "' berhasil dihapus.");
                searchField.clear();
                resultContainer.setVisible(false);
                currentBook = null;
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus buku.");
            }
        }
    }

    // ==========================================================
    // INI METODE YANG DIPERBARUI
    // ==========================================================
    @FXML
    private void handleUbahBuku() {
        if (currentBook == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan cari dan pilih buku yang akan diubah terlebih dahulu.");
            return;
        }

        try {
            // 1. Buat FXMLLoader untuk memuat FXML halaman tujuan
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libraryprojectmusmid/view/UpdateBookView.fxml"));

            // 2. Muat FXML menjadi sebuah Parent node
            Parent root = loader.load();

            // 3. Dapatkan instance dari controller halaman tujuan (UpdateBookController)
            UpdateBookController controller = loader.getController();

            // 4. Panggil metode initData di controller tujuan dan kirimkan objek buku saat ini
            controller.initData(currentBook);

            // 5. Lakukan navigasi secara manual
            Stage stage = (Stage) ubahButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Navigasi", "Gagal memuat halaman ubah buku.");
        }
    }

    @FXML
    private void handleKembali() {
        NavigationUtil.navigateTo(kembaliButton, "/com/example/libraryprojectmusmid/view/AdminDashboard.fxml");
    }

    private void displayBookDetails(Book book) {
        judulLabel.setText(book.getTitle());
        pengarangLabel.setText(book.getAuthor());
        stokLabel.setText("Stok Buku ada: " + bookService.getTotalStockByIsbn(book.getISBN()));
        try (InputStream imageStream = getClass().getResourceAsStream(book.getCoverImagePath())) {
            coverImageView.setImage(new Image(imageStream != null ? imageStream : Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        } catch (Exception e) {
            System.err.println("Gagal memuat cover: " + book.getCoverImagePath());
            coverImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}