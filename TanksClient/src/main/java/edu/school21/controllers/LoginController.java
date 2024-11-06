package edu.school21.controllers;

import edu.school21.observers.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML private Button login;
    @FXML private Button registration;
    @FXML private TextField name;

    private Observable observer;

    @FXML
    public void initialize() {
        login.addEventHandler(MouseEvent.MOUSE_CLICKED,
                              new EventHandler<MouseEvent>() {
                                  @Override
                                  public void handle(MouseEvent ev) {
                                      observer.notifyView();
                                  }
                              });

        registration.addEventHandler(
            MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent ev) {
                    System.out.printf("\n%s registered\n", getName());
                }
            });
    }

    public void setObserver(Observable observer) { this.observer = observer; }

    public String getName() { return name.getText(); }
}
