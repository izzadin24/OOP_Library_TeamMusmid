package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.book.Book;
import com.example.libraryprojectmusmid.service.BookService;
import com.example.libraryprojectmusmid.util.NavigationUtil; // <-- Pastikan ini di-import
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button; // <-- Import untuk Button
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BookListViewController {

    // Deklarasi FXML untuk tabel (tidak berubah)
    @FXML private TableView<Book> bookTableView;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> publisherColumn;
    @FXML private TableColumn<Book, String> isbnColumn;

    // ========================================================
    // DEKLARASI FXML BARU UNTUK TOMBOL KEMBALI
    // ========================================================
    @FXML
    private Button backButton;

    private final BookService bookService = BookService.getInstance();

    @FXML
    public void initialize() {
        // Logika untuk mengisi tabel (tidak berubah)
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        bookTableView.setItems(FXCollections.observableArrayList(bookService.getAllBooks()));
        System.out.println("Halaman Daftar Buku berhasil dimuat dan data telah ditampilkan!");
    }

    // ========================================================
    // METODE BARU UNTUK MENANGANI AKSI TOMBOL KEMBALI
    // ========================================================
    @FXML
    private void handleBackButton() {
        System.out.println("Tombol kembali diklik, navigasi ke Member Dashboard...");
        // Gunakan NavigationUtil untuk kembali ke dashboard.
        // `backButton` digunakan sebagai konteks untuk mendapatkan window saat ini.
        NavigationUtil.navigateTo(backButton, "/com/example/libraryprojectmusmid/view/MemberDashboard.fxml");
    }
}