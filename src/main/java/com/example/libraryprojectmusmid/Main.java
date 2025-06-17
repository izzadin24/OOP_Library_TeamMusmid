package com.example.libraryprojectmusmid;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libraryprojectmusmid/view/library_login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("myUMM Library Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}