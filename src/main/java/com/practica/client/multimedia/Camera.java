package com.practica.client.multimedia;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {
    boolean isCameraActive;
    Webcam webcam;

    public Camera (){
        Webcam webcam = Webcam.getDefault();
        webcam.open();
    }

    public SimpleObjectProperty<Image> readCamera(){
        SimpleObjectProperty<Image> frameProperty = new SimpleObjectProperty<>();
        isCameraActive = true;
        System.out.println("A");
        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                System.out.println("B");
                while (isCameraActive) {
                    System.out.println("C");
                    BufferedImage bufferedImage = webcam.getImage();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            final Image frame = SwingFXUtils.toFXImage(bufferedImage, null);
                            frameProperty.set(frame);
                            System.out.println("camara");
                        }
                    });
                    bufferedImage.flush();
                    System.out.println("D");
                }
                return null;

            }
        };
        System.out.println("E");
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        System.out.println("F");
        return frameProperty;
    }
}
