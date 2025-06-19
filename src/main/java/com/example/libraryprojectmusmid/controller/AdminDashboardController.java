package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.util.Objects;

public class AdminDashboardController {

    // Deklarasi FXML untuk label ikon
    @FXML private Label kelolaBukuIconLabel;
    @FXML private Label kelolaMemberIconLabel;
    @FXML private Label laporanIconLabel;

    // Deklarasi FXML untuk tombol logout
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        // Panggil metode untuk memuat semua ikon saat halaman dibuka
        loadAllIcons();
    }

    // =================================================================
    // METODE HANDLER UNTUK SETIAP TOMBOL
    // =================================================================

    @FXML
    private void handleKelolaBuku() {
        System.out.println("Tombol 'Kelola Buku' diklik!");
        // TODO: Arahkan ke halaman manajemen buku untuk admin
        NavigationUtil.navigateTo(kelolaBukuIconLabel, "/com/example/libraryprojectmusmid/view/ManageBookView.fxml");
    }

    @FXML
    private void handleKelolaMember() {
        System.out.println("Tombol 'Kelola Member' diklik!");
        // TODO: Arahkan ke halaman manajemen member untuk admin
        NavigationUtil.navigateTo(kelolaMemberIconLabel, "/com/example/libraryprojectmusmid/view/ManageMemberView.fxml");
    }

    @FXML
    private void handleLaporan() {
        System.out.println("Tombol 'Laporan' diklik!");
        NavigationUtil.navigateTo(laporanIconLabel, "/com/example/libraryprojectmusmid/view/StatisticView.fxml");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Tombol Logout diklik!");
        // Logika untuk kembali ke halaman login
        NavigationUtil.navigateTo(logoutButton, "/com/example/libraryprojectmusmid/view/library_login.fxml");
    }

    // =================================================================
    // KODE UNTUK MEMUAT GAMBAR IKON
    // =================================================================

    private void loadAllIcons() {
        // Ganti nama file gambar sesuai dengan yang Anda miliki
        loadIcon(kelolaBukuIconLabel, "/images/books.png");
        loadIcon(kelolaMemberIconLabel, "/images/user-access.png");
        loadIcon(laporanIconLabel, "/images/statistics.png");
    }

    private void loadIcon(Label label, String imagePath) {
        try (InputStream imageStream = getClass().getResourceAsStream(imagePath)) {
            if (imageStream != null) {
                Image image = new Image(imageStream);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(64);
                imageView.setFitHeight(64);
                label.setGraphic(imageView);
            } else {
                System.err.println("Gambar tidak ditemukan di path: " + imagePath);
                label.setText("!"); // Tampilkan placeholder jika gambar tidak ada
            }
        } catch (Exception e) {
            System.err.println("Error saat memuat gambar: " + imagePath);
            e.printStackTrace();
        }
    }
}