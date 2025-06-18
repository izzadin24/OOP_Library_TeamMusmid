package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.Main; // Impor kelas Main
import com.example.libraryprojectmusmid.model.user.Role;
import com.example.libraryprojectmusmid.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField nimField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink adminLoginLink;

    private final AuthenticationService authService = AuthenticationService.getInstance();
    private boolean isAdminMode = false;

    @FXML
    private void handleLogin() {
        String username = nimField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "NIM/Username dan Password tidak boleh kosong.");
            return;
        }

        boolean loginSuccess = authService.login(username, password);

        if (loginSuccess) {
            Role role = authService.getCurrentUserRole();
            showAlert(Alert.AlertType.INFORMATION, "Login Berhasil!", "Selamat datang! Anda login sebagai " + role);

            // === INI BAGIAN NAVIGASINYA ===
            if (role == Role.ADMIN) {
                // Pindah ke Dashboard Admin
                navigateToPage("/com/example/libraryprojectmusmid/view/AdminDashboard.fxml");
            } else if (role == Role.MEMBER) {
                // Pindah ke Dashboard Member
                navigateToPage("/com/example/libraryprojectmusmid/view/MemberDashboard.fxml");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Kredensial salah.");
        }
    }

    /**
     * Metode untuk berpindah dari satu halaman FXML ke halaman FXML lain.
     * @param fxmlFile Path ke file FXML tujuan (dimulai dari root resources).
     */
    private void navigateToPage(String fxmlFile) {
        try {
            // Muat file FXML tujuan
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Parent root = loader.load();

            // Dapatkan stage (jendela) saat ini dari elemen mana saja di scene
            Stage stage = (Stage) nimField.getScene().getWindow();

            // Buat scene baru dengan konten dari FXML tujuan
            Scene scene = new Scene(root);

            // Ganti scene di stage dengan scene yang baru
            stage.setScene(scene);
            stage.centerOnScreen(); // Opsional: pusatkan jendela setelah ganti scene
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Navigasi", "Gagal memuat halaman: " + fxmlFile);
        }
    }

    // Metode lain yang sudah ada (toggleLoginMode, updateUIForMode, showAlert)
    // biarkan seperti sebelumnya...

    @FXML
    private void toggleLoginMode() {
        isAdminMode = !isAdminMode;
        updateUIForMode();
    }

    private void updateUIForMode() {
        if (isAdminMode) {
            nimField.setPromptText("Masukkan Username Admin");
            passwordField.setPromptText("Masukkan Password Admin");
            adminLoginLink.setText("Login sebagai Mahasiswa");
        } else {
            nimField.setPromptText("Masukkan NIM");
            passwordField.setPromptText("Masukkan Password atau PIC");
            adminLoginLink.setText("Admin Login");
        }
        nimField.clear();
        passwordField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}