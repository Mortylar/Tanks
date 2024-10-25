package com.example.auth;

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

public class Auth extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
            new FXMLLoader(getClass().getResource("/forms/connect.fxml"));
        Parent root = fxmlLoader.load();
        AuthController contr = fxmlLoader.getController();
        Scene scene = new Scene(root, 300, 200);
        // scene.getStylesheets().add(
        //   getClass().getResource("/styles/styles.css").toExternalForm());
        stage.setTitle("Auth");
        stage.setScene(scene);
        stage.show();

        /* scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
             public void handle(KeyEvent ev) {
                 System.out.printf("\nPort = %s\n", contr.getPort());
                 System.out.printf("AAAAA");
             }
         });*/
    }

    public void run() { launch(); }
}
