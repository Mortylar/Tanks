package com.example.view;

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

public class View extends Application implements Viewable {

    @Override
    public void start(Stage stage) throws IOException {
        ConnectView connect = new ConnectView(stage);
        connect.run();
    }

    @Override
    public void run() {
        launch();
    }

    @Override
    public void catchEvent() {
        System.out.printf("\nTODO\n");
    }
}
