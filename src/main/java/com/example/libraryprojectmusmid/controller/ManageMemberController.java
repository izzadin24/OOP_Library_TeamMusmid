package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.service.UserService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ManageMemberController {

    // Deklarasi elemen FXML dari form
    @FXML private TextField idMemberField;
    @FXML private TextField namaLengkapField;
    @FXML private TextField jurusanField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button tambahkanButton;
    @FXML private Button kembaliButton;

    // Instance dari UserService untuk mengakses logika backend
    private final UserService userService = UserService.getInstance();

    /**
     * Metode ini dijalankan saat tombol "Tambahkan" diklik.
     */
    @FXML
    private void handleTambahkanButton() {
        String id = idMemberField.getText().trim();
        String nama = namaLengkapField.getText().trim();
        String jurusan = jurusanField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText(); // Password tidak perlu di-trim

        // Validasi input sederhana, pastikan semua field terisi
        if (id.isEmpty() || nama.isEmpty() || jurusan.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Semua field wajib diisi.");
            return;
        }

        // Panggil service untuk mendaftarkan member baru
        boolean success = userService.registerMember(username, password, id, nama, jurusan, email);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Member baru dengan nama " + nama + " berhasil ditambahkan!");
            clearFields(); // Kosongkan form setelah berhasil
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Member ID atau Username mungkin sudah terdaftar. Silakan periksa kembali.");
        }
    }

    /**
     * Metode untuk tombol kembali, mengarah ke Admin Dashboard.
     */
    @FXML
    private void handleKembaliButton() {
        // Asumsi kembali ke Admin Dashboard, karena ini adalah fitur admin
        NavigationUtil.navigateTo(kembaliButton, "/com/example/libraryprojectmusmid/view/AdminDashboard.fxml");
    }

    /**
     * Helper untuk membersihkan semua field input.
     */
    private void clearFields() {
        idMemberField.clear();
        namaLengkapField.clear();
        jurusanField.clear();
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    /**
     * Helper untuk menampilkan dialog notifikasi.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}