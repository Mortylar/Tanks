package com.example.auth;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AuthController {

    @FXML private Button connect;
    @FXML private Button cancel;
    @FXML private TextField port;

    @FXML
    public void initialize() {
        connect.addEventHandler(
            MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent ev) {
                    System.out.printf("\nPort = %s\n", getPort());
                }
            });

        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED,
                               new EventHandler<MouseEvent>() {
                                   @Override
                                   public void handle(MouseEvent ev) {
                                       reset();
                                   }
                               });
    }

    public String getPort() { return port.getText(); }

    public void reset() { port.clear(); }
}
