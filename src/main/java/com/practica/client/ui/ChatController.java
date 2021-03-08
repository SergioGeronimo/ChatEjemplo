package com.practica.client.ui;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ChatController {
    public FlowPane cameraContainer;
    public ImageView camImage;
    public TextArea chatMessage;

    public void sendMessage(KeyEvent key){

        String messageText = chatMessage.getText();


    }

    public void readCamera(){
        Webcam webcam = Webcam.getDefault();
        Dimension webcamViewSize = webcam.getViewSize();
        webcam.open();
        SimpleObjectProperty<Image> frameProperty = new SimpleObjectProperty<>();
        boolean isCameraActive = true;
        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                while (isCameraActive) {
                    BufferedImage bufferedImage = webcam.getImage();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            final Image frame = SwingFXUtils.toFXImage(bufferedImage, null);
                            frameProperty.set(frame);
                        }
                    });
                    bufferedImage.flush();
                }
                return null;

            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        camImage = new ImageView();
        camImage.imageProperty().bind(frameProperty);
        cameraContainer.getChildren().add(camImage);

    }
}
