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
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class View extends Application implements Viewable {

    private static final String SERVER_ADDRESS = "localhost";

    private Viewable current;
    private ConnectView connect;
    private LoginView login;
    private Client client;

    { this.client = new Client(SERVER_ADDRESS); }

    @Override
    public void start(Stage stage) throws Exception {
        connect =
            new ConnectView(new Stage(), new ViewObserver(this), this.client);
        login = new LoginView(new Stage(), new ViewObserver(this), this.client);
        current = connect;
        current.run(); //
    }

    @Override
    public void run() {
        launch();
    }

    @Override
    public void catchEvent() {
        // System.out.printf("\nTODO\n");
        viewManager();
        System.out.printf("\nid = %d\n", client.getId());
    }

    private void viewManager() {
        if (current == connect) {
            current = login;
        } else if (current == login) {
            return;
        }
        try {
            current.run();
        } catch (Exception e) {
            System.err.printf("\n%s\n", e.getMessage());
        }
    }
}
