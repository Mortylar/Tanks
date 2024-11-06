package edu.school21.controllers;

import edu.school21.observers.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class GameController {

    @FXML private ImageView player;
    @FXML private ImageView enemy;

    private Observable observer;
    private Scene scene;

    @FXML
    public void initialize() {
        /*  this.scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
              public void handle(KeyEvent ev) {
                  if (ev.getCharacter().equals("a")) {
                      movePlayer(-10);
                  } else if (ev.getCharacter().equals("d")) {
                      movePlayer(10);
                  }
              }
          });*/
    }

    /*
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
                           });*/

    public void setObserver(Observable observer) { this.observer = observer; }
    public void setScene(Scene scene) {
        this.scene = scene;
        this.scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ev) {
                if (ev.getCharacter().equals("a")) {
                    movePlayer(-10);
                } else if (ev.getCharacter().equals("d")) {
                    movePlayer(10);
                }
            }
        });
    }

    public void movePlayer(int x) { player.setX(player.getX() + x); }
}
