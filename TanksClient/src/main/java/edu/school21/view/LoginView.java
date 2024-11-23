package edu.school21.view;

import edu.school21.client.Client;
import edu.school21.controllers.LoginController;
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

public class LoginView implements Viewable {

    private static final String CONNECT_FORM = "/forms/login.fxml";

    private Stage stage;
    private FXMLLoader fxmlLoader;
    private Parent root;
    private LoginController controller;
    private Observable observer;
    private Client client;

    public LoginView(Stage stage, Observable observer, Client client)
        throws IOException {
        this.client = client;
        this.observer = observer;
        this.stage = stage;
        this.fxmlLoader = new FXMLLoader(getClass().getResource(CONNECT_FORM));

        this.root = this.fxmlLoader.load();
        this.controller = this.fxmlLoader.getController();
    }

    @Override
    public void run() {
        controller.setObserver(new ViewObserver(this));
        controller.setClient(client);
        Scene scene = new Scene(root, 300, 200);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void catchEvent() {
        this.stage.hide();
        observer.notifyView();
    }
}
