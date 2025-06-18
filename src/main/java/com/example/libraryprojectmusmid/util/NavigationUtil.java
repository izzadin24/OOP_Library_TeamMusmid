package com.example.libraryprojectmusmid.util;

import com.example.libraryprojectmusmid.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    /**
     * Metode statis untuk menavigasi ke halaman FXML lain.
     * @param contextNode Node apa saja dari scene saat ini (misal: tombol atau label)
     * untuk mendapatkan Stage (window).
     * @param fxmlPath Path ke file FXML tujuan.
     */
    public static void navigateTo(Node contextNode, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource(fxmlPath));
            Stage stage = (Stage) contextNode.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Gagal menavigasi ke: " + fxmlPath);
            e.printStackTrace();
        }
    }
}