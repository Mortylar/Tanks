package edu.school21.view;

import edu.school21.client.Client;
import edu.school21.observers.ViewObserver;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class View extends Application implements Viewable {

    private Viewable current;
    private ConnectView connect;
    private LoginView login;
    private GameView game;
    private Client client;

    { this.client = new Client(); }

    @Override
    public void start(Stage stage) throws Exception {
        connect =
            new ConnectView(new Stage(), new ViewObserver(this), this.client);
        login = new LoginView(new Stage(), new ViewObserver(this), this.client);
        game = new GameView(new Stage(), new ViewObserver(this), this.client);
        current = connect;
        current.run();
    }

    @Override
    public void run() {
        launch();
    }

    @Override
    public void catchEvent() {
        viewManager();
    }

    private void viewManager() {
        if (current == connect) {
            current = login;
        } else if (current == login) {
            current = game;
        }
        try {
            current.run();
        } catch (Exception e) {
            System.err.printf("\n%s\n", e.getMessage());
        }
    }
}
