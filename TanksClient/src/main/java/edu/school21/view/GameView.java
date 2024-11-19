package edu.school21.view;

import edu.school21.client.Client;
import edu.school21.controllers.GameController;
import edu.school21.exceptions.EndGameException;
import edu.school21.observers.Observable;
import edu.school21.observers.ViewObserver;
import java.io.IOException;
import java.util.List;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameView implements Viewable {

    private static final String CONNECT_FORM = "/forms/game.fxml";

    private Stage stage;
    private FXMLLoader fxmlLoader;
    private Parent root;
    private GameController controller;
    private Observable observer;
    private Client client;

    public GameView(Stage stage, Observable observer, Client client)
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
        client.setObserver(new ViewObserver(this));
        Scene scene = new Scene(root, 1024, 1024);
        stage.setTitle("Tanks");
        stage.setScene(scene);
        stage.setResizable(false);
        controller.setScene(scene);
        controller.setClient(client);

        scene.getStylesheets().add(
            getClass().getResource("/styles/styles.css").toExternalForm());
        try {
            stage.show();
            this.client.playGame();
        } catch (Exception e) {
            System.out.printf("\nAAAA\n");
        }
    }

    @Override
    public void catchEvent() {
        try {
            controller.draw();
        } catch (EndGameException e) {
            this.client.endGame();
            System.out.println(this.client.getStatisticInfo());
            System.out.println("EndGame");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
