package edu.school21.controllers;

import edu.school21.observers.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        connect.addEventHandler(
            MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent ev) {
                    if (isPortValidate()) {
                        observer.notifyView();
                    } else {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setContentText("Invalid port Value");
                        alert.showAndWait();
                    }
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

    public int getPort() { return Integer.parseInt(port.getText()); }

    public void reset() { port.clear(); }

    public boolean isPortValidate() {
        final int MIN_PORT = 1024;
        final int MAX_PORT = 65535;
        String portStr = port.getText();
        if (portStr == null) {
            return false;
        }

        int number;
        try {
            number = Integer.parseInt(portStr);
            if ((number < MIN_PORT) || (number > MAX_PORT)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
