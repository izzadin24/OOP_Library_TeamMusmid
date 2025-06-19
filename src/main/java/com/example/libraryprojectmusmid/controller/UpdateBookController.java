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

public class UpdateBookController {

    @FXML private TextField judulField;
    @FXML private TextField penulisField;
    @FXML private TextField penerbitField;
    @FXML private TextField isbnField;
    @FXML private TextField kategoriField;
    @FXML private TextField genreBidangField;
    @FXML private TextField stokField;
    @FXML private Button ubahButton;
    @FXML private Button kembaliButton;

    private Book bookToUpdate;
    private final BookService bookService = BookService.getInstance();

    /**
     * Metode ini adalah kunci untuk menerima data dari halaman sebelumnya.
     * Ini akan dipanggil secara manual dari ManageBookController.
     * @param book Objek buku yang akan diedit.
     */
    public void initData(Book book) {
        this.bookToUpdate = book;

        // Isi semua field dengan data dari objek buku yang diterima
        judulField.setText(book.getTitle());
        penulisField.setText(book.getAuthor());
        penerbitField.setText(book.getPublisher());
        isbnField.setText(book.getISBN());

        if (book instanceof FictionBook) {
            kategoriField.setText("Fiction");
            genreBidangField.setText(((FictionBook) book).getGenre());
        } else if (book instanceof NonFictionBook) {
            kategoriField.setText("NonFiction");
            genreBidangField.setText(((NonFictionBook) book).getSubject());
        }

        stokField.setText(String.valueOf(bookService.getTotalStockByIsbn(book.getISBN())));
    }

    @FXML
    private void handleUbah() {
        if (bookToUpdate == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Tidak ada buku yang dipilih untuk diubah.");
            return;
        }

        // Ambil data yang mungkin diubah dari form
        String newTitle = judulField.getText().trim();
        String newAuthor = penulisField.getText().trim();
        String newPublisher = penerbitField.getText().trim();

        // Validasi
        if (newTitle.isEmpty() || newAuthor.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Judul dan Penulis tidak boleh kosong.");
            return;
        }

        // Panggil service untuk melakukan update
        boolean success = bookService.updateBook(bookToUpdate.getISBN(), newTitle, newAuthor, newPublisher);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data buku berhasil diubah.");
            // Kembali ke halaman manajemen setelah berhasil
            handleKembali();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengubah data buku.");
        }
    }

    @FXML
    private void handleKembali() {
        NavigationUtil.navigateTo(kembaliButton, "/com/example/libraryprojectmusmid/view/ManageBookView.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}