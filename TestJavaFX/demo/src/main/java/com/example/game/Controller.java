package com.example.game;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class Controller {

    @FXML private ImageView player;
    @FXML private ImageView enemy;

    public void movePlayer(int x) { player.setX(player.getX() + x); }
}
