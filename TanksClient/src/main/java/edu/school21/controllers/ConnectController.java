package com.example.controllers;

import com.example.observers.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ConnectController {

    @FXML private Button connect;
    @FXML private Button cancel;
    @FXML private TextField port;

    private Observable observer;

    @FXML
    public void initialize() {
        connect.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent ev) {
                                        observer.notifyView();
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

    public void setObserver(Observable observer) { this.observer = observer; }

    public String getPort() { return port.getText(); }

    public void reset() { port.clear(); }
}
