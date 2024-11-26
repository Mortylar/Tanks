package edu.school21.controllers;

import edu.school21.client.Client;
import edu.school21.exceptions.EndGameException;
import edu.school21.observers.Observable;
import edu.school21.state.StateManager;
import edu.school21.state.bullet.Bullet;
import edu.school21.state.player.Player;
import edu.school21.state.position.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameController {

    private static final int BASE_ELEMENT_COUNT = 4;
    private static final int MAX_BULLETS_COUNT = 50;
    private static final int BASE_BULLET_COORD = 1042;
    private static final float XP_DIVIDER = 100F;
    private static final int X_BULLET_OFFSET = 70;
    private static final int Y_BULLET_OFFSET = 1024;

    @FXML private ImageView player;
    @FXML private ImageView enemy;
    @FXML private ProgressBar playerXP;
    @FXML private ProgressBar enemyXP;
    @FXML private Pane rootBox;

    private Observable observer;
    private Scene scene;
    private Image playerBulletImage;
    private Image enemyBulletImage;
    private Image diedImage;
    private ArrayList<ImageView> playerBullets;
    private ArrayList<ImageView> enemyBullets;
    private Client client;

    @FXML
    public void initialize() {
        playerBulletImage = new Image("/textures/playerBullet.png");
        enemyBulletImage = new Image("/textures/enemyBullet.png");
        diedImage = new Image("/textures/fail.png");
        playerBullets = new ArrayList<ImageView>(MAX_BULLETS_COUNT);
        enemyBullets = new ArrayList<ImageView>(MAX_BULLETS_COUNT);
        for (int i = 0; i < MAX_BULLETS_COUNT; ++i) {
            playerBullets.add(i, createBullet(playerBulletImage));
            enemyBullets.add(i, createBullet(enemyBulletImage));
            rootBox.getChildren().add(playerBullets.get(i));
            rootBox.getChildren().add(enemyBullets.get(i));
        }
    }

    private ImageView createBullet(Image image) {
        ImageView tmp = new ImageView();
        tmp.setImage(image);
        tmp.setX(BASE_BULLET_COORD);
        tmp.setY(BASE_BULLET_COORD);
        return tmp;
    }

    public void setObserver(Observable observer) { this.observer = observer; }

    public void setClient(Client client) { this.client = client; }

    public void setScene(Scene scene) {
        this.scene = scene;
        this.scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ev) {
                String ch = ev.getCharacter();
                if (ch.equals("a") || ch.equals("d")) {
                    client.setAction(ev.getCharacter());
                }
                if (client.isEndGame()) {
                    client.updateStatistic();
                    viewInfo(client.getStatisticInfo());
                }
            }
        });

        this.scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ev) {
                if (ev.getCode().getName().equals("Space")) {
                    client.setAction(" ");
                }
            }
        });
    }

    public void draw() throws EndGameException {
        StateManager state = this.client.getStateManager();
        Player playerState = state.getPlayer(this.client.getId());
        setXP();
        this.player.setX(playerState.getPosition().x() -
                         StateManager.START_X_POSITION);

        drawPlayerBullets(state.getPlayer(this.client.getId()).getBullets());

        Player enemyState = state.getEnemy(this.client.getId());
        this.enemy.setX(enemyState.getPosition().x() -
                        StateManager.START_X_POSITION);
        drawEnemyBullets(state.getEnemy(this.client.getId()).getBullets());
    }

    private void drawPlayerBullets(ArrayDeque<Bullet> bullets) {
        for (int i = 0; i < MAX_BULLETS_COUNT; ++i) {
            for (Bullet cur : bullets) {
                playerBullets.get(i).setX(X_BULLET_OFFSET +
                                          cur.getPosition().x());
                playerBullets.get(i).setY(Y_BULLET_OFFSET -
                                          cur.getPosition().y());
                ++i;
                if (i >= MAX_BULLETS_COUNT) {
                    break;
                }
            }
            if (i < MAX_BULLETS_COUNT) {
                playerBullets.get(i).setX(BASE_BULLET_COORD);
                playerBullets.get(i).setY(BASE_BULLET_COORD);
            }
        }
    }

    private void drawEnemyBullets(ArrayDeque<Bullet> bullets) {
        for (int i = 0; i < MAX_BULLETS_COUNT; ++i) {
            for (Bullet cur : bullets) {
                enemyBullets.get(i).setX(X_BULLET_OFFSET +
                                         cur.getPosition().x());
                enemyBullets.get(i).setY(cur.getPosition().y());
                ++i;
                if (i >= MAX_BULLETS_COUNT) {
                    break;
                }
            }
            if (i < MAX_BULLETS_COUNT) {
                enemyBullets.get(i).setX(BASE_BULLET_COORD);
                enemyBullets.get(i).setY(BASE_BULLET_COORD);
            }
        }
    }

    private void setXP() throws EndGameException {
        StateManager state = this.client.getStateManager();
        int xp = state.getPlayer(this.client.getId()).getXP();
        checkXP(xp);
        this.playerXP.setProgress(xp / XP_DIVIDER);
        xp = state.getEnemy(this.client.getId()).getXP();
        checkXP(xp);
        this.enemyXP.setProgress(xp / XP_DIVIDER);
    }

    private void checkXP(int xp) throws EndGameException {
        if (xp <= 0) {
            throw new EndGameException();
        }
    }

    public void drawDiedPlayer() {
        StateManager state = this.client.getStateManager();
        int xp = state.getPlayer(this.client.getId()).getXP();
        if (xp <= 0) {
            this.player.setImage(this.diedImage);
        }
        xp = state.getEnemy(this.client.getId()).getXP();
        if (xp <= 0) {
            this.enemy.setImage(this.diedImage);
        }
        for (int i = 0; i < MAX_BULLETS_COUNT; ++i) {
            this.enemyBullets.get(i).setX(BASE_BULLET_COORD);
            this.playerBullets.get(i).setX(BASE_BULLET_COORD);
        }
    }

    public void viewInfo(String info) {
        ButtonType exit = new ButtonType("EXIT");
        Alert alert = new Alert(AlertType.INFORMATION, "Statistic", exit);
        alert.setContentText(info);
        alert.setTitle("Game Statistic");
        alert.setHeaderText(getEndGameMessage());
        alert.showAndWait()
            .filter(responce -> responce == exit)
            .ifPresent(responce -> ((Stage)this.scene.getWindow()).close());
    }

    private String getEndGameMessage() {
        String message = String.format("Player %s, ", this.client.getName());
        if (this.client.getStateManager()
                .getPlayer(this.client.getId())
                .getXP() <= 0) {
            message += "Congratulation - you lose!!";
            return message;
        }
        return message + "Sorry, but you win(";
    }
}
