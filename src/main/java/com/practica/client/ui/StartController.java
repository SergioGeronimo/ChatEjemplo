package com.practica.client.ui;

import com.practica.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class StartController {
    @FXML
    private VBox layout;
    @FXML
    private TextField iPAddress;
    @FXML
    private TextField port;

    public void connect(ActionEvent actionEvent) throws IOException {
        String iPAddress = this.iPAddress.getText();
        int port = Integer.parseInt(this.port.getText());


        Client client = new Client(iPAddress, port);
        client.connect();
        Thread clientService = new Thread(client);
        clientService.start();


        Scene scene = ((Node) actionEvent.getSource()).getScene();

        Parent root = FXMLLoader.load(getClass().getResource("/chat.fxml"));

        scene.setRoot(root);
        scene.getWindow().setWidth(700);
        scene.getWindow().setHeight(500);
        scene.getWindow().centerOnScreen();


    }
}
