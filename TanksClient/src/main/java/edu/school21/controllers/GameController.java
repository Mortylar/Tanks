package edu.school21.controllers;

import edu.school21.client.Client;
import edu.school21.observers.Observable;
import edu.school21.state.StateManager;
import edu.school21.state.bullet.Bullet;
import edu.school21.state.player.Player;
import edu.school21.state.position.Position;
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
    private Client client;

    @FXML
    public void initialize() {}

    public void setObserver(Observable observer) { this.observer = observer; }

    public void setClient(Client client) { this.client = client; }

    public void setScene(Scene scene) {
        this.scene = scene;
        this.scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ev) {
                client.setAction(ev.getCharacter());
            }
        });
    }

    public void draw() {
        StateManager state = this.client.getStateManager();
        // System.out.printf("\n\nstate = %s\n\n", state);
        Player playerState = state.getPlayer(this.client.getId());
        this.player.setX(playerState.getPosition().x() -
                         StateManager.START_X_POSITION);
        Player enemyState = state.getEnemy(this.client.getId());
        this.enemy.setX(enemyState.getPosition().x() -
                        StateManager.START_X_POSITION);
    }
}
