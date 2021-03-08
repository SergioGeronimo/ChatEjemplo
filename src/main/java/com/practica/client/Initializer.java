package com.practica.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Initializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("prism.lcdtext", "false");
        Parent root = FXMLLoader.load(getClass().getResource("/start.fxml"));
        Scene scene = new Scene(root, 400, 600 );
        scene.getStylesheets().add(getClass().getResource("/ui-theme.css").toString());
        Font.loadFont(getClass().getResourceAsStream("/fonts/Jost.ttf"), 16);

        stage.setTitle("com.practica.client.ui.Chat");
        stage.setScene(scene);
        stage.show();
    }
}
