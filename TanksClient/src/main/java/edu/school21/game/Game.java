package edu.school21.game;

import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Game extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
            new FXMLLoader(getClass().getResource("/forms/game.fxml"));
        Parent root = fxmlLoader.load();
        Controller contr = fxmlLoader.getController();
        Scene scene = new Scene(root, 1042, 1042);
        scene.getStylesheets().add(
            getClass().getResource("/styles/styles.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ev) {
                if (ev.getCharacter().equals("d")) {
                    contr.movePlayer(10);
                    System.out.printf("\na\n");
                } else if (ev.getCharacter().equals("a")) {
                    contr.movePlayer(-10);
                    System.out.printf("\nd\n");
                }
            }
        });
    }

    public void run() { launch(); }
}
