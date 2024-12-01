package edu.school21.view;

import edu.school21.client.Client;
import edu.school21.controllers.ConnectController;
import edu.school21.observers.Observable;
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

public class ConnectView implements Viewable {

    private static final String CONNECT_FORM = "/forms/connect.fxml";
    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 200;

    private Stage stage;
    private FXMLLoader fxmlLoader;
    private Parent root;
    private ConnectController controller;
    private Observable observer;
    private Client client;

    public ConnectView(Stage stage, Observable observer, Client client)
        throws IOException {
        this.observer = observer;
        this.client = client;
        this.stage = stage;
        this.fxmlLoader = new FXMLLoader(getClass().getResource(CONNECT_FORM));

        this.root = this.fxmlLoader.load();
        this.controller = this.fxmlLoader.getController();
    }

    @Override
    public void run() {
        controller.setObserver(new ViewObserver(this));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Connect");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void catchEvent() {
        try {
            this.client.setIpAndPort(this.controller.getIpAddress(),
                                     this.controller.getPort());
            this.stage.hide();
            observer.notifyView();
        } catch (Exception e) {
            this.controller.showWarning(e.getMessage());
        }
    }
}
