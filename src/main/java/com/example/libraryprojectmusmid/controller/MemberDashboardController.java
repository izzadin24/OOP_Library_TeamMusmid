package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button; // <-- Tambahkan import untuk Button
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.util.Objects;

public class MemberDashboardController {

    // Deklarasi FXML untuk ikon
    @FXML private Label bukuTersediaIconLabel;
    @FXML private Label bukuDipinjamIconLabel;
    @FXML private Label statusAkunIconLabel;
    @FXML private Label pinjamBukuIconLabel;
    @FXML private Label kembalikanBukuIconLabel; // <-- Nama diubah agar sesuai
    @FXML private Label lihatBukuIconLabel;

    // Deklarasi FXML untuk Tombol Logout baru
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        loadAllIcons();
    }

    @FXML
    private void handleBukuTersedia() {
        System.out.println("Tombol 'Buku Tersedia' diklik!");
        NavigationUtil.navigateTo(bukuTersediaIconLabel, "/com/example/libraryprojectmusmid/view/BookListView.fxml");
    }

    @FXML
    private void handleBukuDipinjam() {
        System.out.println("Tombol 'Riwayat Peminjaman' diklik!");
        // TODO: Arahkan ke halaman riwayat peminjaman
        NavigationUtil.navigateTo(bukuDipinjamIconLabel, "/com/example/libraryprojectmusmid/view/HistoryView.fxml");
    }

    @FXML
    private void handleStatusAkun() {
        System.out.println("Tombol 'Status Akun' diklik! Menavigasi ke halaman akun...");
        NavigationUtil.navigateTo(statusAkunIconLabel, "/com/example/libraryprojectmusmid/view/AccountView.fxml");
    }

    @FXML
    private void handlePinjamBuku() {
        System.out.println("Tombol 'Pinjam Buku' diklik!");
        NavigationUtil.navigateTo(pinjamBukuIconLabel, "/com/example/libraryprojectmusmid/view/LoanView.fxml");
    }

    // ==========================================================
    // PERUBAHAN DI SINI: Nama metode diubah
    // ==========================================================
    @FXML
    private void handleKembalikanBuku() {
        System.out.println("Tombol 'Kembalikan Buku' diklik!");
        NavigationUtil.navigateTo(kembalikanBukuIconLabel, "/com/example/libraryprojectmusmid/view/ReturnView.fxml");
    }

    // ==========================================================
    // PERUBAHAN DI SINI: Metode baru untuk tombol Logout di bawah
    // ==========================================================
    @FXML
    private void handleLogout() {
        System.out.println("Tombol 'Logout' di bawah diklik!");
        // Logika untuk kembali ke halaman login
        NavigationUtil.navigateTo(logoutButton, "/com/example/libraryprojectmusmid/view/library_login.fxml");
    }

    @FXML
    private void handleLihatBuku() {
        System.out.println("Tombol 'Cari Buku' diklik! Menavigasi ke halaman pencarian...");
        NavigationUtil.navigateTo(lihatBukuIconLabel, "/com/example/libraryprojectmusmid/view/SearchBookView.fxml");
    }

    private void loadAllIcons() {
        loadIcon(bukuTersediaIconLabel, "/images/books.png");
        loadIcon(bukuDipinjamIconLabel, "/images/addBook.png"); // Ganti ikon jika perlu
        loadIcon(statusAkunIconLabel, "/images/user-access.png");
        loadIcon(pinjamBukuIconLabel, "/images/loanbook.png");
        loadIcon(kembalikanBukuIconLabel, "/images/kembalikan.png"); // Ganti ikon jika perlu
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
                // Fallback ke gambar default jika tidak ada
                InputStream defaultStream = getClass().getResourceAsStream("/images/default-icon.png");
                if (defaultStream != null) {
                    label.setGraphic(new ImageView(new Image(defaultStream, 64, 64, true, true)));
                } else {
                    label.setText("!");
                }
            }
        } catch (Exception e) {
            System.err.println("Error saat memuat gambar: " + imagePath);
            e.printStackTrace();
        }
    }
}