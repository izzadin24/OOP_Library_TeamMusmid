package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.user.Member;
import com.example.libraryprojectmusmid.model.user.User;
import com.example.libraryprojectmusmid.service.AuthenticationService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
// Import untuk Image dan ImageView tidak diperlukan lagi di sini
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import java.io.InputStream;

public class AccountViewController {

    // HAPUS deklarasi FXML untuk ikon profil
    // @FXML private Label profileIconLabel;

    @FXML private Label namaLabel;
    @FXML private Label nimLabel;
    @FXML private Label jurusanLabel;
    @FXML private Label emailLabel;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        // HAPUS pemanggilan metode untuk memuat ikon
        // loadProfileIcon();

        User currentUser = AuthenticationService.getInstance().getCurrentUser();

        if (currentUser != null && currentUser instanceof Member) {
            Member currentMember = (Member) currentUser;
            namaLabel.setText(currentMember.getFullName());
            nimLabel.setText(currentMember.getUsername());
            jurusanLabel.setText(currentMember.getMajor());
            emailLabel.setText(currentMember.getEmail());
        } else {
            namaLabel.setText("Tidak Ditemukan");
            nimLabel.setText("Tidak Ditemukan");
            jurusanLabel.setText("Tidak Ditemukan");
            emailLabel.setText("Tidak Ditemukan");
        }
    }

    @FXML
    private void handleBackButton() {
        NavigationUtil.navigateTo(backButton, "/com/example/libraryprojectmusmid/view/MemberDashboard.fxml");
    }

    // HAPUS seluruh metode loadProfileIcon() karena sudah tidak digunakan lagi
    /*
    private void loadProfileIcon() {
        ...
    }
    */
}