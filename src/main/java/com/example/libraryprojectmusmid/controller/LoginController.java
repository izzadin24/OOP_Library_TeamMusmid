package com.example.libraryprojectmusmid.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField nimField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String nim = nimField.getText();
        String password = passwordField.getText();

        if (isValidLogin(nim, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login berhasil!");
            // navigasi ke halaman berikutnya
        } else {
            showAlert(Alert.AlertType.ERROR, "NIM atau Password salah.");
        }
    }

    @FXML
    private void handleAdminLogin() {
        showAlert(Alert.AlertType.INFORMATION, "Pindah ke halaman login admin...");
        // navigasi ke login admin
    }

    private boolean isValidLogin(String nim, String password) {
        // Simulasi validasi login mahasiswa (bisa diganti koneksi DB)
        return nim.equals("202310370311123") && password.equals("perpustakaan123");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
