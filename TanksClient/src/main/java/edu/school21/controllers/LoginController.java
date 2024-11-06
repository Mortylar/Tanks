package edu.school21.controllers;

import edu.school21.client.Client;
import edu.school21.observers.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML private Button login;
    @FXML private Button registration;
    @FXML private TextField name;
    private Client client;

    private Observable observer;

    @FXML
    public void initialize() {
        login.addEventHandler(
            MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent ev) {
                    System.out.printf("\nLogin name = %s\n", getName());
                    if (client.login(getName())) {
                        System.out.printf("\nLogin\n");
                        observer.notifyView();
                    } else {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setContentText("Loging is failed");
                        alert.showAndWait();
                    }
                }
            });

        registration.addEventHandler(
            MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent ev) {
                    System.out.printf("\n%s registered\n", getName());
                    if (client.registration(getName())) {
                        System.out.printf("\nReg\n");
                        observer.notifyView();
                    } else {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setContentText("Registration is failed");
                        alert.showAndWait();
                    }
                }
            });
    }

    public void setObserver(Observable observer) { this.observer = observer; }

    public String getName() { return name.getText(); }

    public void setClient(Client client) { this.client = client; }
}
