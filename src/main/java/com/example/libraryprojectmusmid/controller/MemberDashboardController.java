package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

public class MemberDashboardController {

    // Deklarasi untuk menampung ikon-ikon (sudah benar)
    @FXML private Label bukuTersediaIconLabel;
    @FXML private Label bukuDipinjamIconLabel;
    @FXML private Label statusAkunIconLabel;
    @FXML private Label pinjamBukuIconLabel;
    @FXML private Label logoutIconLabel; // Menggantikan placeholder
    @FXML private Label lihatBukuIconLabel;

    /**
     * Metode yang dipanggil otomatis saat FXML dimuat.
     */
    @FXML
    public void initialize() {
        // Panggil metode untuk memuat semua ikon
        loadAllIcons();
    }

    // =================================================================
    // INI ADALAH METODE-METODE HANDLER YANG HILANG
    // Setiap metode ini terhubung ke satu tombol di FXML.
    // =================================================================

    @FXML
    private void handleBukuTersedia() {
        System.out.println("Tombol 'Buku Tersedia' diklik!");
        // Pindah ke halaman daftar buku
        NavigationUtil.navigateTo(bukuTersediaIconLabel, "/com/example/libraryprojectmusmid/view/BookListView.fxml");
    }

    @FXML
    private void handleBukuDipinjam() {
        System.out.println("Tombol 'Buku Dipinjam' diklik!");
        // TODO: Tambahkan logika untuk menampilkan buku yang sedang dipinjam user
    }

    @FXML
    private void handleStatusAkun() {
        System.out.println("Tombol 'Status Akun' diklik! Menavigasi ke halaman akun...");
        NavigationUtil.navigateTo(statusAkunIconLabel, "/com/example/libraryprojectmusmid/view/AccountView.fxml");
    }
    @FXML
    private void handlePinjamBuku() {
        System.out.println("Tombol 'Pinjam Buku' diklik!");
        // TODO: Tambahkan logika untuk pindah ke halaman peminjaman
    }

    @FXML
    private void handleLogout() {
        System.out.println("Tombol 'Logout' diklik!");
        // Kembali ke halaman login
        NavigationUtil.navigateTo(logoutIconLabel, "/com/example/libraryprojectmusmid/view/library_login.fxml");
    }

    @FXML
    private void handleLihatBuku() {
        System.out.println("Tombol 'Lihat Buku' diklik! Menavigasi ke halaman pencarian...");
        // Arahkan ke halaman pencarian buku yang baru
        NavigationUtil.navigateTo(lihatBukuIconLabel, "/com/example/libraryprojectmusmid/view/SearchBookView.fxml");
    }

    // =================================================================
    // KODE UNTUK MEMUAT GAMBAR IKON (TIDAK PERLU DIUBAH)
    // =================================================================

    private void loadAllIcons() {
        // Pastikan path dan nama file gambar sesuai
        loadIcon(bukuTersediaIconLabel, "/images/books.png");
        loadIcon(bukuDipinjamIconLabel, "/images/addBook.png");
        loadIcon(statusAkunIconLabel, "/images/user-access.png");
        loadIcon(pinjamBukuIconLabel, "/images/loanbook.png");
        loadIcon(logoutIconLabel, "/images/logout.png"); // Ganti dengan ikon logout jika ada
        loadIcon(lihatBukuIconLabel, "/images/viewbook.png");
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
                label.setText("!");
            }
        } catch (Exception e) {
            System.err.println("Error saat memuat gambar: " + imagePath);
            e.printStackTrace();
        }
    }
}