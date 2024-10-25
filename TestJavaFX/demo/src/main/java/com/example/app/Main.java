package com.example.app;

import com.example.auth.Auth;
import com.example.game.Game;
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

public class Main {

    /*  @Override
      public void start(Stage stage) throws IOException {
          FXMLLoader fxmlLoader = new
      FXMLLoader(HelloApplication.class.getResource("/forms/game.fxml")); Parent
      root = fxmlLoader.load(); HelloController contr =
      fxmlLoader.getController(); Scene scene = new Scene(root, 1042, 1042);
          scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
          stage.setTitle("Hello!");
          stage.setScene(scene);
          stage.show();


      }*/

    public static void main(String[] args) {
        Auth auth = new Auth();
        auth.run();
    }
}
